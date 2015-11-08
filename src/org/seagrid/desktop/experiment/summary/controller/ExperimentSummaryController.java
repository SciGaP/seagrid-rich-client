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
package org.seagrid.desktop.experiment.summary.controller;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.seagrid.desktop.apis.airavata.AiravataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperimentSummaryController {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentSummaryController.class);

    @FXML
    private Label experimentIdLabel;

    @FXML
    private Label experimentNameLabel;

    @FXML
    private Label experimentProjectLabel;

    @FXML
    private Label experimentDescField;

    @FXML
    private Label experimentApplicationLabel;

    @FXML
    private Label experimentCRLabel;

    @FXML
    private Label experimentJobStatusLabel;

    @FXML
    private Label experimentStatusLabel;

    @FXML
    private Label experimentCreationTimeLabel;

    @FXML
    private Label experimentLastModifiedTimeLabel;

    @FXML
    private Label experimentEnableAutoSchedLabel;

    @FXML
    private Label experimentWallTimeLabel;

    @FXML
    private Label experimentCPUCountLabel;

    @FXML
    private Label experimentQueueLabel;

    @FXML
    private Label experimentNodeCountLabel;

    @FXML
    private GridPane experimentInfoGridPane;

    public void initialize() {
    }

    public void updateExperimentInfo(String experimentId){
        try {
            ExperimentModel experimentModel = AiravataManager.getInstance().getExperiment(experimentId);
            if(experimentModel != null){
                experimentIdLabel.setText(experimentModel.getExperimentId());
                experimentNameLabel.setText(experimentModel.getExperimentName());
                experimentDescField.setText(experimentModel.getDescription());
                experimentProjectLabel.setText(experimentModel.getProjectId());
                experimentApplicationLabel.setText(experimentModel.getExecutionId());
                experimentCRLabel.setText(experimentModel.getUserConfigurationData()
                        .getComputationalResourceScheduling().getResourceHostId());
                experimentJobStatusLabel.setText("JOB-STATUS");
                experimentStatusLabel.setText(experimentModel.getExperimentStatus().getState().toString());
                experimentCreationTimeLabel.setText(experimentModel.getCreationTime()+"");
                experimentLastModifiedTimeLabel.setText(experimentModel.getCreationTime()+"");
                experimentEnableAutoSchedLabel.setText("true");
                experimentWallTimeLabel.setText(experimentModel.getUserConfigurationData()
                        .getComputationalResourceScheduling().getWallTimeLimit()+"");
                experimentCPUCountLabel.setText(experimentModel.getUserConfigurationData()
                        .getComputationalResourceScheduling().getTotalCPUCount()+"");
                experimentQueueLabel.setText(experimentModel.getUserConfigurationData()
                        .getComputationalResourceScheduling().getQueueName());
                experimentNodeCountLabel.setText(experimentModel.getUserConfigurationData()
                        .getComputationalResourceScheduling().getNodeCount()+"");
            }
        } catch (AiravataClientException e) {
            e.printStackTrace();
        }
    }
}