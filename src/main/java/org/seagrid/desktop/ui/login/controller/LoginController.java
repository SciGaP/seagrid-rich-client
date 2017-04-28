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
package org.seagrid.desktop.ui.login.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.airavata.model.error.AuthorizationException;
import org.apache.airavata.model.workspace.Notification;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.connectors.wso2is.AuthResponse;
import org.seagrid.desktop.connectors.wso2is.AuthenticationException;
import org.seagrid.desktop.connectors.wso2is.AuthenticationManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.UserPrefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;


public class LoginController {
    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    public Label notificationLabel;

    @FXML
    public RadioButton rememberMe;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginAuthFailed;

    @FXML
    private Hyperlink dontHaveAccountLink;

    @FXML
    private WebView loginWebView;

    //Dummy class used for storing notification list index
    private class Index{
        int index;
    }

    public void initialize() {
        loginButton.disableProperty().bind(new BooleanBinding() {
            {super.bind(passwordField.textProperty(),usernameField.textProperty());}
            @Override
            protected boolean computeValue() {
                return usernameField.getText().isEmpty() || passwordField.getText().isEmpty();
            }
        });
        loginButton.setOnMouseClicked(event -> {
            handleLogin();
        });
        passwordField.setOnAction(event->{if(!usernameField.getText().isEmpty() && !passwordField.getText().isEmpty())
            handleLogin();});
        usernameField.setOnAction(event->{if(!usernameField.getText().isEmpty() && !passwordField.getText().isEmpty())
            handleLogin();});

        UserPrefs userPrefs = SEAGridContext.getInstance().getUserPrefs();
        if(userPrefs != null){
            usernameField.setText(userPrefs.getUserName());
            if(userPrefs.isRememberPassword()){
                rememberMe.setSelected(true);
                passwordField.setText(userPrefs.getPassword());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        passwordField.requestFocus();
                        passwordField.end();
                    }
                });

            }
        }

        dontHaveAccountLink.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://seagrid.org/create"));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        String imgtext = "<img src=\"data:image/png;base64," + SEAGridContext.logoBase64 + "\" height=50 width=50>";
        String textinfo1 = "<div style=\"background-color:#E7EEF6; color:#000000\">" +
                "<div style=\"background-color:#A7B3C7; color:#FFFFFF;\">" +
                imgtext + "<font size=5> Welcome to SEAGrid !! - Science and Engineering Applications Grid" +
                "</font>" +
                "<br></div>" +
                "<p>You are running the " +
                "<Font color='green'>SEAGrid Desktop Client </font>" +
                "Application. </p>";
        String textinfo2 = "<p>To use Web Portal and for more information, " +
                " visit <a href='https://seagrid.org/'>https://seagrid.org/</a></div></p>";
        String textinfo3 = "<p>If you do not have SEAGrid account, you may request one on the web portal." +
                "</div></p>";

        String textinfo4 = "<br><p><Font color='red'>Note: This version is in active development and will" +
                " be auto-updated automatically.</font></p>";

        loginWebView.getEngine().loadContent(textinfo1 + textinfo2 + textinfo3 + textinfo4);

        //initializing notification messages
        notificationLabel.setCursor(Cursor.HAND);
        notificationLabel.setStyle("-fx-border-color: white;");
        notificationLabel.setMaxWidth(Double.MAX_VALUE);
        try{
            java.util.List<Notification> messages = AiravataManager.getInstance().getNotifications();
            final Index index = new Index();
            index.index = 0;
            if (messages != null && messages.size() > 0) {
                notificationLabel.setText(messages.get(index.index).getTitle() + " : "
                        + messages.get(index.index).getNotificationMessage().split("\r|\n")[0]);
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0),
                        event -> {
                            index.index++;
                            index.index = index.index % messages.size();
                            switch (messages.get(index.index).getPriority()){
                                case HIGH:
                                    notificationLabel.setTextFill(Color.web("#ff0000"));
                                    break;
                                case NORMAL:
                                    notificationLabel.setTextFill(Color.web("#ffa500"));
                                    break;
                                case LOW:
                                    notificationLabel.setTextFill(Color.web("#808080"));
                                    break;
                            }
                            notificationLabel.setText(messages.get(index.index).getTitle() + " : "
                                    + messages.get(index.index).getNotificationMessage().split("\r|\n")[0]);

                            notificationLabel.setOnMouseClicked(event1 -> {
                                SEAGridDialogHelper.showInformationDialog("Notification", messages.get(index.index).getTitle(),
                                        messages.get(index.index).getNotificationMessage(), null);
                            });
                        }),
                        new KeyFrame(Duration.seconds(5)));
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
            }
        }catch (Exception ex){
            //cannot connect to Airavata
            ex.printStackTrace();
        }
    }

    public boolean handleLogin(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        AuthenticationManager authenticationManager = new AuthenticationManager();
        try {
            AuthResponse authResponse = authenticationManager.authenticate(username,password);
            if(authResponse != null){
                SEAGridContext.getInstance().setAuthenticated(true);
                SEAGridContext.getInstance().setUserName(username);
                SEAGridContext.getInstance().setOAuthToken(authResponse.getAccess_token());
                SEAGridContext.getInstance().setRefreshToken(authResponse.getRefresh_token());
                SEAGridContext.getInstance().setTokenExpiaryTime(authResponse.getExpires_in() * 1000
                        + System.currentTimeMillis());
                Stage stage = (Stage) loginButton.getScene().getWindow();

                UserPrefs userPrefs = SEAGridContext.getInstance().getUserPrefs();
                userPrefs.setUserName(username);
                userPrefs.setPassword(password);
                userPrefs.setRememberPassword(rememberMe.isSelected());

                stage.close();
            }else{
                loginAuthFailed.setText("Authentication Failed !");
                loginAuthFailed.setFont(new Font(10));
                loginAuthFailed.setTextFill(Color.RED);
                passwordField.setText("");
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
            SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", loginButton.getScene().getWindow(),
                    "Login operation failed !");
        } catch (AuthorizationException e){
            loginAuthFailed.setText("Your account is not yet approved by the Admin !");
            loginAuthFailed.setFont(new Font(10));
            loginAuthFailed.setTextFill(Color.RED);
            passwordField.setText("");
        }
        return false;
    }

}