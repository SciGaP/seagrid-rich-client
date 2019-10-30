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
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.airavata.model.appcatalog.appdeployment.ApplicationModule;
import org.apache.airavata.model.workspace.Notification;
import org.apache.thrift.TException;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.util.SEAGridConfig;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.*;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginController {
    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    public Label notificationLabel;


    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink dontHaveAccountLink;

    @FXML
    private WebView loginWebView;

    //Dummy class used for storing notification list index
    private class Index{
        int index;
    }

    public void initialize() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        try{
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        loginButton.setOnMouseClicked(event -> {
            handleSEAGridWebLogin();
            loginButton.resizeRelocate( 100.0,100.0,50.0,50.0 );
        });

        dontHaveAccountLink.setOnAction(event -> {
            try {
                String url;
                if(SEAGridConfig.DEV){
                    url = "https://dev.seagrid.org/create";
                }else{
                    url = "https://seagrid.org/create";
                }
                Desktop.getDesktop().browse(new URI(url));
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

        handleSEAGridWebLogin();



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

    //initializing notification messages
        notificationLabel.setCursor(Cursor.HAND);
        notificationLabel.setStyle("-fx-border-color: white;");
        notificationLabel.setMaxWidth(Double.MAX_VALUE);
}
    public boolean handleSEAGridWebLogin(){

        //loginWebView.getEngine().loadContent(textinfo1 + textinfo2 + textinfo3 + textinfo4);
        final WebEngine webEngine = loginWebView.getEngine();
        final Label location = new Label();
        String url;
        if(SEAGridConfig.DEV){
            url = "https://dev.seagrid.org/login-desktop";
        }else{
            url = "https://seagrid.org/login-desktop";
        }


        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED.equals(newValue)) {
                String locationUrl = webEngine.getLocation();
                location.setText(locationUrl);
                Map<String, String> params = getQueryMap(locationUrl);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                if(params.containsKey("status")){
                    if(params.get("status").equals("ok")){
                        //login successful
                        String token = params.get("code");
                        String refreshToken = params.get("refresh_code");
                        int validTime = Integer.parseInt(params.get("valid_time").trim());
                        String userName = params.get("username");
                        SEAGridContext.getInstance().setAuthenticated(true);
                        SEAGridContext.getInstance().setOAuthToken(token);
                        SEAGridContext.getInstance().setRefreshToken(refreshToken);
                        SEAGridContext.getInstance().setTokenExpiaryTime(validTime);
                        SEAGridContext.getInstance().setUserName(userName);
                        try {
                            List<ApplicationModule> appModules = AiravataManager.getInstance().getAccessibleAppModules();
                            if (!appModules.isEmpty()) {
                                stage.close();
                            } else {
                                java.net.CookieHandler.setDefault(new com.sun.webkit.network.CookieManager());
                                webEngine.load(url);
                                loginWebView.setVisible(false);
                                //loginWebView.setVisible(true);
                                SEAGridDialogHelper.showInformationDialog("Login Failed", "Unauthorized login",
                                        "You don't have permission to access this client." +
                                                " Please contact the Gateway Admin to get your account authorized by sending an" +
                                                " email to help@seagrid.org.", stage);
                                loginWebView.setVisible(true);
                            }
                        } catch (TException e) {

                            logger.error("Failed to check accessible app modules for user", e);
                            java.net.CookieHandler.setDefault(new com.sun.webkit.network.CookieManager());
                            webEngine.load(url);
                            loginWebView.setVisible(false);
                            SEAGridDialogHelper.showInformationDialog("Login Failed", "Error occurred",
                                    "An error occurred while trying to check your access: " + e.getMessage(), stage);
                            loginWebView.setVisible(true);
                        }
                    }else{
                        //login failed
                        java.net.CookieHandler.setDefault(new com.sun.webkit.network.CookieManager());
                        webEngine.load(url);
                        loginWebView.setVisible(false);
                        SEAGridDialogHelper.showInformationDialog("Login Failed", "Unauthorized login",
                                "You don't have permission to access this client." +
                                " Please use a correct user credentials and try again.", stage);
                        loginButton.resizeRelocate( 100.0,100.0,50.0,50.0 );
                        loginWebView.setVisible(true);
                    }
                }
            }
        });

        //loginWebView.getEngine().loadContent(textinfo1 + textinfo2 + textinfo3 + textinfo4);
        webEngine.load(url);
        return false;
    }


    private Map<String, String> getQueryMap(String query)
    {
        Map<String, String> map = new HashMap<>();
        if(query.contains("?")) {
            String[] params = query.split("\\?")[1].split("&");
            for (String param : params) {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                map.put(name, value);
            }
        }
        return map;
    }

}
