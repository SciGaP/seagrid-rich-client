/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package org.seagrid.desktop;

import com.google.common.eventbus.Subscribe;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.ui.home.HomeWindow;
import org.seagrid.desktop.ui.login.LoginWindow;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SEAGridDesktop extends Application{
    private static Logger logger;

    public SEAGridDesktop(){
        SEAGridEventBus.getInstance().register(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initApplicationDirs();
        File dataDir = new File(applicationDataDir());
        if(dataDir.exists()) {
            LoginWindow loginWindow =  new LoginWindow();
            loginWindow.displayLoginAndWait();
            boolean isAuthenticated = SEAGridContext.getInstance().getAuthenticated();
            if (isAuthenticated) {
                HomeWindow homeWindow = new HomeWindow();
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                primaryStage.setX(bounds.getMinX());
                primaryStage.setY(bounds.getMinY());
                primaryStage.setWidth(bounds.getWidth());
                primaryStage.setHeight(bounds.getHeight());
                homeWindow.start(primaryStage);
                primaryStage.setOnCloseRequest(t -> {
                    Platform.exit();
                    System.exit(0);
                });
            }
        }else{
            SEAGridDialogHelper.showExceptionDialogAndWait(new Exception("Application Data Dir Does Not Exists"),
                    "Application Data Dir Does Not Exists", null, "Application Data Dir Does Not Exists");
            System.exit(0);
        }
    }

    @Subscribe
    public void handleSEAGridEvents(SEAGridEvent event){
        if(event.getEventType().equals(SEAGridEvent.SEAGridEventType.LOGOUT)){
            try {
                start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        initApplicationDirs();
        launch(args);
    }

    public static void initApplicationDirs() throws IOException {
        createTrustStoreFileIfNotExists();
        File appDataRoot = new File(applicationDataDir());
        if(!appDataRoot.exists()){
            appDataRoot.mkdirs();
        }
        if(!appDataRoot.canWrite()){
            SEAGridDialogHelper.showExceptionDialogAndWait(new Exception("Cannot Write to Application Data Dir"),
                    "Cannot Write to Application Data Dir", null, "Cannot Write to Application Data Dir " + applicationDataDir());
        }

        //Legacy editors use stdout and stderr instead of loggers. This is a workaround to append them to a file
        System.setProperty("app.data.dir", applicationDataDir() + "logs");
        logger = LoggerFactory.getLogger(SEAGridDesktop.class);
        File logParent = new File(applicationDataDir() + "logs");
        if(!logParent.exists())
            logParent.mkdirs();
        PrintStream outPs = new PrintStream(applicationDataDir() + "logs/seagrid.std.out");
        PrintStream errPs = new PrintStream(applicationDataDir() + "logs/seagrid.std.err");
        System.setOut(outPs);
        System.setErr(errPs);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                outPs.close();
                errPs.close();
                SEAGridContext.getInstance().saveUserPrefs();
            }
        });

        extractLegacyEditorResources();
    }

    public static void createTrustStoreFileIfNotExists() throws IOException {
        File parentDir = new File(applicationDataDir());
        if(!parentDir.exists())
            parentDir.mkdirs();
        File targetFile = new File(applicationDataDir() + "client_truststore.jks");
        if(!targetFile.exists()) {
            InputStream initialStream = SEAGridContext.class.getResourceAsStream("/client_truststore.jks");
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
        }
    }

    public static void extractLegacyEditorResources() {
        try {
            String destParent = applicationDataDir() + ".ApplicationData" + File.separator;
            File appHome = new File(destParent);
            if(!appHome.exists()){
                if(!appHome.mkdirs()){
                    SEAGridDialogHelper.showExceptionDialogAndWait(new Exception("Cannot Create Application Data Dir"),
                            "Cannot Create Application Data Dir", null, "Cannot Create Application Data Dir");
                }
            }
            byte[] buf = new byte[1024];
            ZipInputStream zipinputstream;
            ZipEntry zipentry;
            zipinputstream = new ZipInputStream(SEAGridDesktop.class.getClassLoader().getResourceAsStream("legacy.editors.zip"));

            zipentry = zipinputstream.getNextEntry();
            if(zipentry == null){
                SEAGridDialogHelper.showExceptionDialogAndWait(new Exception("Cannot Read Application Resources"),
                        "Cannot Read Application Resources", null, "Cannot Read Application Resources");
            }else {
                while (zipentry != null) {
                    //for each entry to be extracted
                    String entryName = destParent + zipentry.getName();
                    entryName = entryName.replace('/', File.separatorChar);
                    entryName = entryName.replace('\\', File.separatorChar);
                    logger.info("entryname " + entryName);
                    int n;
                    FileOutputStream fileoutputstream;
                    File newFile = new File(entryName);
                    if (zipentry.isDirectory()) {
                        if (!newFile.mkdirs()) {
                            break;
                        }
                        zipentry = zipinputstream.getNextEntry();
                        continue;
                    }
                    fileoutputstream = new FileOutputStream(entryName);
                    while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
                        fileoutputstream.write(buf, 0, n);
                    }
                    fileoutputstream.close();
                    zipinputstream.closeEntry();
                    zipentry = zipinputstream.getNextEntry();
                }
                zipinputstream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    private static String applicationDataDir()
    {
        return System.getProperty("user.home") + File.separator + "SEAGrid" + File.separator;
    }
}