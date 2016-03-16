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
package org.seagrid.desktop.ui.experiment.create.controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.airavata.model.appcatalog.appinterface.ApplicationInterfaceDescription;
import org.apache.airavata.model.appcatalog.computeresource.BatchQueue;
import org.apache.airavata.model.appcatalog.computeresource.ComputeResourceDescription;
import org.apache.airavata.model.application.io.DataType;
import org.apache.airavata.model.application.io.InputDataObjectType;
import org.apache.airavata.model.application.io.OutputDataObjectType;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.experiment.UserConfigurationDataModel;
import org.apache.airavata.model.scheduling.ComputationalResourceSchedulingModel;
import org.apache.airavata.model.workspace.Project;
import org.apache.thrift.TException;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.connectors.storage.GuiBulkFileUploadTask;
import org.seagrid.desktop.connectors.storage.GuiFileDownloadTask;
import org.seagrid.desktop.ui.commons.ImageButton;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class ExperimentCreateController {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentCreateController.class);

    //These attributes are used when edit experiment is called
    private boolean isEditExperiment = false;

    private ExperimentModel editExperimentModel = null;


    @FXML
    public GridPane expCreateInputsGridPane;

    @FXML
    public Label expCreateTitle;

    @FXML
    private Label expCreateWallTimeLabel;

    @FXML
    private Label expCreateCoreCountLabel;

    @FXML
    private Label expCreateNodeCountLabel;

    @FXML
    private TextField expCreateNameField;

    @FXML
    private TextArea expCreateDescField;

    @FXML
    private ComboBox expCreateProjField;

    @FXML
    private ComboBox expCreateAppField;

    @FXML
    private ComboBox expCreateResourceField;

    @FXML
    private ComboBox expCreateQueueField;

    @FXML
    private TextField expCreateNodeCountField;

    @FXML
    private TextField expCreateTotalCoreCountField;

    @FXML
    private TextField expCreateWallTimeField;

    @FXML
    private TextField expCreatePhysicalMemField;

    @FXML
    private Button expSaveButton;

    @FXML
    private Button expSaveLaunchButton;

    private FileChooser fileChooser;

    private Map<InputDataObjectType, Object> experimentInputs;

    private String remoteDataDirRoot = SEAGridContext.getInstance().getGatewayUserDataRoot();

    private List<OutputDataObjectType> outputDataObjectTypes;

    @SuppressWarnings("unused")
    public void initialize() {
        initElements();
        initFileChooser();
    }

    private void initElements(){
        try{
            List<Project> projects = AiravataManager.getInstance().getProjects();
            expCreateProjField.getItems().setAll(projects);
            expCreateProjField.setConverter(new StringConverter<Project>() {
                @Override
                public String toString(Project project) {
                    return project.getName();
                }

                @Override
                public Project fromString(String string) {
                    return null;
                }
            });
            expCreateProjField.getSelectionModel().selectFirst();

            List<ApplicationInterfaceDescription> applications = AiravataManager.getInstance().getAllApplicationInterfaces();
            expCreateAppField.getItems().setAll(applications);
            expCreateAppField.setConverter(new StringConverter<ApplicationInterfaceDescription>() {
                @Override
                public String toString(ApplicationInterfaceDescription application) {
                    return application.getApplicationName();
                }

                @Override
                public ApplicationInterfaceDescription fromString(String string) {
                    return null;
                }
            });
            expCreateAppField.valueProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    loadAvailableComputeResources();
                    ApplicationInterfaceDescription applicationInterfaceDescription = (ApplicationInterfaceDescription)expCreateAppField
                            .getSelectionModel().getSelectedItem();
                    List<InputDataObjectType> inputDataObjectTypes = applicationInterfaceDescription.getApplicationInputs();
                    this.outputDataObjectTypes = applicationInterfaceDescription.getApplicationOutputs();
                    updateExperimentInputs(inputDataObjectTypes);
                } catch (Exception e) {
                    e.printStackTrace();
                    SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expCreateAppField.getScene().getWindow(),
                            "Failed to load experiment create dialog !");
                }
            });
            expCreateAppField.getSelectionModel().selectFirst();

            //Won't allow characters to be entered
            expCreateNodeCountField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    expCreateNodeCountField.setText(oldValue);
                }
            });
            expCreateTotalCoreCountField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    expCreateTotalCoreCountField.setText(oldValue);
                }
            });
            expCreateWallTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    expCreateWallTimeField.setText(oldValue);
                }
            });
            expCreatePhysicalMemField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    expCreatePhysicalMemField.setText(oldValue);
                }
            });

            expSaveButton.setOnAction(event -> {
                createExperiment(false);
            });

            expSaveLaunchButton.setOnAction(event -> {
                createExperiment(true);
            });

        }catch (Exception e){
            e.printStackTrace();
            SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expCreateNameField.getScene().getWindow(),
                    "Failed To Load New Experiment Window !");
        }
    }

    public void initExperimentEdit(ExperimentModel experimentModel){
        isEditExperiment = true;
        editExperimentModel = experimentModel;
        expCreateTitle.setText("Edit Experiment");

        expCreateNameField.setText(experimentModel.getExperimentName());
        expCreateDescField.setText(experimentModel.getDescription());
        expCreateProjField.getItems().stream().filter(p->((Project)p).getProjectID().equals(experimentModel.getProjectId()))
                .forEach(p -> expCreateProjField.getSelectionModel().select(p));
        expCreateAppField.getItems().stream().filter(p->((ApplicationInterfaceDescription)p).getApplicationInterfaceId()
                .equals(experimentModel.getExecutionId())).forEach(p -> expCreateAppField.getSelectionModel().select(p));
        expCreateAppField.setDisable(true);
        expCreateAppField.setStyle("-fx-opacity: 1;");
        expCreateResourceField.getItems().stream().filter(r -> ((ComputeResourceDescription) r).getComputeResourceId()
                .equals(experimentModel.getUserConfigurationData().getComputationalResourceScheduling().getResourceHostId()))
                .forEach(r -> expCreateResourceField.getSelectionModel().select(r));
        expCreateQueueField.getItems().stream().filter(q -> ((BatchQueue) q).getQueueName()
                .equals(experimentModel.getUserConfigurationData().getComputationalResourceScheduling().getQueueName()))
                .forEach(q -> expCreateQueueField.getSelectionModel().select(q));
        expCreateNodeCountField.setText(experimentModel.getUserConfigurationData().getComputationalResourceScheduling()
                .getNodeCount()+"");
        expCreateTotalCoreCountField.setText(experimentModel.getUserConfigurationData().getComputationalResourceScheduling()
                .getTotalCPUCount()+"");
        expCreateWallTimeField.setText(experimentModel.getUserConfigurationData().getComputationalResourceScheduling()
                .getWallTimeLimit()+"");
        expCreatePhysicalMemField.setText(experimentModel.getUserConfigurationData().getComputationalResourceScheduling()
                .getTotalPhysicalMemory()+"");
        updateExperimentInputs(experimentModel.getExperimentInputs());
    }

    //FIXME This is an application specific initialization method. It is nice if we can come up with a generalizable way to handle
    //FIXME application specific initializer
    public void initGaussianExperiment(String gaussianInput) throws FileNotFoundException, TException {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + "gaussian.in";
        PrintWriter out = new PrintWriter(tempFilePath);
        out.println(gaussianInput);
        out.close();
        String[] lines = gaussianInput.split("\n");
        for(String temp : lines){
            //These properties are set in the input file we are reusing it.
            if(temp.contains("%nproc=")){
                temp = temp.replace("%nproc=", "");
                expCreateNodeCountField.setText(temp);
            }else if(temp.contains("%nprocl=")){
                temp = temp.replace("%nprocl=", "");
                expCreateTotalCoreCountField.setText(temp);
            }else if(temp.contains("%mem=")){
                temp = temp.replace("%mem=", "");
                temp = temp.replace("MB", "");
                expCreatePhysicalMemField.setText(temp);
            }
        }
        List<ApplicationInterfaceDescription> applicationInterfaceDescriptions = AiravataManager.getInstance().getAllApplicationInterfaces();
        ApplicationInterfaceDescription gaussianApp = null;
        for(ApplicationInterfaceDescription appInter : applicationInterfaceDescriptions){
            if(appInter.getApplicationName().toLowerCase().contains(SEAGridContext.getInstance().getGaussianAppName())){
                gaussianApp = appInter;
                break;
            }
        }
        if(gaussianApp !=  null){
            List<ApplicationInterfaceDescription> gaussianAppList = new ArrayList();
            gaussianAppList.add(gaussianApp);
            expCreateAppField.getItems().setAll(gaussianAppList);
            expCreateAppField.getSelectionModel().select(0);
            List<InputDataObjectType> gaussianInputs = gaussianApp.getApplicationInputs();
            gaussianInputs.get(0).setValue(tempFilePath);
            updateExperimentInputs(gaussianInputs);
        }
    }

    public void initGamessExperiment(String gamessInput) throws FileNotFoundException, TException {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + "gamess.in";
        PrintWriter out = new PrintWriter(tempFilePath);
        out.println(gamessInput);
        out.close();
        List<ApplicationInterfaceDescription> applicationInterfaceDescriptions = AiravataManager.getInstance().getAllApplicationInterfaces();
        ApplicationInterfaceDescription gamessApp = null;
        for(ApplicationInterfaceDescription appInter : applicationInterfaceDescriptions){
            if(appInter.getApplicationName().toLowerCase().contains(SEAGridContext.getInstance().getGamessAppName())){
                gamessApp = appInter;
                break;
            }
        }
        if(gamessApp !=  null){
            List<ApplicationInterfaceDescription> gamessAppList = new ArrayList();
            gamessAppList.add(gamessApp);
            expCreateAppField.getItems().setAll(gamessAppList);
            expCreateAppField.getSelectionModel().select(0);
            List<InputDataObjectType> gamessAppInput = gamessApp.getApplicationInputs();
            gamessAppInput.get(0).setValue(tempFilePath);
            updateExperimentInputs(gamessAppInput);
        }
    }

    private void initFileChooser(){
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
//        Todo Should add Science Specific Filters
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
//                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
//                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
//                new FileChooser.ExtensionFilter("All Files", "*.*"));
    }

    private void loadAvailableComputeResources() throws TException {
        ApplicationInterfaceDescription selectedApp = (ApplicationInterfaceDescription)expCreateAppField
                .getSelectionModel().getSelectedItem();
        if(selectedApp != null){
            List<ComputeResourceDescription> computeResourceDescriptions = AiravataManager.getInstance()
                    .getAvailableComputeResourcesForApp(selectedApp.getApplicationInterfaceId());
            expCreateResourceField.getItems().setAll(computeResourceDescriptions);
            expCreateResourceField.setConverter(new StringConverter<ComputeResourceDescription>() {
                @Override
                public String toString(ComputeResourceDescription resourceDescription) {
                    return resourceDescription.getHostName();
                }

                @Override
                public ComputeResourceDescription fromString(String string) {
                    return null;
                }
            });
            expCreateResourceField.valueProperty().addListener((observable, oldValue, newValue) -> {
                loadAvailableBatchQueues();
            });
            expCreateResourceField.getSelectionModel().selectFirst();
        }
    }

    private void loadAvailableBatchQueues(){
        ComputeResourceDescription selectedResource = (ComputeResourceDescription)expCreateResourceField
                .getSelectionModel().getSelectedItem();
        if(selectedResource != null){
            List<BatchQueue> batchQueues = selectedResource.getBatchQueues();
            expCreateQueueField.getItems().setAll(batchQueues);
            expCreateQueueField.setConverter(new StringConverter<BatchQueue>() {
                @Override
                public String toString(BatchQueue batchQueue) {
                    return batchQueue.getQueueName();
                }

                @Override
                public BatchQueue fromString(String string) {
                    return null;
                }
            });
            expCreateQueueField.valueProperty().addListener((observable, oldValue, newValue) -> {
                updateQueueSpecificLimitsInLabels();
            });
            expCreateQueueField.getSelectionModel().selectFirst();
        }
    }

    private void updateQueueSpecificLimitsInLabels(){
        BatchQueue selectedQueue = (BatchQueue)expCreateQueueField.getSelectionModel().getSelectedItem();
        if(selectedQueue !=  null){
            expCreateNodeCountLabel.setText("Node Count ( max - " + selectedQueue.getMaxNodes()
                    + " )");
            expCreateCoreCountLabel.setText("Total Core Count ( max - " + selectedQueue.getMaxProcessors()
                    + " )");
            expCreateWallTimeLabel.setText("Wall Time Limit ( max - " + selectedQueue.getMaxRunTime()
                    + " )");
        }
    }

    private void updateExperimentInputs(List<InputDataObjectType> inputDataObjectTypes) {
        this.experimentInputs = new HashMap<>();
        expCreateInputsGridPane.getChildren().clear();
        expCreateInputsGridPane.getRowConstraints().clear();
        int index = 0;
        for(InputDataObjectType inputDataObjectType : inputDataObjectTypes){
            if(inputDataObjectType.getType().equals(DataType.STRING)){
                expCreateInputsGridPane.add(new Label(inputDataObjectType.getName()), 0, index);
                TextField stringField = new TextField();
                expCreateInputsGridPane.add( stringField, 1, index);
                this.experimentInputs.put(inputDataObjectType,null);
                stringField.textProperty().addListener((observable, oldValue, newValue) -> {
                    experimentInputs.put(inputDataObjectType,newValue);
                });
                if(inputDataObjectType.getValue() != null)
                    stringField.setText(inputDataObjectType.getValue());
            }else if(inputDataObjectType.getType().equals(DataType.INTEGER)){
                expCreateInputsGridPane.add(new Label(inputDataObjectType.getName()), 0, index);
                TextField numericField = new TextField();
                numericField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) {
                        numericField.setText(oldValue);
                        experimentInputs.put(inputDataObjectType, oldValue);
                    } else {
                        experimentInputs.put(inputDataObjectType, newValue);
                    }
                });
                expCreateInputsGridPane.add(numericField, 1, index);
                if(inputDataObjectType.getValue() != null)
                    numericField.setText(inputDataObjectType.getValue());
            }else if(inputDataObjectType.getType().equals(DataType.FLOAT)){
                expCreateInputsGridPane.add(new Label(inputDataObjectType.getName()), 0, index);
                TextField floatField = new TextField();
                floatField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\f*")) {
                        floatField.setText(oldValue);
                        experimentInputs.put(inputDataObjectType, oldValue);
                    } else {
                        experimentInputs.put(inputDataObjectType, newValue);
                    }
                });
                expCreateInputsGridPane.add(floatField, 1, index);
                if(inputDataObjectType.getValue() != null)
                    floatField.setText(inputDataObjectType.getValue());
            }else if(inputDataObjectType.getType().equals(DataType.URI)){
                expCreateInputsGridPane.add(new Label(inputDataObjectType.getName()), 0, index);
                HBox hBox = new HBox(2);
                Button localFilePickBtn = new ImageButton("/images/local-storage.png");
                localFilePickBtn.setTooltip(new Tooltip("Select local file"));
                hBox.getChildren().add(0, localFilePickBtn);
                Button remoteFilePickBtn = new ImageButton("/images/remote-storage.png");
                remoteFilePickBtn.setTooltip(new Tooltip("Select remote file"));
                hBox.getChildren().add(1, remoteFilePickBtn);
                expCreateInputsGridPane.add(hBox, 1, index);
                localFilePickBtn.setOnAction(event -> {
                    File selectedFile = fileChooser.showOpenDialog(expCreateInputsGridPane.getScene().getWindow());
                    handleExperimentFileSelect(inputDataObjectType, hBox, localFilePickBtn, remoteFilePickBtn, selectedFile);
                });
                remoteFilePickBtn.setOnAction(event -> {
                    try {
                        String selectedRemoteFilePath = showSelectRemoteFile();
                        if(selectedRemoteFilePath != null && !selectedRemoteFilePath.isEmpty()) {
                            selectedRemoteFilePath = remoteDataDirRoot + (selectedRemoteFilePath.startsWith("/")
                                    ? selectedRemoteFilePath.substring(1) : selectedRemoteFilePath);
                            inputDataObjectType.setValue(selectedRemoteFilePath);
                            handleExperimentFileSelect(inputDataObjectType, hBox, localFilePickBtn, remoteFilePickBtn, new File(selectedRemoteFilePath));
                        }
                    } catch (IOException e) {
                        SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", remoteFilePickBtn.getScene().getWindow(),
                                "Failed to load remote file picker");
                    }
                });
                if(inputDataObjectType.getValue() != null && !inputDataObjectType.getValue().isEmpty()){
                    handleExperimentFileSelect(inputDataObjectType, hBox, localFilePickBtn, remoteFilePickBtn, new File(inputDataObjectType.getValue()));
                }
            }
            //maintaining the grid pane row height
            expCreateInputsGridPane.getRowConstraints().add(index,new RowConstraints(25));
            index++;
        }
    }

    private void handleExperimentFileSelect(InputDataObjectType inputDataObjectType, HBox hBox, Button localFilePickBtn,
                                            Button remoteFilePickBtn,File selectedFile){
        if (selectedFile != null) {
            hBox.getChildren().clear();
            Hyperlink hyperlink = new Hyperlink(selectedFile.getName());
            hyperlink.setOnAction(hyperLinkEvent -> {
                //FIXME Else it is a remote file. Cannot open locally without downloading it.
                if(selectedFile.exists()){
                    try {
                        Desktop.getDesktop().open(selectedFile);
                    } catch (IOException e) {
                        SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog",
                                expCreateInputsGridPane.getScene().getWindow(), "Failed Opening File");
                    }
                }else{
                    boolean result = SEAGridDialogHelper.showConfirmDialog("Confirm Action", "Remote File Download", "You have selected a remote file." +
                            " Do you want to download it ?");
                    if(result){
                        String remotePath = selectedFile.getPath();
                        remotePath = remotePath.replaceAll(remoteDataDirRoot, "");
                        downloadFile(Paths.get(remotePath), System.getProperty("java.io.tmpdir"));
                    }
                }
            });
            hBox.getChildren().add(0, hyperlink);
            hBox.getChildren().add(1, localFilePickBtn);
            hBox.getChildren().add(2, remoteFilePickBtn);
            experimentInputs.put(inputDataObjectType, selectedFile);
        }
    }

    private String showSelectRemoteFile() throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/views/experiment/create/remote-file-picker.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("SEAGrid Desktop Client - Select Remote File");
        primaryStage.setScene(new Scene(root, 400, 600));
        RemoteFilePickerController controller = loader.getController();
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.showAndWait();
        return controller.getSelectedFilePath();
    }

    private void createExperiment(boolean launch){
        if(validateExperimentFields()){
            //FIXME Hardcoded value
            String projectId = ((Project)expCreateProjField.getSelectionModel().getSelectedItem()).getProjectID();
            String randomString = projectId.substring(0,projectId.length()-37) + "/"
                    + expCreateNameField.getText().replaceAll("/[^A-Za-z0-9 ]/","-")+"."+System.currentTimeMillis();
            String remoteDataDir = SEAGridContext.getInstance().getRemoteDataDirPrefix() + remoteDataDirRoot + randomString  + "/";
            ExperimentModel experimentModel = assembleExperiment(remoteDataDir, SEAGridContext.getInstance().getUserName() + "/"
                    + randomString + "/");
            Map<String,File> uploadFiles = new HashMap<>();
            for(Iterator<Map.Entry<InputDataObjectType, Object>> it = experimentInputs.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<InputDataObjectType, Object> entry = it.next();
                if(entry.getKey().getType().equals(DataType.URI)) {
                    File file = (File) entry.getValue();
                    //FIXME - Otherwise the file is remote file. This is not a good way to handle this. Should find a better way to handle it
                    if(file.exists())
                        uploadFiles.put("/" + randomString + "/" + file.getName(), file);
                }
            }
            if(uploadFiles.size() > 0){
                Service<Boolean> fileUploadService = getFileUploadService(uploadFiles);
                fileUploadService.setOnSucceeded(event -> {
                    createExperiment(experimentModel, launch);
                });
                fileUploadService.start();
            }else{
                createExperiment(experimentModel, launch);
            }
        }
    }

    private void createExperiment(ExperimentModel experimentModel, boolean launch){
        try {
            if(!isEditExperiment) {
                String expId = AiravataManager.getInstance().createExperiment(experimentModel);
                experimentModel = AiravataManager.getInstance().getExperiment(expId);
                SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_CREATED
                        , experimentModel));

            }else{
                experimentModel.setExperimentId(editExperimentModel.getExperimentId());
                AiravataManager.getInstance().updateExperiment(experimentModel);
                experimentModel = AiravataManager.getInstance().getExperiment(experimentModel.getExperimentId());
                SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_UPDATED
                        ,experimentModel));
            }
            if(launch){
                try {
                    AiravataManager.getInstance().launchExperiment(experimentModel.getExperimentId());
                    SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_LAUNCHED
                            ,experimentModel));
                }catch (TException e){
                    e.printStackTrace();
                    SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expSaveLaunchButton.getScene().getWindow(),
                            "Experiment launch failed !");
                }
            }
            Stage stage = (Stage) expSaveButton.getScene().getWindow();
            stage.close();
        } catch (TException e) {
            e.printStackTrace();
            SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expSaveButton.getScene().getWindow(),
                    "Experiment create failed !");
        }
    }

    private ExperimentModel assembleExperiment(String remoteDataDir, String experimentDataDir){
        ExperimentModel experimentModel = new ExperimentModel();
        experimentModel.setExperimentName(expCreateNameField.getText());
        experimentModel.setDescription(expCreateDescField.getText() == null ? "" : expCreateDescField.getText());
        experimentModel.setProjectId(((Project)expCreateProjField.getSelectionModel().getSelectedItem()).getProjectID());
        experimentModel.setExecutionId(((ApplicationInterfaceDescription)expCreateAppField.getSelectionModel()
                .getSelectedItem()).getApplicationInterfaceId());
        experimentModel.setGatewayId(SEAGridContext.getInstance().getAiravataGatewayId());
        experimentModel.setUserName(SEAGridContext.getInstance().getUserName());

        UserConfigurationDataModel userConfigurationDataModel = new UserConfigurationDataModel();

        //FIXME Hard Coded Default Values
        userConfigurationDataModel.setAiravataAutoSchedule(false);
        userConfigurationDataModel.setOverrideManualScheduledParams(false);
        userConfigurationDataModel.setStorageId(SEAGridContext.getInstance().getGatewayaStorageId());
        userConfigurationDataModel.setExperimentDataDir(remoteDataDir + experimentDataDir);

        ComputationalResourceSchedulingModel resourceSchedulingModel = new ComputationalResourceSchedulingModel();
        resourceSchedulingModel.setResourceHostId(((ComputeResourceDescription)expCreateResourceField.getSelectionModel()
                .getSelectedItem()).getComputeResourceId());
        resourceSchedulingModel.setQueueName(((BatchQueue)expCreateQueueField.getSelectionModel().getSelectedItem())
                .getQueueName());
        resourceSchedulingModel.setNodeCount(Integer.parseInt(expCreateNodeCountField.getText()));
        resourceSchedulingModel.setTotalCPUCount(Integer.parseInt(expCreateTotalCoreCountField.getText()));
        resourceSchedulingModel.setWallTimeLimit(Integer.parseInt(expCreateWallTimeField.getText()));
        resourceSchedulingModel.setTotalPhysicalMemory(Integer.parseInt(expCreatePhysicalMemField.getText()));
        userConfigurationDataModel.setComputationalResourceScheduling(resourceSchedulingModel);
        experimentModel.setUserConfigurationData(userConfigurationDataModel);

        List<InputDataObjectType> temp = new ArrayList<>();
        for(InputDataObjectType inputDataObjectType : this.experimentInputs.keySet()){
            if(inputDataObjectType.getType().equals(DataType.URI)){
                //FIXME - Otherwise the file is remote file. This is not a good way to handle this. Should find a better way to handle it
                if(((File) this.experimentInputs
                        .get(inputDataObjectType)).exists()) {
                    inputDataObjectType.setValue(remoteDataDir + ((File) this.experimentInputs
                            .get(inputDataObjectType)).getName());
                }
            }else{
                inputDataObjectType.setValue((String) this.experimentInputs.get(inputDataObjectType));
            }
            temp.add(inputDataObjectType);
        }
        experimentModel.setExperimentInputs(temp);
        experimentModel.setExperimentOutputs(outputDataObjectTypes);
        return experimentModel;
    }

    private boolean validateExperimentFields(){

        String expName = expCreateNameField.getText();
        if(expName == null || expName.isEmpty()){
            SEAGridDialogHelper.showWarningDialog("Warning Dialog", "Experiment Validation Failed", "Experiment name should" +
                    " be non empty");
            return false;
        }

        BatchQueue selectedQueue = (BatchQueue)expCreateQueueField.getSelectionModel().getSelectedItem();
        if(selectedQueue != null) {
            try {
                int nodeCount = Integer.parseInt(expCreateNodeCountField.getText().trim());
                if (nodeCount <= 0 || nodeCount > selectedQueue.getMaxNodes()){
                    SEAGridDialogHelper.showWarningDialog("Warning Dialog", "Experiment Validation Failed", "Node count should" +
                            " be positive value less than " + selectedQueue.getMaxNodes());
                    return false;
                }

                int coreCount = Integer.parseInt(expCreateTotalCoreCountField.getText().trim());
                if (coreCount <= 0 || coreCount > selectedQueue.getMaxProcessors()){
                    SEAGridDialogHelper.showWarningDialog("Warning Dialog", "Experiment Validation Failed", "Core count should" +
                            " be positive value less than " + selectedQueue.getMaxProcessors());
                    return false;
                }

                int wallTime = Integer.parseInt(expCreateWallTimeField.getText().trim());
                if (wallTime <= 0 || wallTime > selectedQueue.getMaxProcessors()){
                    SEAGridDialogHelper.showWarningDialog("Warning Dialog", "Experiment Validation Failed", "Wall time should" +
                            " be positive value less than " + selectedQueue.getMaxRunTime());
                    return false;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if(this.experimentInputs == null || this.experimentInputs.size()==0){
            SEAGridDialogHelper.showWarningDialog("Warning Dialog", "Experiment Validation Failed", "Experiment inputs" +
                    " not selected");
            return false;
        }else{
            for(InputDataObjectType inputDataObjectType : this.experimentInputs.keySet()){
                Object object = this.experimentInputs.get(inputDataObjectType);
                if(object == null){
                    SEAGridDialogHelper.showWarningDialog("Warning Dialog", "Experiment Validation Failed", "No value" +
                            " selected for input " + inputDataObjectType.getName());
                    return false;
                }
            }
        }
        return true;
    }

    private Service<Boolean> getFileUploadService(Map<String, File> uploadFiles){
        Service<Boolean> service = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                try {
                    return new GuiBulkFileUploadTask(uploadFiles);
                } catch (Exception e) {
                    e.printStackTrace();
                    SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expCreateInputsGridPane.getScene().getWindow(),
                            "Unable To Connect To File Server !");
                }
                return null;
            }
        };
        SEAGridDialogHelper.showProgressDialog(service,"Progress Dialog",expCreateInputsGridPane.getScene().getWindow(),
                "Uploading Experiment Input Files");
        service.setOnFailed((WorkerStateEvent t) -> {
            SEAGridDialogHelper.showExceptionDialogAndWait(service.getException(), "Exception Dialog",
                    expCreateInputsGridPane.getScene().getWindow(), "File Upload Failed");
        });
        return service;
    }

    private void downloadFile(Path remotePath, String destParentPath){
        String localPath = destParentPath + File.separator + remotePath.getFileName();
        Service<Boolean> service = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                try {
                    return new GuiFileDownloadTask(remotePath.toString(), localPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expCreateNameField.getScene().getWindow(),
                            "Unable To Connect To File Server !");
                }
                return null;
            }
        };
        SEAGridDialogHelper.showProgressDialog(service,"Progress Dialog",expCreateNameField.getScene().getWindow(),
                "Downloading File " + remotePath.getFileName());
        service.setOnFailed((WorkerStateEvent t) -> {
            SEAGridDialogHelper.showExceptionDialogAndWait(service.getException(), "Exception Dialog",
                    expCreateNameField.getScene().getWindow(), "File Download Failed");
        });
        service.setOnSucceeded((WorkerStateEvent t)->{
            SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.FILE_DOWNLOADED,localPath));
        });
        service.start();
    }
}