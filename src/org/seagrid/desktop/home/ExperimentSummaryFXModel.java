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
package org.seagrid.desktop.home;

import javafx.beans.property.*;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ExperimentSummaryFXModel {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentSummaryFXModel.class);

    private final BooleanProperty checked;
    private final StringProperty name;
    private final StringProperty application;
    private final StringProperty host;
    private final StringProperty status;
    private final ObjectProperty<LocalDateTime> createdTime;

    public ExperimentSummaryFXModel(BooleanProperty checked, StringProperty name, StringProperty application, StringProperty host,
                                    StringProperty status, ObjectProperty<LocalDateTime> createdTime) {
        this.checked = checked;
        this.name = name;
        this.application = application;
        this.host = host;
        this.status = status;
        this.createdTime = createdTime;
    }

    public ExperimentSummaryFXModel(){
        this.checked = new SimpleBooleanProperty(false);
        this.name = new SimpleStringProperty("test-name");
        this.application = new SimpleStringProperty("test-application");
        this.host = new SimpleStringProperty("test-host");
        this.status = new SimpleStringProperty("test-status");
        this.createdTime = new SimpleObjectProperty<>(LocalDateTime.now());
    }

    public ExperimentSummaryFXModel(ExperimentSummaryModel experimentSummaryModel){
        this.checked = new SimpleBooleanProperty();
        this.name = new SimpleStringProperty(experimentSummaryModel.getName());
        this.application = new SimpleStringProperty(experimentSummaryModel.getExecutionId().substring(0,
                experimentSummaryModel.getExecutionId().length()-37));
        this.host = new SimpleStringProperty(experimentSummaryModel.getResourceHostId().substring(0,
                experimentSummaryModel.getResourceHostId().length()-37));
        this.status = new SimpleStringProperty(experimentSummaryModel.getExperimentStatus());
        this.createdTime = new SimpleObjectProperty<>(LocalDateTime.ofEpochSecond(experimentSummaryModel
                .getCreationTime() / 1000, 0, ZoneOffset.UTC));
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
}