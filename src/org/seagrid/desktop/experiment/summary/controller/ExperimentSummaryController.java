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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.airavata.model.appcatalog.appinterface.ApplicationInterfaceDescription;
import org.apache.airavata.model.appcatalog.computeresource.ComputeResourceDescription;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.status.JobStatus;
import org.apache.airavata.model.workspace.Project;
import org.seagrid.desktop.apis.airavata.AiravataManager;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;

public class ExperimentSummaryController {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentSummaryController.class);

    private static final double EXPERIMENT_UPDATE_INTERVAL = 3000;

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

    private Timeline expListUpdateTimeline = null;

    public void initialize() {
    }

    public void initExperimentInfo(String experimentId){
        try {
            ExperimentModel experimentModel = AiravataManager.getInstance().getExperiment(experimentId);
            if(experimentModel != null){
                experimentIdLabel.setText(experimentModel.getExperimentId());
                experimentNameLabel.setText(experimentModel.getExperimentName());
                experimentDescField.setText(experimentModel.getDescription());
                if(experimentModel.getProjectId() != null){
                    Project project = AiravataManager.getInstance().getProject(experimentModel.getProjectId());
                    if(project != null){
                        experimentProjectLabel.setText(project.getName());
                    }
                }
                if(experimentModel.getExecutionId() != null){
                    ApplicationInterfaceDescription app = AiravataManager.getInstance().getApplicationInterface(
                            experimentModel.getExecutionId());
                    if(app != null){
                        experimentApplicationLabel.setText(app.getApplicationName());
                    }
                }
                if(experimentModel.getUserConfigurationData().getComputationalResourceScheduling().getResourceHostId() != null){
                    ComputeResourceDescription computeResourceDescription = AiravataManager.getInstance().getComputeResource(
                            experimentModel.getUserConfigurationData().getComputationalResourceScheduling().getResourceHostId()
                    );
                    if(computeResourceDescription != null){
                        experimentCRLabel.setText(computeResourceDescription.getHostName());
                    }
                }
                Map<String, JobStatus> jobStatusMap = AiravataManager.getInstance().getJobStatuses(experimentModel.getExperimentId());
                if(jobStatusMap != null && jobStatusMap.values().size()>0){
                    JobStatus jobStatus = (JobStatus)jobStatusMap.values().toArray()[0];
                    experimentJobStatusLabel.setText(jobStatus.getJobState().toString());
                    switch (jobStatus.getJobState()){
                        case COMPLETE :
                            experimentJobStatusLabel.setTextFill(Color.GREEN);
                            break;
                        case FAILED :
                            experimentJobStatusLabel.setTextFill(Color.RED);
                            break;
                        default :
                            experimentJobStatusLabel.setTextFill(Color.ORANGE);
                    }
                }else{
                    experimentJobStatusLabel.setText("NOT-AVAILABLE");
                }
                experimentStatusLabel.setText(experimentModel.getExperimentStatus().getState().toString());
                switch (experimentModel.getExperimentStatus().getState()){
                    case COMPLETED :
                        experimentStatusLabel.setTextFill(Color.GREEN);
                        break;
                    case FAILED :
                        experimentStatusLabel.setTextFill(Color.RED);
                        break;
                    case CREATED :
                        experimentStatusLabel.setTextFill(Color.BLUE);
                        break;
                    default :
                        experimentStatusLabel.setTextFill(Color.ORANGE);
                }
                experimentCreationTimeLabel.setText(LocalDateTime.ofEpochSecond(experimentModel
                        .getCreationTime() / 1000, 0, SEAGridContext.getInstance().getTimeZoneOffset()).toString());
                experimentLastModifiedTimeLabel.setText(LocalDateTime.ofEpochSecond(experimentModel.getExperimentStatus()
                                .getTimeOfStateChange() / 1000, 0, SEAGridContext.getInstance().getTimeZoneOffset()).toString());
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

            //TODO this should replace with a RabbitMQ Listener
            if(!(experimentStatusLabel.getText().equals("FAILED") || experimentStatusLabel.getText().equals("COMPLETED")
                    || experimentStatusLabel.getText().equals("CANCELLED"))) {
                expListUpdateTimeline = new Timeline(new KeyFrame(
                        Duration.millis(EXPERIMENT_UPDATE_INTERVAL),
                        ae -> updateExperimentInfo()));
                expListUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
                expListUpdateTimeline.play();
            }
        } catch (AiravataClientException e) {
            e.printStackTrace();
        }
    }

    //TODO update other experiment details too
    //updates the experiment status in the background
    public void updateExperimentInfo(){
        if(!(experimentStatusLabel.getText().equals("FAILED") || experimentStatusLabel.getText().equals("COMPLETED")
                || experimentStatusLabel.getText().equals("CANCELLED"))) {
            Platform.runLater(() -> {
                String experimentId = experimentIdLabel.getText();
                try {
                    ExperimentModel experimentModel = AiravataManager.getInstance().getExperiment(experimentId);
                    experimentStatusLabel.setText(experimentModel.getExperimentStatus().getState().toString());
                    switch (experimentModel.getExperimentStatus().getState()){
                        case COMPLETED :
                            experimentStatusLabel.setTextFill(Color.GREEN);
                            break;
                        case FAILED :
                            experimentStatusLabel.setTextFill(Color.RED);
                            break;
                        case CREATED :
                            experimentStatusLabel.setTextFill(Color.BLUE);
                            break;
                        default :
                            experimentStatusLabel.setTextFill(Color.ORANGE);
                    }
                    logger.debug("Updated Experiment :" + experimentId);
                } catch (AiravataClientException e) {
                    e.printStackTrace();
                }
            });
        }else{
            expListUpdateTimeline.stop();
        }
    }
}