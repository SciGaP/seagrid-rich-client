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
import org.apache.airavata.model.appcatalog.userresourceprofile.UserComputeResourcePreference;
import org.apache.airavata.model.application.io.DataType;
import org.apache.airavata.model.application.io.InputDataObjectType;
import org.apache.airavata.model.application.io.OutputDataObjectType;
import org.apache.airavata.model.data.replica.*;
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
import org.seagrid.desktop.util.UserPrefs;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
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

    @FXML
    private RadioButton useMyCRAccount;

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

            UserPrefs userPrefs = SEAGridContext.getInstance().getUserPrefs();
            ApplicationInterfaceDescription lastSelectedApplication = null;
            if(userPrefs != null && userPrefs.getLastApplicationId() != null){
                Optional<ApplicationInterfaceDescription> lastApplication = applications.stream()
                        .filter(application -> application.getApplicationInterfaceId().equals(userPrefs.getLastApplicationId())).findFirst();
                if(lastApplication.isPresent()){
                    lastSelectedApplication = lastApplication.get();
                }
            }

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
                    boolean hasOptionalFileInputs = applicationInterfaceDescription.isHasOptionalFileInputs();
                    List<InputDataObjectType> inputDataObjectTypes = applicationInterfaceDescription.getApplicationInputs();
                    this.outputDataObjectTypes = applicationInterfaceDescription.getApplicationOutputs();
                    updateExperimentInputs(inputDataObjectTypes, hasOptionalFileInputs);
                } catch (Exception e) {
                    e.printStackTrace();
                    SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expCreateAppField.getScene().getWindow(),
                            "Failed to load experiment create dialog !");
                }
            });

            if(lastSelectedApplication != null){
                expCreateAppField.getSelectionModel().select(lastSelectedApplication);
            }else{
                expCreateAppField.getSelectionModel().selectFirst();
            }

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
                try {
                    createExperiment(false);
                } catch (TException e) {
                    SEAGridDialogHelper.showExceptionDialog(e,"Caught Exception",null, "Unable to create experiment");
                }
            });

            expSaveLaunchButton.setOnAction(event -> {
                try {
                    createExperiment(true);
                } catch (TException e) {
                    SEAGridDialogHelper.showExceptionDialog(e, "Caught Exception", null, "Unable to create experiment");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expCreateNameField.getScene().getWindow(),
                    "Failed To Load New Experiment Window !");
        }
    }

    public void initExperimentEdit(ExperimentModel experimentModel) throws TException, URISyntaxException {
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
        expCreateProjField.setDisable(true);
        expCreateAppField.setStyle("-fx-opacity: 1;");
        expCreateResourceField.getItems().stream().filter(r -> ((ComputeResourceDescription) r).getComputeResourceId()
                .equals(experimentModel.getUserConfigurationData().getComputationalResourceScheduling().getResourceHostId()))
                .forEach(r -> expCreateResourceField.getSelectionModel().select(r));

        UserComputeResourcePreference userComputeResourcePreference = AiravataManager.getInstance()
                .getUserComputeResourcePrefs(editExperimentModel.getUserConfigurationData()
                        .getComputationalResourceScheduling().getResourceHostId());

        if(userComputeResourcePreference != null){
            useMyCRAccount.setVisible(true);
            if(editExperimentModel.getUserConfigurationData().isUseUserCRPref()){
                useMyCRAccount.setSelected(true);
            }
        }else{
            useMyCRAccount.setVisible(false);
        }

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

        ApplicationInterfaceDescription applicationInterfaceDescription = AiravataManager.getInstance().getApplicationInterface(experimentModel.getExecutionId());
        boolean hasOptionalFileInputs = applicationInterfaceDescription.isHasOptionalFileInputs();
        updateExperimentInputs(experimentModel.getExperimentInputs(), hasOptionalFileInputs);
    }

    //FIXME This is an application specific initialization method. It is nice if we can come up with a generalizable way to handle
    //FIXME application specific initializer
    public void initGaussianExperiment(String gaussianInput) throws FileNotFoundException, TException, URISyntaxException {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + "gaussian.in";
        PrintWriter out = new PrintWriter(tempFilePath);
        out.println(gaussianInput);
        out.close();
        int bcount=0;
        String[] lines = gaussianInput.split("\n");
        String desc="";
        for(String temp : lines){
            //These properties are set in the input file we are reusing it.
            if(temp.contains("%nproc=")){
                temp = temp.replace("%nproc=", "");
                expCreateNodeCountField.setText("1");
                expCreateTotalCoreCountField.setText(temp);
            }else if(temp.contains("%nprocl=")){
                temp = temp.replace("%nprocl=", "");
                //expCreateTotalCoreCountField.setText(temp);
                expCreateNodeCountField.setText(temp);
            }else if(temp.contains("%mem=")){
                temp = temp.replace("%mem=", "");
                temp = temp.replace("MB", "");
                expCreatePhysicalMemField.setText(temp);
            }
            else if(temp.equals("")){
                //blank
                bcount++;
                if (bcount == 1) {
                    desc="";
                    temp="";
                }
            }
            if (bcount < 2) {
                desc=desc+temp;
            }

        }
        expCreateNameField.setText("Default_job" );
        expCreateDescField.setText(desc);
        List<ApplicationInterfaceDescription> applicationInterfaceDescriptions = AiravataManager.getInstance().getAllApplicationInterfaces();
        List<ApplicationInterfaceDescription> gaussianApps = new ArrayList<>();
        for(ApplicationInterfaceDescription appInter : applicationInterfaceDescriptions){
            if(appInter.getApplicationName().toLowerCase().startsWith(SEAGridContext.getInstance().getGaussianAppName())){
                gaussianApps.add(appInter);
            }
        }
        if(gaussianApps.size() > 0){
            expCreateAppField.getItems().setAll(gaussianApps);
            expCreateAppField.getSelectionModel().select(0);
            for(ApplicationInterfaceDescription app : gaussianApps){
                List<InputDataObjectType> gaussianInputs = app.getApplicationInputs();
                gaussianInputs.get(0).setValue(tempFilePath);
                updateExperimentInputs(gaussianInputs, true);
            }
        }
    }

    public void initGamessExperiment(String gamessInput) throws FileNotFoundException, TException, URISyntaxException {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + "gamess.inp";
        PrintWriter out = new PrintWriter(tempFilePath);
        out.println(gamessInput);
        out.close();
        List<ApplicationInterfaceDescription> applicationInterfaceDescriptions = AiravataManager.getInstance().getAllApplicationInterfaces();
        List<ApplicationInterfaceDescription> gamessApps = new ArrayList<>();
        for(ApplicationInterfaceDescription appInter : applicationInterfaceDescriptions){
            if(appInter.getApplicationName().toLowerCase().startsWith(SEAGridContext.getInstance().getGamessAppName())){
                gamessApps.add(appInter);
            }
        }
        if(gamessApps.size() > 0){
            expCreateAppField.getItems().setAll(gamessApps);
            expCreateAppField.getSelectionModel().select(0);
            for(ApplicationInterfaceDescription app : gamessApps){
                List<InputDataObjectType> gaussianInputs = app.getApplicationInputs();
                gaussianInputs.get(0).setValue(tempFilePath);
                updateExperimentInputs(gaussianInputs, true);
            }
        }
    }

    public void initNwchemExperiment(String nwchemInput) throws FileNotFoundException, TException, URISyntaxException {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + "input.nw";
        PrintWriter out = new PrintWriter(tempFilePath);
        out.println(nwchemInput);
        out.close();
        List<ApplicationInterfaceDescription> applicationInterfaceDescriptions = AiravataManager.getInstance().getAllApplicationInterfaces();
        List<ApplicationInterfaceDescription> nwchemApps = new ArrayList<>();
        for(ApplicationInterfaceDescription appInter : applicationInterfaceDescriptions){
            if(appInter.getApplicationName().toLowerCase().startsWith(SEAGridContext.getInstance().getNwchemAppName())){
                nwchemApps.add(appInter);
            }
        }
        if(nwchemApps.size() > 0){
            expCreateAppField.getItems().setAll(nwchemApps);
            expCreateAppField.getSelectionModel().select(0);
            for(ApplicationInterfaceDescription app : nwchemApps){
                List<InputDataObjectType> gaussianInputs = app.getApplicationInputs();
                gaussianInputs.get(0).setValue(tempFilePath);
                updateExperimentInputs(gaussianInputs, true);
            }
        }
    }

    public void initPsi4Experiment(String processors, String psi4Input) throws FileNotFoundException, TException, URISyntaxException {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + "input.dat";
        PrintWriter out = new PrintWriter(tempFilePath);
        out.println(psi4Input);
        out.close();
        List<ApplicationInterfaceDescription> applicationInterfaceDescriptions = AiravataManager.getInstance().getAllApplicationInterfaces();
        List<ApplicationInterfaceDescription> psi4Apps = new ArrayList<>();
        for(ApplicationInterfaceDescription appInter : applicationInterfaceDescriptions){
            if(appInter.getApplicationName().toLowerCase().startsWith(SEAGridContext.getInstance().getPsi4AppName())){
                psi4Apps.add(appInter);
            }
        }
        if(psi4Apps.size() > 0){
            expCreateAppField.getItems().setAll(psi4Apps);
            expCreateAppField.getSelectionModel().select(0);
            for(ApplicationInterfaceDescription app : psi4Apps){
                List<InputDataObjectType> gaussianInputs = app.getApplicationInputs();
                gaussianInputs.get(0).setValue(processors);
                gaussianInputs.get(1).setValue(tempFilePath);
                updateExperimentInputs(gaussianInputs, true);
            }
        }
    }
    public void initMolcasExperiment(String processors, String molcasInput) throws FileNotFoundException, TException, URISyntaxException {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + "molcas.input";
        PrintWriter out = new PrintWriter(tempFilePath);
        out.println(molcasInput);
        out.close();
        List<ApplicationInterfaceDescription> applicationInterfaceDescriptions = AiravataManager.getInstance().getAllApplicationInterfaces();
        List<ApplicationInterfaceDescription> molcasApps = new ArrayList<>();
        for(ApplicationInterfaceDescription appInter : applicationInterfaceDescriptions){
            if(appInter.getApplicationName().toLowerCase().startsWith(SEAGridContext.getInstance().getMolcasAppName())){
                molcasApps.add(appInter);
            }
        }
        if(molcasApps.size() > 0){
            expCreateAppField.getItems().setAll(molcasApps);
            expCreateAppField.getSelectionModel().select(0);
            for(ApplicationInterfaceDescription app : molcasApps){
                List<InputDataObjectType> gaussianInputs = app.getApplicationInputs();
                gaussianInputs.get(1).setValue(processors);
                gaussianInputs.get(0).setValue(tempFilePath);
                updateExperimentInputs(gaussianInputs, true);
            }
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
                String computeResourceId = ((ComputeResourceDescription)newValue).getComputeResourceId();
                try{
                    UserComputeResourcePreference userComputeResourcePreference = AiravataManager.getInstance()
                            .getUserComputeResourcePrefs(computeResourceId);
                    if(userComputeResourcePreference != null){
                        useMyCRAccount.setSelected(true);
                        useMyCRAccount.setVisible(true);
                    }else{
                        useMyCRAccount.setSelected(false);
                        useMyCRAccount.setVisible(false);
                    }
                }catch (Exception ex){
                    SEAGridDialogHelper.showExceptionDialog(ex, "Failed while retrieving user compute prefs", null, ex.getMessage());
                }

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

    private void updateExperimentInputs(List<InputDataObjectType> inputDataObjectTypes, boolean hasOptionalFileInputs) throws TException, URISyntaxException {
        if(hasOptionalFileInputs){
            if(!inputDataObjectTypes.stream().filter(p->p.getType().equals(DataType.URI_COLLECTION)).findFirst().isPresent()){
                InputDataObjectType inputDataObjectType = new InputDataObjectType();
                inputDataObjectType.setName("Optional-File-Inputs");
                inputDataObjectType.setType(DataType.URI_COLLECTION);
                inputDataObjectType.setIsRequired(false);
                inputDataObjectTypes.add(inputDataObjectType);
            }
        }
        this.experimentInputs = new HashMap<>();
        expCreateInputsGridPane.getChildren().clear();
        expCreateInputsGridPane.getRowConstraints().clear();
        int index = 0;
        for(InputDataObjectType inputDataObjectType : inputDataObjectTypes){
            String lable = inputDataObjectType.getName();
            String toolTip = "";
            if(inputDataObjectType.getUserFriendlyDescription() != null){
                toolTip = inputDataObjectType.getUserFriendlyDescription();
            }
            Label labelObj = new Label(lable);
            labelObj.setTooltip(new Tooltip(toolTip));
            if(inputDataObjectType.getType().equals(DataType.STRING)){
                expCreateInputsGridPane.add(labelObj, 0, index);
                TextField stringField = new TextField();
                expCreateInputsGridPane.add( stringField, 1, index);
                this.experimentInputs.put(inputDataObjectType,null);
                stringField.textProperty().addListener((observable, oldValue, newValue) -> {
                    experimentInputs.put(inputDataObjectType,newValue);
                });
                if(inputDataObjectType.getValue() != null)
                    stringField.setText(inputDataObjectType.getValue());
            }else if(inputDataObjectType.getType().equals(DataType.INTEGER)){
                expCreateInputsGridPane.add(labelObj, 0, index);
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
                expCreateInputsGridPane.add(labelObj, 0, index);
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
                expCreateInputsGridPane.add(labelObj, 0, index);
                HBox hBox = new HBox(2);
                Button localFilePickBtn = new ImageButton("/images/local-storage.png");
                localFilePickBtn.setTooltip(new Tooltip("Select local file"));
                hBox.getChildren().add(0, localFilePickBtn);
                Button remoteFilePickBtn = new ImageButton("/images/remote-storage.png");
                remoteFilePickBtn.setTooltip(new Tooltip("Select remote file"));
//                hBox.getChildren().add(1, remoteFilePickBtn);
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
                if(inputDataObjectType.getValue() != null && inputDataObjectType.getValue().startsWith("airavata-dp")){
                    List<DataReplicaLocationModel> replicas = AiravataManager.getInstance().getDataReplicas(inputDataObjectType.getValue());
                    String fileUri = "";
                    for(DataReplicaLocationModel rpModel : replicas){
                        if(rpModel.getReplicaLocationCategory().equals(ReplicaLocationCategory.GATEWAY_DATA_STORE)) {
                            fileUri = rpModel.getFilePath();
                            break;
                        }
                    }
                    String filePath = (new URI(fileUri)).getPath();
                    handleExperimentFileSelect(inputDataObjectType, hBox, localFilePickBtn, remoteFilePickBtn, new File(filePath));
                }else{
                    File file = new File(inputDataObjectType.getValue());
                    if(file.exists()){
                        handleExperimentFileSelect(inputDataObjectType, hBox, localFilePickBtn, remoteFilePickBtn, new File(inputDataObjectType.getValue()));
                    }
                }
            }else if(inputDataObjectType.getType().equals(DataType.URI_COLLECTION)){
                expCreateInputsGridPane.add(labelObj, 0, index);
                HBox hBox = new HBox(2);
                Button localFilePickBtn = new ImageButton("/images/local-storage.png");
                localFilePickBtn.setTooltip(new Tooltip("Select multiple local files"));
                hBox.getChildren().add(0, localFilePickBtn);
                expCreateInputsGridPane.add(hBox, 1, index);

                Button remoteFilePickBtn = new ImageButton("/images/remote-storage.png");
                remoteFilePickBtn.setTooltip(new Tooltip("Select remote file"));
//                hBox.getChildren().add(1, remoteFilePickBtn);

                localFilePickBtn.setOnAction(event -> {
                    List<File> selectedFiles = fileChooser.showOpenMultipleDialog(expCreateInputsGridPane.getScene().getWindow());
                    handleMultipleExperimentFileSelect(inputDataObjectType, hBox, localFilePickBtn, remoteFilePickBtn, selectedFiles);
                });

                if(inputDataObjectType.getValue() != null && inputDataObjectType.getValue().contains("airavata-dp")){
                    String[] replicaUris = inputDataObjectType.getValue().split(",");
                    ArrayList<File> fileList = new ArrayList<>();
                    for(String replicaUri : replicaUris) {
                        List<DataReplicaLocationModel> replicas = AiravataManager.getInstance().getDataReplicas(replicaUri);
                        String fileUri = "";
                        for (DataReplicaLocationModel rpModel : replicas) {
                            if (rpModel.getReplicaLocationCategory().equals(ReplicaLocationCategory.GATEWAY_DATA_STORE)) {
                                fileUri = rpModel.getFilePath();
                                break;
                            }
                        }
                        String filePath = (new URI(fileUri)).getPath();
                        fileList.add(new File(filePath));
                    }
                    handleMultipleExperimentFileSelect(inputDataObjectType, hBox, localFilePickBtn, remoteFilePickBtn, fileList);
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
                        logger.error("Cannot open file. Opening parent directory");
                        try {
                            Desktop.getDesktop().open(selectedFile.getParentFile());
                        } catch (IOException e1) {
                            logger.error("Cannot open file. Opening parent directory");
                            SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog",
                                    expCreateInputsGridPane.getScene().getWindow(), "Failed Opening File");
                        }
                    }
                }else{
                    boolean result = SEAGridDialogHelper.showConfirmDialog("Confirm Action", "Remote File Download", "You have selected a remote file." +
                            " Do you want to download it ?");
                    if(result){
                        try {
                            String filePath = selectedFile.getPath();
                            filePath= filePath.replace("\\", "/");
                            String remotePath = filePath.replaceAll(remoteDataDirRoot, "");
                            downloadFile(Paths.get(remotePath), System.getProperty("java.io.tmpdir"));
                        } catch (Exception e) {
                            SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog",
                                    expCreateInputsGridPane.getScene().getWindow(), "Failed Downloading File");
                        }
                    }
                }
            });
            hBox.getChildren().add(0, hyperlink);
            hBox.getChildren().add(1, localFilePickBtn);
