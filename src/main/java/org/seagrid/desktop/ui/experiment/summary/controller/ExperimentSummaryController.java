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
package org.seagrid.desktop.ui.experiment.summary.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.apache.airavata.model.appcatalog.appinterface.ApplicationInterfaceDescription;
import org.apache.airavata.model.appcatalog.computeresource.ComputeResourceDescription;
import org.apache.airavata.model.application.io.InputDataObjectType;
import org.apache.airavata.model.application.io.OutputDataObjectType;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.status.JobStatus;
import org.apache.airavata.model.workspace.Project;
import org.apache.thrift.TException;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.connectors.storage.GuiFileDownloadTask;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ExperimentSummaryController {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentSummaryController.class);

    private static final double EXPERIMENT_UPDATE_INTERVAL = 10000;

    @FXML
    private Button expMonitorOutput;

    @FXML
    private Button expLaunchButton;

    @FXML
    private Button expEditButton;

    @FXML
    private Button expCancelButton;

    @FXML
    private Button expCloneButton;

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

    private Timeline expInfpUpdateTimer = null;

    public void initExperimentInfo(String experimentId) throws TException{
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
            showStatus(experimentModel);
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

            showExperimentInputs(experimentModel);
            updateButtonOptions(experimentModel);
            if((experimentStatusLabel.getText().equals("FAILED") || experimentStatusLabel.getText().equals("COMPLETED")
                    || experimentStatusLabel.getText().equals("CANCELLED"))) {
                showExperimentOutputs(experimentModel);
            }
        }

        //TODO this should replace with a RabbitMQ Listener
        if(!(experimentStatusLabel.getText().equals("FAILED") || experimentStatusLabel.getText().equals("COMPLETED")
                || experimentStatusLabel.getText().equals("CANCELLED"))) {
            expInfpUpdateTimer = new Timeline(new KeyFrame(
                    Duration.millis(EXPERIMENT_UPDATE_INTERVAL),
                    ae -> updateExperimentInfo()));
            expInfpUpdateTimer.setCycleCount(Timeline.INDEFINITE);
            expInfpUpdateTimer.play();
        }
    }

    //updates the experiment status and outputs in the background
    public void updateExperimentInfo(){
        if(!(experimentStatusLabel.getText().equals("FAILED") || experimentStatusLabel.getText().equals("COMPLETED")
                || experimentStatusLabel.getText().equals("CANCELLED"))) {
            Platform.runLater(() -> {
                String experimentId = experimentIdLabel.getText();
                try {
                    ExperimentModel experimentModel = AiravataManager.getInstance().getExperiment(experimentId);
                    showStatus(experimentModel);
                    updateButtonOptions(experimentModel);
                    String expState = experimentModel.getExperimentStatus().getState().toString();
                    if(expState.equals("FAILED") || expState.equals("COMPLETED") || expState.equals("CANCELLED")){
                        showExperimentOutputs(experimentModel);
                        expInfpUpdateTimer.stop();
                    }
                    logger.debug("Updated Experiment :" + experimentId);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(this.expInfpUpdateTimer != null)
                        this.expInfpUpdateTimer.stop();
                }
            });
        }else{
            expInfpUpdateTimer.stop();
        }
    }

    private void showStatus(ExperimentModel experimentModel) throws TException {
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

        Map<String, JobStatus> jobStatusMap = null;
        try {
            jobStatusMap = AiravataManager.getInstance().getJobStatuses(experimentModel.getExperimentId());
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
        } catch (AiravataClientException e) {
            e.printStackTrace();
        }
    }

    private void showExperimentInputs(ExperimentModel experimentModel){
        List<InputDataObjectType> inputDataObjectTypes = experimentModel.getExperimentInputs();
        int rowIndex = 16;
        experimentInfoGridPane.add(new Label("Inputs"), 0, rowIndex);
        for(InputDataObjectType input : inputDataObjectTypes){
            switch (input.getType()){
                case URI :
                case STDERR:
                case STDOUT:
                    Hyperlink hyperlink = new Hyperlink(Paths.get(input.getValue()).getFileName().toString());
                    TextFlow uriInputLabel = new TextFlow(new Text(input.getName()+" : "), hyperlink);
                    hyperlink.setOnAction(event -> {
                        //FIXME the path has different different forms.
                        String[] bits = input.getValue().split(":");
                        String filePathString = bits[bits.length-1];
                        Path filePath = Paths.get(filePathString);
                        downloadFile(filePath, experimentModel);
                    });
                    experimentInfoGridPane.add(uriInputLabel, 1, rowIndex);
                    break;
                default :
                    Label outputLabel = new Label();
                    outputLabel.setText(input.getName() + " : " + input.getValue());
                    experimentInfoGridPane.add(outputLabel, 1, rowIndex);
            }
            experimentInfoGridPane.getRowConstraints().add(rowIndex-1,new RowConstraints(25));
            rowIndex++;
        }
    }

    private void showExperimentOutputs(ExperimentModel experimentModel){
        int rowIndex = experimentInfoGridPane.getRowConstraints().size();
        experimentInfoGridPane.add(new Label("Outputs"), 0, rowIndex);
        List<OutputDataObjectType> outputDataObjectTypes = experimentModel.getExperimentOutputs();
        for(OutputDataObjectType output : outputDataObjectTypes){
            switch (output.getType()){
                case URI :
                case STDERR:
                case STDOUT:
                    Hyperlink hyperlink = new Hyperlink(Paths.get(output.getValue()).getFileName().toString());
                    TextFlow uriOutputLabel = new TextFlow(new Text(output.getName()+" : "), hyperlink);
                    hyperlink.setOnAction(event -> {
                        downloadFile(Paths.get(output.getValue()), experimentModel);
                    });
                    experimentInfoGridPane.add(uriOutputLabel, 1, rowIndex);
                    break;
                default :
                    Label outputLabel = new Label();
                    outputLabel.setText(output.getName() + " : " + output.getValue());
                    experimentInfoGridPane.add(outputLabel, 1, rowIndex);
            }
            experimentInfoGridPane.getRowConstraints().add(rowIndex - 1, new RowConstraints(25));
            rowIndex++;
        }
    }

    private void downloadFile(Path remotePath, ExperimentModel experimentModel){
        String localPath = SEAGridContext.getInstance()
                .getFileDownloadLocation()+ File.separator + experimentModel.getExperimentId()
                +File.separator+remotePath.getFileName();
        Service<Boolean> service = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                try {
                    return new GuiFileDownloadTask(remotePath.toString(), localPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    SEAGridDialogHelper.showExceptionDialog(e,"Exception Dialog",experimentInfoGridPane.getScene().getWindow(),
                            "Unable To Connect To File Server !");
                }
                return null;
            }
        };
        SEAGridDialogHelper.showProgressDialog(service,"Progress Dialog",experimentInfoGridPane.getScene().getWindow(),
                "Downloading File " + remotePath.getFileName());
        service.setOnFailed((WorkerStateEvent t) -> {
            SEAGridDialogHelper.showExceptionDialog(service.getException(), "Exception Dialog",
                    experimentInfoGridPane.getScene().getWindow(), "File Download Failed");
        });
        service.setOnSucceeded((WorkerStateEvent t)->{
            SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.FILE_DOWNLOADED,localPath));
        });
        service.start();
    }

    private void updateButtonOptions(ExperimentModel experimentModel){
        switch (experimentModel.getExperimentStatus().getState()){
            case CREATED:
                expCancelButton.setDisable(true);
                expMonitorOutput.setDisable(true);
                break;
            case EXECUTING:
                expLaunchButton.setDisable(true);
                expEditButton.setDisable(true);
                expCancelButton.setDisable(false);
                expMonitorOutput.setDisable(false);
                break;
            case FAILED:
            case CANCELED:
            case COMPLETED:
                expCancelButton.setDisable(true);
                expLaunchButton.setDisable(true);
                expEditButton.setDisable(true);
        }
    }
}