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
package org.seagrid.desktop.ui.storage;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.seagrid.desktop.ui.storage.controller.MassStorageBrowserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MassStorageBrowserWindow extends Application{
    private final static Logger logger = LoggerFactory.getLogger(MassStorageBrowserWindow.class);

    private static Stage primaryStage;
    private static MassStorageBrowserController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/storage/mass-storage-browser.fxml"));
        primaryStage.setTitle("SEAGrid Desktop Client - Storage Browser");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void displayFileBrowse(String path) throws IOException, SftpException, JSchException {
        if(primaryStage == null || !primaryStage.isShowing()) {
            primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader(MassStorageBrowserWindow.class.getResource(
                    "/views/storage/mass-storage-browser.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            primaryStage.setTitle("SEAGrid Desktop Client - Storage Browser");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.initModality(Modality.WINDOW_MODAL);
            primaryStage.show();
        }

        if(path != null){
            if(!path.startsWith("/")){
                path = "/" + path;
            }
            controller.gotoRemoteDir(path);
        }
        primaryStage.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}