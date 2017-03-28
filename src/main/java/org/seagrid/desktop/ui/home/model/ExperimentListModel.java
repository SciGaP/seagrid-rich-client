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
package org.seagrid.desktop.ui.home.model;

import com.google.common.eventbus.Subscribe;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.util.Duration;
import org.apache.airavata.model.appcatalog.appinterface.ApplicationInterfaceDescription;
import org.apache.airavata.model.appcatalog.computeresource.ComputeResourceDescription;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.apache.thrift.TException;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ExperimentListModel {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentListModel.class);
    private static final double EXPERIMENT_LIST_UPDATE_INTERVAL = 10000;

    private StringProperty id;
    private StringProperty projectId;
    private BooleanProperty checked;
    private StringProperty name;
    private StringProperty ownerName;
    private StringProperty application;
    private StringProperty host;
    private StringProperty status;
    private ObjectProperty<LocalDateTime> createdTime;

    private Timeline expStatusUpdateTimer = null;

    public ExperimentListModel(StringProperty id, StringProperty projectId, BooleanProperty checked, StringProperty name, StringProperty ownerName,
                               StringProperty application, StringProperty host,
                               StringProperty status, ObjectProperty<LocalDateTime> createdTime) {
        this.id = id;
        this.projectId = projectId;
        this.checked = checked;
        this.name = name;
        this.ownerName = ownerName;
        this.application = application;
        this.host = host;
        this.status = status;
        this.createdTime = createdTime;
    }

    public ExperimentListModel(){
        this.checked = new SimpleBooleanProperty(false);
        this.id = new SimpleStringProperty("test-id");
        this.projectId = new SimpleStringProperty("test-proj-id");
        this.name = new SimpleStringProperty("test-name");
        this.ownerName = new SimpleStringProperty("owner-name");
        this.application = new SimpleStringProperty("test-application");
        this.host = new SimpleStringProperty("test-host");
        this.status = new SimpleStringProperty("test-status");
        this.createdTime = new SimpleObjectProperty<>(LocalDateTime.now());
    }

    public ExperimentListModel(ExperimentSummaryModel experimentSummaryModel) throws TException {
        this.id = new SimpleStringProperty(experimentSummaryModel.getExperimentId());
        this.projectId = new SimpleStringProperty(experimentSummaryModel.getProjectId());
        this.checked = new SimpleBooleanProperty();
        this.name = new SimpleStringProperty(experimentSummaryModel.getName());
        this.ownerName = new SimpleStringProperty(experimentSummaryModel.getUserName());
        if(experimentSummaryModel.getResourceHostId()!=null){
            ComputeResourceDescription resourceDescription = null;
            try {
                resourceDescription = AiravataManager.getInstance().getComputeResource(experimentSummaryModel.getResourceHostId());
                if(resourceDescription != null){
                    this.host = new SimpleStringProperty(resourceDescription.getHostName());
                }
            } catch (AiravataClientException e) {
                e.printStackTrace();
            }
        }
        if(experimentSummaryModel.getExecutionId()!=null){
            ApplicationInterfaceDescription interfaceDescription = null;
            try {
                interfaceDescription = AiravataManager.getInstance().getApplicationInterface(experimentSummaryModel.getExecutionId());
                if(interfaceDescription != null){
                    this.application = new SimpleStringProperty(interfaceDescription.getApplicationName());
                }
            } catch (Exception e) {
                logger.error("Failed to load application interface for id: "+ experimentSummaryModel.getExecutionId());
            }
        }
        this.status = new SimpleStringProperty(experimentSummaryModel.getExperimentStatus());
        this.createdTime = new SimpleObjectProperty<>(LocalDateTime.ofEpochSecond(experimentSummaryModel
                .getCreationTime() / 1000, 0, SEAGridContext.getInstance().getTimeZoneOffset()));

        //TODO this should replace with a RabbitMQ Listener
        if(!(status.equals("FAILED") || getStatus().equals("COMPLETED") || getStatus().equals("CANCELLED"))) {
            expStatusUpdateTimer = new Timeline(new KeyFrame(
                    Duration.millis(EXPERIMENT_LIST_UPDATE_INTERVAL),
                    ae -> updateExperimentStatuses()));
            expStatusUpdateTimer.setCycleCount(Timeline.INDEFINITE);
            expStatusUpdateTimer.play();
        }

        SEAGridEventBus.getInstance().register(this);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getProjectId() {
        return projectId.get();
    }

    public StringProperty projectIdProperty() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId.set(projectId);
    }

    public boolean getChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getOwnerName() {
        return ownerName.get();
    }

    public StringProperty ownerNameProperty() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName.set(ownerName);
    }

    public String getApplication() {
        return application.get();
    }

    public StringProperty applicationProperty() {
        return application;
    }

    public void setApplication(String application) {
        this.application.set(application);
    }

    public String getHost() {
        return host.get();
    }

    public StringProperty hostProperty() {
        return host;
    }

    public void setHost(String host) {
        this.host.set(host);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public LocalDateTime getCreatedTime() {
        return createdTime.get();
    }

    public ObjectProperty<LocalDateTime> createdTimeProperty() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime.set(createdTime);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void listenSEAGridEvents(SEAGridEvent event){
        if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_DELETED)) {
            if(event.getPayload() instanceof ExperimentListModel){
                ExperimentListModel experimentListModel = (ExperimentListModel) event.getPayload();
                if(getId().equals(experimentListModel.getId())){
                    this.expStatusUpdateTimer.stop();
                }
            }else if(event.getPayload() instanceof  ExperimentModel){
                ExperimentModel deletedExpModel = (ExperimentModel) event.getPayload();
                if(getId().equals(deletedExpModel.getExperimentId())){
                    this.expStatusUpdateTimer.stop();
                }
            }
        }else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_UPDATED)) {
            if(event.getPayload() instanceof  ExperimentModel){
                ExperimentModel updatedExperimentModel = (ExperimentModel) event.getPayload();
                if(getId().equals(updatedExperimentModel.getExperimentId())){
                    try{
                        this.setProjectId(updatedExperimentModel.getProjectId());
                        this.setName(updatedExperimentModel.getExperimentName());
                        ComputeResourceDescription host  = AiravataManager.getInstance()
                                .getComputeResource(updatedExperimentModel.getUserConfigurationData()
                                        .getComputationalResourceScheduling().getResourceHostId());
                        this.setHost(host.getHostName());
                    }catch (Exception e){
                        e.printStackTrace();
                        SEAGridDialogHelper.showExceptionDialog(e,"Exception Dialog", null, "Failed updating experiment" +
                                " information in ExperimentListModel");
                    }
                }
            }
        }
    }

    //updates the experiment status in the background
    private void updateExperimentStatuses(){
        if(!(status.equals("FAILED") || getStatus().equals("COMPLETED") || getStatus().equals("CANCELLED"))) {
            Platform.runLater(() -> {
                String experimentId = id.getValue();
                try {
                    ExperimentModel experimentModel = AiravataManager.getInstance().getExperiment(experimentId);
                    if(experimentModel.getExperimentStatus() != null) {
                        this.setStatus(experimentModel.getExperimentStatus().get(0).getState().toString());
                    }
                    logger.debug("Updated Experiment Status for :" + experimentId);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(this.expStatusUpdateTimer != null)
                        expStatusUpdateTimer.stop();
                }
            });
        }else{
            expStatusUpdateTimer.stop();
        }
    }
}