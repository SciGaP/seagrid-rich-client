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
package org.seagrid.desktop.ui.commons;

import javafx.concurrent.Service;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.ExceptionDialog;
import org.controlsfx.dialog.ProgressDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SEAGridDialogHelper {
    private final static Logger logger = LoggerFactory.getLogger(SEAGridDialogHelper.class);

    public static void showProgressDialog(Service service, String title, Window parentWindow, String message){
        ProgressDialog progressDialog = new ProgressDialog(service);
        progressDialog.setTitle(title);
        progressDialog.initOwner(parentWindow);
        progressDialog.setHeaderText(message);
        ButtonBar buttonBar = (ButtonBar)progressDialog.getDialogPane().getChildren().get(2);
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> {
            if(showConfirmDialog("Confirm Action", "Do you want cancel", "The transferred data will be lost"))
            service.cancel();
        });
        buttonBar.getButtons().add(cancelButton);
        progressDialog.initModality(Modality.WINDOW_MODAL);
    }

    public static void showExceptionDialogAndWait(Throwable e, String title, Window parentWindow, String message){
        ExceptionDialog exceptionDialog = new ExceptionDialog(e);
        exceptionDialog.setTitle(title);
        exceptionDialog.initOwner(parentWindow);
        exceptionDialog.setHeaderText(message);
        exceptionDialog.initModality(Modality.WINDOW_MODAL);
        exceptionDialog.getDialogPane().setMaxWidth(800);
        exceptionDialog.showAndWait();
    }

    public static void showExceptionDialog(Throwable e, String title, Window parentWindow, String message){
        ExceptionDialog exceptionDialog = new ExceptionDialog(e);
        exceptionDialog.setTitle(title);
        exceptionDialog.initOwner(parentWindow);
        exceptionDialog.setHeaderText(message);
        exceptionDialog.initModality(Modality.WINDOW_MODAL);
        exceptionDialog.getDialogPane().setMaxWidth(800);
        exceptionDialog.show();
    }

    public static void showWarningDialog(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void showInformationNotification(String title, String message, Window parentWindow) {
        Notifications notification = Notifications.create();
        notification.hideAfter(new Duration(3000));
        notification.position(Pos.TOP_RIGHT);
        notification.owner(parentWindow);
        notification.text(message);
        notification.title(title);
        notification.showInformation();
    }

    public static boolean showConfirmDialog(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    public static void showInformationDialog(String title, String headerText, String contentText, Window parent){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.initOwner(parent);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void showInformationDialog(String title, String headerText, WebView webView, Window parent){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.initOwner(parent);
        alert.getDialogPane().setContent(webView);
        alert.showAndWait();
    }
}