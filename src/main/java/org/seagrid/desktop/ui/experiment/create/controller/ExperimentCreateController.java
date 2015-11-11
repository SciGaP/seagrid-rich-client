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
import javafx.util.StringConverter;
import org.apache.airavata.model.appcatalog.appinterface.ApplicationInterfaceDescription;
import org.apache.airavata.model.appcatalog.computeresource.BatchQueue;
import org.apache.airavata.model.appcatalog.computeresource.ComputeResourceDescription;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.workspace.Project;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
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

        }catch (Exception e){

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
            expCreateNodeCountLabel.setText(expCreateNodeCountLabel.getText() + " ( max - " + selectedQueue.getMaxNodes()
                    + " )");
            expCreateCoreCountLabel.setText(expCreateCoreCountLabel.getText() + " ( max - " + selectedQueue.getMaxProcessors()
                    + " )");
            expCreateWallTimeLabel.setText(expCreateWallTimeLabel.getText() + " ( max - " + selectedQueue.getMaxRunTime()
                    + " )");
        }
    }
}