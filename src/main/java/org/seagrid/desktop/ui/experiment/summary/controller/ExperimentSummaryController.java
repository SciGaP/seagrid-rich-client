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

import com.google.common.eventbus.Subscribe;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.apache.airavata.model.appcatalog.appinterface.ApplicationInterfaceDescription;
import org.apache.airavata.model.appcatalog.computeresource.ComputeResourceDescription;
import org.apache.airavata.model.application.io.DataType;
import org.apache.airavata.model.application.io.InputDataObjectType;
import org.apache.airavata.model.application.io.OutputDataObjectType;
import org.apache.airavata.model.commons.ErrorModel;
import org.apache.airavata.model.data.replica.DataReplicaLocationModel;
import org.apache.airavata.model.data.replica.ReplicaLocationCategory;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.status.ExperimentState;
import org.apache.airavata.model.status.JobStatus;
import org.apache.airavata.model.workspace.Project;
import org.apache.thrift.TException;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.connectors.storage.GuiFileDownloadTask;
import org.seagrid.desktop.connectors.storage.StorageManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.ui.home.model.ExperimentListModel;
import org.seagrid.desktop.ui.storage.MassStorageBrowserWindow;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExperimentSummaryController {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentSummaryController.class);

    private static final double EXPERIMENT_UPDATE_INTERVAL = 10000;

    private ExperimentModel experimentModel;

    @FXML
    private Button expStorageDir;

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

    @FXML
    private GridPane experimentErrorGridPane;

    @FXML
    private TextArea errorTextArea;

    @FXML
    private VBox experimentSummaryVBox;

    private Timeline expInfoUpdateTimer = null;


    private String remoteDataDirRoot = SEAGridContext.getInstance().getGatewayUserDataRoot();

    //This is the start row of experiment inputs in summary view
    private int EXPERIMENT_INPUT_START_ROW = 15;

    public void initialize(){
        expLaunchButton.setOnAction(event -> {
           try{
               AiravataManager.getInstance().launchExperiment(experimentModel.getExperimentId());
               SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_LAUNCHED,experimentModel));
           } catch (Exception e) {
               SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog", experimentInfoGridPane.getScene().getWindow(),
                       "Failed launching experiment");
           }
        });
        expCancelButton.setOnAction(event -> {
            try{
                AiravataManager.getInstance().cancelExperiment(experimentModel.getExperimentId());
                SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_CANCELLED, experimentModel));
            } catch (Exception e) {
                SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog", experimentInfoGridPane.getScene().getWindow(),
                        "Failed cancelling experiment");
            }
        });
        expEditButton.setOnAction(event -> {
            SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_EDIT_REQUEST, experimentModel));
        });
        expCloneButton.setOnAction(event -> {
            try {
                List<InputDataObjectType> inputDataObjectTypes = experimentModel.getExperimentInputs();
                for(InputDataObjectType inputDataObjectType : inputDataObjectTypes){
                    if(inputDataObjectType.getType().equals(DataType.URI)){
                        String randomString = experimentNameLabel.getText().replaceAll(" ","-")+"-"+System.currentTimeMillis();
                        //FIXME - Hardcoded logic
                        File remoteSrcFile =  new File(inputDataObjectType.getValue());
                        String remoteSrcPath = "/" + remoteSrcFile.getParentFile().getName()
                                + "/" + remoteSrcFile.getName();
                        String remoteDestFilePath = "/" + randomString + "/" + remoteSrcFile.getName();
                        StorageManager.getInstance().createSymLink(remoteSrcPath, remoteDestFilePath);
                    }
                }
                String expId = AiravataManager.getInstance().cloneExperiment(experimentModel.getExperimentId(),
                        "Clone of " + experimentModel.getExperimentName());
                ExperimentModel clonedExperimentModel = AiravataManager.getInstance().getExperiment(expId);
                clonedExperimentModel.setExperimentInputs(inputDataObjectTypes);
                AiravataManager.getInstance().updateExperiment(clonedExperimentModel);
                SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_CLONED,
                        clonedExperimentModel));
            } catch (Exception e) {
                SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog", experimentInfoGridPane.getScene().getWindow(),
                        "Failed cloning experiment");
            }
        });
        expStorageDir.setOnAction(event -> {
            try {
                String path = experimentModel.getUserConfigurationData().getExperimentDataDir();
                path = path.replaceAll(SEAGridContext.getInstance().getGatewayUserDataRoot(),"");
                MassStorageBrowserWindow.displayFileBrowse(path);
            } catch (Exception e) {
                SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog", expStorageDir.getScene().getWindow(),
                        "Failed to open mass storage browser");
            }
        });
        SEAGridEventBus.getInstance().register(this);
    }

    public void initExperimentInfo(ExperimentModel experimentModel) throws TException, URISyntaxException {
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

            //This is to clear the current grid rows for experiment inputs, outputs and errors
            //EXPERIMENT_INPUT_START_ROW is a hardcoded constant which the input row starts
            List<Node> removingNodes = experimentInfoGridPane.getChildren().stream().filter(child ->
                    experimentInfoGridPane.getRowIndex(child) >= EXPERIMENT_INPUT_START_ROW).collect(Collectors.toList());
            experimentInfoGridPane.getChildren().removeAll(removingNodes);
            experimentInfoGridPane.getRowConstraints().remove(EXPERIMENT_INPUT_START_ROW, experimentInfoGridPane
                    .getRowConstraints().size());
            showExperimentInputs(experimentModel);
            updateButtonOptions(experimentModel);
            if((experimentStatusLabel.getText().equals(ExperimentState.FAILED.toString()) || experimentStatusLabel.getText()
                    .equals(ExperimentState.COMPLETED.toString())
                    || experimentStatusLabel.getText().equals(ExperimentState.CANCELED.toString()))) {
                showExperimentOutputs(experimentModel);
            }else{
                //TODO this should replace with a RabbitMQ Listener
                expInfoUpdateTimer = new Timeline(new KeyFrame(
                        Duration.millis(EXPERIMENT_UPDATE_INTERVAL),
                        ae -> updateExperimentInfo()));
                expInfoUpdateTimer.setCycleCount(Timeline.INDEFINITE);
                expInfoUpdateTimer.play();
            }

            if(experimentStatusLabel.getText().equals(ExperimentState.FAILED.toString())){
                showExperimentErrors(experimentModel);
            }else{
                experimentSummaryVBox.getChildren().remove(experimentErrorGridPane);
            }
        }
    }

    public void initExperimentInfo(String experimentId) throws TException, URISyntaxException {
        experimentModel = AiravataManager.getInstance().getExperiment(experimentId);
        initExperimentInfo(experimentModel);
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
                        expInfoUpdateTimer.stop();
                    }
                    logger.debug("Updated Experiment :" + experimentId);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(this.expInfoUpdateTimer != null)
                        this.expInfoUpdateTimer.stop();
                }
            });
        }else{
            expInfoUpdateTimer.stop();
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

    private void showExperimentInputs(ExperimentModel experimentModel) throws TException, URISyntaxException {
        List<InputDataObjectType> inputDataObjectTypes = experimentModel.getExperimentInputs();
        int rowIndex = EXPERIMENT_INPUT_START_ROW;
        experimentInfoGridPane.add(new Label("Inputs"), 0, rowIndex);
        String dataRoot = remoteDataDirRoot;
        List<DataReplicaLocationModel> replicas;
        String fileUri;
        Hyperlink hyperlink;
        TextFlow uriOutputLabel;

        Collections.sort(inputDataObjectTypes, (o1, o2) -> {
            if(o1.getName().startsWith("Optional-File-Inputs"))
                return Integer.MAX_VALUE;
            else if(o2.getName().startsWith("Optional-File-Inputs"))
                return Integer.MIN_VALUE;
            else
                return o1.getName().compareTo(o2.getName());
        });

        for(InputDataObjectType input : inputDataObjectTypes){
            switch (input.getType()){
                case URI :
                case STDERR:
                case STDOUT:
                    if(input.getValue() != null && !input.getValue().equals("")) {
                        replicas = AiravataManager.getInstance().getDataReplicas(input.getValue());
                        fileUri = "";
                        for (DataReplicaLocationModel rpModel : replicas) {
                            if (rpModel.getReplicaLocationCategory().equals(ReplicaLocationCategory.GATEWAY_DATA_STORE)) {
                                fileUri = rpModel.getFilePath();
                                break;
                            }
                        }
                        String filePath1 = (new URI(fileUri)).getPath();
                        hyperlink = new Hyperlink(Paths.get(filePath1).getFileName().toString());
                        uriOutputLabel = new TextFlow(new Text(input.getName() + " : "), hyperlink);
                        hyperlink.setOnAction(event -> {
                            downloadFile(Paths.get(filePath1.toString().replaceAll(dataRoot, "")), experimentModel);
                        });
                        experimentInfoGridPane.add(uriOutputLabel, 1, rowIndex);
                    }
                    break;
                case URI_COLLECTION:
                    if(input.getValue() != null && !input.getValue().equals("")) {
                        String uriCollection = input.getValue();
                        String[] uris = uriCollection.split(",");
                        int i = 1;
                        for(String uri : uris){
                            replicas = AiravataManager.getInstance().getDataReplicas(uri);
                            fileUri = "";
                            for(DataReplicaLocationModel rpModel : replicas){
                                if(rpModel.getReplicaLocationCategory().equals(ReplicaLocationCategory.GATEWAY_DATA_STORE)) {
                                    fileUri = rpModel.getFilePath();
                                    break;
                                }
                            }
                            String filePath2 = (new URI(fileUri)).getPath();
                            hyperlink = new Hyperlink(Paths.get(filePath2).getFileName().toString());
                            uriOutputLabel = new TextFlow(new Text(input.getName()+" ("+ i +")" +" : "), hyperlink);
                            hyperlink.setOnAction(event -> {
                                downloadFile(Paths.get(filePath2.toString().replaceAll(dataRoot, "")), experimentModel);
                            });
                            experimentInfoGridPane.add(uriOutputLabel, 1, rowIndex);

                            experimentInfoGridPane.getRowConstraints().add(rowIndex-1,new RowConstraints(25));
                            rowIndex++;
                            i++;
                        }
                        rowIndex--;
                    }
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

    private void showExperimentErrors(ExperimentModel experimentModel) {
        String error = "";
        if(experimentModel.getErrors() != null && !experimentModel.getErrors().isEmpty()) {
            for (ErrorModel errorModel : experimentModel.getErrors()) {
                error = error + errorModel.getUserFriendlyMessage() + " : ";
                error = error + errorModel.getActualErrorMessage() + "\n";
            }
            errorTextArea.setText(error);
            errorTextArea.setWrapText(true);
        }
    }

    private void showExperimentOutputs(ExperimentModel experimentModel) throws TException, URISyntaxException {
        int rowIndex = experimentInfoGridPane.getRowConstraints().size();
        experimentInfoGridPane.add(new Label("Outputs"), 0, rowIndex);
        List<OutputDataObjectType> outputDataObjectTypes = experimentModel.getExperimentOutputs();
        for(OutputDataObjectType output : outputDataObjectTypes){
            switch (output.getType()){
                case URI :
                case STDERR:
                case STDOUT:
                    String dataRoot = remoteDataDirRoot;
                    try{
                        List<DataReplicaLocationModel> replicas = AiravataManager.getInstance().getDataReplicas(output.getValue());
                        String fileUri = "";
                        for(DataReplicaLocationModel rpModel : replicas){
                            if(rpModel.getReplicaLocationCategory().equals(ReplicaLocationCategory.GATEWAY_DATA_STORE)) {
                                fileUri = rpModel.getFilePath();
                                break;
                            }
                        }
                        String filePath = (new URI(fileUri)).getPath();
                        Hyperlink hyperlink = new Hyperlink(Paths.get(filePath).getFileName().toString());
                        TextFlow uriOutputLabel = new TextFlow(new Text(output.getName()+" : "), hyperlink);
                        hyperlink.setOnAction(event -> {
                            downloadFile(Paths.get(filePath.toString().replaceAll(dataRoot, "")), experimentModel);
                        });
                        experimentInfoGridPane.add(uriOutputLabel, 1, rowIndex);
                        break;
                    }catch (Exception ex){
                        logger.info("Failed to retrieve output data for experiment : " + experimentModel.getExperimentId()
                                + ". Output : " + output.getValue());
                    }

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
                .getFileDownloadLocation()+ File.separator + experimentModel.getProjectId().substring(0,experimentModel
                .getProjectId().length() - 37) + File.separator + experimentModel.getExperimentId() +File.separator+remotePath.getFileName();
        Service<Boolean> service = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                try {
                    return new GuiFileDownloadTask(remotePath.toString(), localPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", experimentInfoGridPane.getScene().getWindow(),
                            "Unable To Connect To File Server !");
                }
                return null;
            }
        };
        SEAGridDialogHelper.showProgressDialog(service,"Progress Dialog",experimentInfoGridPane.getScene().getWindow(),
                "Downloading File " + remotePath.getFileName());
        service.setOnFailed((WorkerStateEvent t) -> {
            SEAGridDialogHelper.showExceptionDialogAndWait(service.getException(), "Exception Dialog",
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
                expStorageDir.setDisable(false);
                break;
            case EXECUTING:
                expCancelButton.setDisable(false);
            case CANCELING:
                expLaunchButton.setDisable(true);
                expEditButton.setDisable(true);
                expCancelButton.setDisable(true);
                break;
            case FAILED:
            case CANCELED:
            case COMPLETED:
                expCancelButton.setDisable(true);
                expLaunchButton.setDisable(true);
                expEditButton.setDisable(true);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void listenSEAGridEvents(SEAGridEvent event) throws TException {
        if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_UPDATED)) {
            ExperimentModel updatedExperimentModel = (ExperimentModel) event.getPayload();
            if(updatedExperimentModel.getExperimentId().equals(this.experimentModel.getExperimentId())){
                try {
                    initExperimentInfo(updatedExperimentModel);
                } catch (URISyntaxException e) {
                    SEAGridDialogHelper.showExceptionDialog(e, "Exception caught", null, "Unable to open experiment");
                }
            }
        }else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_DELETED)) {
            if(event.getPayload() instanceof ExperimentListModel){
                ExperimentListModel experimentListModel = (ExperimentListModel) event.getPayload();
                if(experimentModel.getExperimentId().equals(experimentListModel.getId())){
                    expInfoUpdateTimer.stop();
                }
            }else if(event.getPayload() instanceof  ExperimentModel){
                ExperimentModel deletedExpModel = (ExperimentModel) event.getPayload();
                if(experimentModel.getExperimentId().equals(deletedExpModel.getExperimentId())){
                    expInfoUpdateTimer.stop();
                }
            }
        }
    }
}