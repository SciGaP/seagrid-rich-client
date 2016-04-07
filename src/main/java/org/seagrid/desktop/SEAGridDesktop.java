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
import org.seagrid.desktop.ui.home.HomeWindow;
import org.seagrid.desktop.ui.login.LoginWindow;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SEAGridDesktop extends Application{
    private final static Logger logger = LoggerFactory.getLogger(SEAGridDesktop.class);

    public SEAGridDesktop(){
        SEAGridEventBus.getInstance().register(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        LoginWindow loginWindow =  new LoginWindow();
        loginWindow.displayLoginAndWait();
        boolean isAuthenticated = SEAGridContext.getInstance().getAuthenticated();
        if(isAuthenticated){
            HomeWindow homeWindow =  new HomeWindow();
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
        //Legacy editors use stdout and stderr instead of loggers. This is a workaround to append them to a file
        PrintStream outPs = new PrintStream("./logs/seagrid.std.out");
        PrintStream errPs = new PrintStream("./logs/seagrid.std.err");
        System.setOut(outPs);
        System.setErr(errPs);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                outPs.close();
                errPs.close();
            }
        });

        extractLegacyEditorResources();
        launch(args);
    }

    public static void extractLegacyEditorResources() {
        try {
            String destParent = defaultDataDirectory();
            byte[] buf = new byte[1024];
            ZipInputStream zipinputstream;
            ZipEntry zipentry;
            zipinputstream = new ZipInputStream(SEAGridDesktop.class.getClassLoader().getResourceAsStream("legacy.editors.zip"));

            zipentry = zipinputstream.getNextEntry();
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
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static String defaultDataDirectory()
    {
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN"))
            return System.getenv("APPDATA") + "/SEAGrid/";
        else if (OS.contains("MAC"))
            return System.getProperty("user.home") + "/Library/Application "
                    + "Support" + "/SEAGrid/";
        else if (OS.contains("NUX"))
            return System.getProperty("user.home") + "/.seagrid/";
        return System.getProperty("user.dir") + "/SEAGrid/";
    }
}