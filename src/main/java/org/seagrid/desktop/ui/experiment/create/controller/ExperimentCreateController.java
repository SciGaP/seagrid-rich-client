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

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.airavata.model.appcatalog.appinterface.ApplicationInterfaceDescription;
import org.apache.airavata.model.appcatalog.computeresource.BatchQueue;
import org.apache.airavata.model.appcatalog.computeresource.ComputeResourceDescription;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.experiment.UserConfigurationDataModel;
import org.apache.airavata.model.scheduling.ComputationalResourceSchedulingModel;
import org.apache.airavata.model.workspace.Project;
import org.apache.thrift.TException;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ExperimentCreateController {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentCreateController.class);

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
    private TextField expCreateStaticDirField;

    @FXML
    private Button expSaveButton;

    @FXML
    private Button expSaveLaunchButton;

    @SuppressWarnings("unused")
    public void initialize() {
        initElements();
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
                } catch (AiravataClientException e) {
                    e.printStackTrace();
                }
            });
            expCreateAppField.getSelectionModel().selectFirst();

            //Won't allow characters to be entered
            expCreateNodeCountField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    expCreateNodeCountField.setText(oldValue);
                }
            });
            expCreateNodeCountField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    expCreateNodeCountField.setText(oldValue);
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
                ExperimentModel experimentModel = null;
                try {
                    experimentModel = createExperiment();
                    if(experimentModel != null && experimentModel.getExperimentId() != null
                            && !experimentModel.getExperimentId().isEmpty()){
                        Stage stage = (Stage) expSaveButton.getScene().getWindow();
                        stage.close();
                        SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_CREATED
                                ,experimentModel));
                    }
                } catch (TException e) {
                    e.printStackTrace();
                    SEAGridDialogHelper.showExceptionDialog(e,"Exception Dialog", expSaveButton.getScene().getWindow(),
                            "Experiment create failed !");
                }
            });

            //Todo
            expSaveLaunchButton.setOnAction(event -> {
                ExperimentModel experimentModel = null;
                try {
                    experimentModel = createExperiment();
                    if(experimentModel != null && experimentModel.getExperimentId() == null
                            && !experimentModel.getExperimentId().isEmpty()){
                        Stage stage = (Stage) expSaveButton.getScene().getWindow();
                        stage.close();
                        SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_CREATED
                                ,experimentModel));
                    }
                } catch (TException e) {
                    e.printStackTrace();
                    SEAGridDialogHelper.showExceptionDialog(e,"Exception Dialog", expSaveButton.getScene().getWindow(),
                            "Experiment create failed !");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            SEAGridDialogHelper.showExceptionDialog(e,"Exception Dialog", expCreateNameField.getScene().getWindow(),
                    "Failed To Load New Experiment Window !");
        }
    }

    private void loadAvailableComputeResources() throws AiravataClientException {
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

    private ExperimentModel createExperiment() throws TException {
        if(validateExperimentFields()){
            ExperimentModel experimentModel = new ExperimentModel();
            experimentModel.setExperimentName(expCreateNameField.getText());
            experimentModel.setDescription(expCreateDescField.getText() == null ? "" : expCreateDescField.getText());
            experimentModel.setProjectId(((Project)expCreateProjField.getSelectionModel().getSelectedItem()).getProjectID());
            experimentModel.setExecutionId(((ApplicationInterfaceDescription)expCreateAppField.getSelectionModel()
                    .getSelectedItem()).getApplicationInterfaceId());
            experimentModel.setGatewayId(SEAGridContext.getInstance().getAiravataGatewayId());
            experimentModel.setUserName(SEAGridContext.getInstance().getUserName());

            UserConfigurationDataModel userConfigurationDataModel = new UserConfigurationDataModel();
            userConfigurationDataModel.setAiravataAutoSchedule(false);
            userConfigurationDataModel.setOverrideManualScheduledParams(false);
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

            String expId = AiravataManager.getInstance().createExperiment(experimentModel);
            experimentModel.setExperimentId(expId);
            return experimentModel;
        }
        return null;
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

        return true;
    }
}