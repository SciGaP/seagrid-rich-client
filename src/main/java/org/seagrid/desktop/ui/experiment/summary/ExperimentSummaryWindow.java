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
package org.seagrid.desktop.ui.experiment.summary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.seagrid.desktop.ui.experiment.summary.controller.ExperimentSummaryController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ExperimentSummaryWindow extends Application {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentSummaryWindow.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/experiment-summary.fxml"));
        primaryStage.setTitle("SEAGrid Desktop Client - Experiment Summary");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public Parent getExperimentInfoNode(String experimentId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "view/experiment-summary.fxml"));
        Parent node = loader.load();
        ExperimentSummaryController controller = loader.getController();
        controller.initExperimentInfo(experimentId);
        return node;
    }
}