//            hBox.getChildren().add(2, remoteFilePickBtn);
            experimentInputs.put(inputDataObjectType, selectedFile);
        }
    }

    private void handleMultipleExperimentFileSelect(InputDataObjectType inputDataObjectType, HBox hBox, Button localFilePickBtn,
                                            Button remoteFilePickBtn,List<File> selectedFiles){
        List<File> nonNullSelectedFiles = new ArrayList<>();
        int i = 0;
        hBox.getChildren().clear();
        for (File selectedFile : selectedFiles){
            if (selectedFile != null) {
                Hyperlink hyperlink = new Hyperlink(selectedFile.getName());
                hyperlink.setOnAction(hyperLinkEvent -> {
                    //FIXME Else it is a remote file. Cannot open locally without downloading it.
                    if (selectedFile.exists()) {
                        try {
                            Desktop.getDesktop().open(selectedFile);
                        } catch (IOException e) {
                            logger.error("Cannot open file. Opening parent directory");
                            try {
                                Desktop.getDesktop().open(selectedFile.getParentFile());
                            } catch (IOException e1) {
                                logger.error("Cannot open file. Opening parent directory");
                                SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog",
                                        expCreateInputsGridPane.getScene().getWindow(), "Failed Opening File");
                            }
                        }
                    } else {
                        boolean result = SEAGridDialogHelper.showConfirmDialog("Confirm Action", "Remote File Download", "You have selected a remote file." +
                                " Do you want to download it ?");
                        if (result) {
                            try {
                                String filePath = selectedFile.getPath();
                                String remotePath = filePath.replaceAll(remoteDataDirRoot, "");
                                downloadFile(Paths.get(remotePath), System.getProperty("java.io.tmpdir"));
                            } catch (Exception e) {
                                SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog",
                                        expCreateInputsGridPane.getScene().getWindow(), "Failed Downloading File");
                            }
                        }
                    }
                });
                hBox.getChildren().add(i, hyperlink);
                i++;
                nonNullSelectedFiles.add(selectedFile);
            }
        }
        hBox.getChildren().add(i, localFilePickBtn);
        experimentInputs.put(inputDataObjectType, nonNullSelectedFiles);

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

    private void createExperiment(boolean launch) throws TException {
        if(validateExperimentFields()){
            //FIXME Hardcoded value
            String projectId = ((Project)expCreateProjField.getSelectionModel().getSelectedItem()).getProjectID();
            String randomString = projectId.substring(0,projectId.length()-37).replaceAll("[^A-Za-z0-9 ]", "_") + "/"
                    + expCreateNameField.getText().replaceAll("[^A-Za-z0-9]","_")+"."+System.currentTimeMillis();
            String remoteDataDir = SEAGridContext.getInstance().getRemoteDataDirPrefix() + remoteDataDirRoot + randomString  + "/";
            ExperimentModel experimentModel = assembleExperiment(remoteDataDir, randomString + "/");
            Map<String,File> uploadFiles = new HashMap<>();
            for(Iterator<Map.Entry<InputDataObjectType, Object>> it = experimentInputs.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<InputDataObjectType, Object> entry = it.next();
                if(entry.getKey().getType().equals(DataType.URI)) {
                    File file = (File) entry.getValue();
                    //FIXME - Otherwise the file is remote file. This is not a good way to handle this. Should find a better way to handle it
                    if(file.exists())
                        uploadFiles.put("/" + randomString + "/" + file.getName(), file);
                }else if(entry.getKey().getType().equals(DataType.URI_COLLECTION)){
                    for(File file : (List<File>)entry.getValue()){
                        //FIXME - Otherwise the file is remote file. This is not a good way to handle this. Should find a better way to handle it
                        if(file.exists())
                            uploadFiles.put("/" + randomString + "/" + file.getName(), file);
                    }
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

    private ExperimentModel assembleExperiment(String remoteDataDir, String experimentDataDir) throws TException {
        ExperimentModel experimentModel = new ExperimentModel();
        experimentModel.setExperimentName(expCreateNameField.getText());
        experimentModel.setDescription(expCreateDescField.getText() == null ? "" : expCreateDescField.getText());
        experimentModel.setProjectId(((Project)expCreateProjField.getSelectionModel().getSelectedItem()).getProjectID());
        experimentModel.setExecutionId(((ApplicationInterfaceDescription)expCreateAppField.getSelectionModel()
                .getSelectedItem()).getApplicationInterfaceId());

        UserPrefs userPrefs = SEAGridContext.getInstance().getUserPrefs();
        userPrefs.setLastApplicationId(((ApplicationInterfaceDescription) expCreateAppField.getSelectionModel()
                .getSelectedItem()).getApplicationInterfaceId());

        experimentModel.setGatewayId(SEAGridContext.getInstance().getAiravataGatewayId());
        experimentModel.setUserName(SEAGridContext.getInstance().getUserName());

        UserConfigurationDataModel userConfigurationDataModel = new UserConfigurationDataModel();

        //FIXME Hard Coded Default Values
        userConfigurationDataModel.setAiravataAutoSchedule(false);
        userConfigurationDataModel.setOverrideManualScheduledParams(false);
        userConfigurationDataModel.setStorageId(SEAGridContext.getInstance().getGatewayaStorageId());
        userConfigurationDataModel.setExperimentDataDir(remoteDataDirRoot + experimentDataDir);
        userConfigurationDataModel.setUseUserCRPref(useMyCRAccount.isSelected());

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
                    String fileName = ((File) this.experimentInputs.get(inputDataObjectType)).getName();
                    String remoteFilePath = remoteDataDir + fileName;
                    DataProductModel dpModel = new DataProductModel();
                    dpModel.setGatewayId(SEAGridContext.getInstance().getAiravataGatewayId());
                    dpModel.setOwnerName(SEAGridContext.getInstance().getUserName());
                    dpModel.setProductName(fileName);
                    dpModel.setDataProductType(DataProductType.FILE);

                    DataReplicaLocationModel rpModel = new DataReplicaLocationModel();
                    rpModel.setStorageResourceId(SEAGridContext.getInstance().getGatewayaStorageId());
                    rpModel.setReplicaName(fileName + " gateway data store copy");
                    rpModel.setReplicaLocationCategory(ReplicaLocationCategory.GATEWAY_DATA_STORE);
                    rpModel.setReplicaPersistentType(ReplicaPersistentType.TRANSIENT);
                    rpModel.setFilePath(remoteFilePath);
                    dpModel.addToReplicaLocations(rpModel);
                    String uri = AiravataManager.getInstance().registerDataProduct(dpModel);
                    inputDataObjectType.setValue(uri);
                }
            }else if(inputDataObjectType.getType().equals(DataType.URI_COLLECTION)){
                List<File> files = (List<File>) this.experimentInputs.get(inputDataObjectType);
                //FIXME - Otherwise the files are remote file. This is not a good way to handle this. Should find a better way to handle it
                if(files.get(0).exists()){
                    String uriCollection = "";
                    for(File file : files){
                        if(file.exists()) {
                            String fileName = file.getName();
                            String remoteFilePath = remoteDataDir + fileName;
                            DataProductModel dpModel = new DataProductModel();
                            dpModel.setGatewayId(SEAGridContext.getInstance().getAiravataGatewayId());
                            dpModel.setOwnerName(SEAGridContext.getInstance().getUserName());
                            dpModel.setProductName(fileName);
                            dpModel.setDataProductType(DataProductType.FILE);

                            DataReplicaLocationModel rpModel = new DataReplicaLocationModel();
                            rpModel.setStorageResourceId(SEAGridContext.getInstance().getGatewayaStorageId());
                            rpModel.setReplicaName(fileName + " gateway data store copy");
                            rpModel.setReplicaLocationCategory(ReplicaLocationCategory.GATEWAY_DATA_STORE);
                            rpModel.setReplicaPersistentType(ReplicaPersistentType.TRANSIENT);
                            rpModel.setFilePath(remoteFilePath);
                            dpModel.addToReplicaLocations(rpModel);
                            String uri = AiravataManager.getInstance().registerDataProduct(dpModel);
                            uriCollection = uriCollection + uri + ",";
                        }
                    }
                    uriCollection = uriCollection.substring(0, uriCollection.length() -1);
                    inputDataObjectType.setValue(uriCollection);
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
                if (wallTime <= 0 || wallTime > selectedQueue.getMaxRunTime()){
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
                if(inputDataObjectType.isIsRequired() && (object == null || object.toString().equals(""))){
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