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
package org.seagrid.desktop.ui.experiment.create;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.thrift.TException;
import org.seagrid.desktop.ui.experiment.create.controller.ExperimentCreateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public class ExperimentCreateWindow extends Application{
    private final static Logger logger = LoggerFactory.getLogger(ExperimentCreateWindow.class);

    private static Stage createPrimaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/experiment/create/experiment-create.fxml"));
        primaryStage.setTitle("SEAGrid Desktop Client - Create Experiment");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void displayCreateExperiment() throws IOException {
        if(createPrimaryStage == null || !createPrimaryStage.isShowing()) {
            createPrimaryStage = new Stage();
            Parent root = FXMLLoader.load(ExperimentCreateWindow.class.getResource("/views/experiment/create/experiment-create.fxml"));
            createPrimaryStage.setTitle("SEAGrid Desktop Client - Create Experiment");
            createPrimaryStage.setScene(new Scene(root, 800, 600));
            createPrimaryStage.initModality(Modality.WINDOW_MODAL);
            createPrimaryStage.show();
        }
        createPrimaryStage.requestFocus();
    }

    public static void displayEditExperiment(ExperimentModel experimentModel) throws IOException, TException, URISyntaxException {
        if(createPrimaryStage != null) {
            createPrimaryStage.close();
        }
        createPrimaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(ExperimentCreateWindow.class.getResource(
                "/views/experiment/create/experiment-create.fxml"));
        Parent root = loader.load();
        createPrimaryStage.setTitle("SEAGrid Desktop Client - Edit Experiment");
        createPrimaryStage.setScene(new Scene(root, 800, 600));
        ExperimentCreateController controller = loader.getController();
        controller.initExperimentEdit(experimentModel);
        createPrimaryStage.initModality(Modality.WINDOW_MODAL);
        createPrimaryStage.show();
    }

    public static void displayCreateGaussianExp(String gaussianInput) throws IOException, TException, URISyntaxException {
        if(createPrimaryStage != null) {
            createPrimaryStage.close();
        }
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(ExperimentCreateWindow.class.getResource(
                "/views/experiment/create/experiment-create.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("SEAGrid Desktop Client - Create Gaussian Experiment");
        primaryStage.setScene(new Scene(root, 800, 600));
        ExperimentCreateController controller = loader.getController();
        controller.initGaussianExperiment(gaussianInput);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }

    public static void displayCreateGamessExp(String gamessInput) throws IOException, TException, URISyntaxException {
        if(createPrimaryStage != null) {
            createPrimaryStage.close();
        }
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(ExperimentCreateWindow.class.getResource(
                "/views/experiment/create/experiment-create.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("SEAGrid Desktop Client - Create Gamess Experiment");
        primaryStage.setScene(new Scene(root, 800, 600));
        ExperimentCreateController controller = loader.getController();
        controller.initGamessExperiment(gamessInput);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }

    public static void displayCreateNwchemExp(String nwchemInput) throws IOException, TException, URISyntaxException {
        if(createPrimaryStage != null) {
            createPrimaryStage.close();
        }
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(ExperimentCreateWindow.class.getResource(
                "/views/experiment/create/experiment-create.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("SEAGrid Desktop Client - Create NWChem Experiment");
        primaryStage.setScene(new Scene(root, 800, 600));
        ExperimentCreateController controller = loader.getController();
        controller.initNwchemExperiment(nwchemInput);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }

    public static void displayCreatePsi4Exp(String processors, String psi4Input) throws IOException, TException, URISyntaxException {
        if(createPrimaryStage != null) {
            createPrimaryStage.close();
        }
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(ExperimentCreateWindow.class.getResource(
                "/views/experiment/create/experiment-create.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("SEAGrid Desktop Client - Create PSI4 Experiment");
        primaryStage.setScene(new Scene(root, 800, 600));
        ExperimentCreateController controller = loader.getController();
        controller.initPsi4Experiment(processors, psi4Input);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }

    public static void displayCreateMolcasExp(String processors, String molcasInput) throws IOException, TException, URISyntaxException {
        if(createPrimaryStage != null) {
            createPrimaryStage.close();
        }
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(ExperimentCreateWindow.class.getResource(
                "/views/experiment/create/experiment-create.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("SEAGrid Desktop Client - Create Molcas Experiment");
        primaryStage.setScene(new Scene(root, 800, 600));
        ExperimentCreateController controller = loader.getController();
        controller.initMolcasExperiment(processors, molcasInput);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}