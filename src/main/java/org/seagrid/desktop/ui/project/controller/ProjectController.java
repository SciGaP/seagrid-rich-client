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
package org.seagrid.desktop.ui.project.controller;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.airavata.model.workspace.Project;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectController {
    private final static Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @FXML
    public TextArea projectDescField;

    @FXML
    public TextField projectNameField;

    @FXML
    private Button saveButton;

    public void initialize() {
        BooleanBinding projectNameBind = new BooleanBinding(){
            {super.bind(projectNameField.textProperty());}

            @Override
            protected boolean computeValue() {
                return (projectNameField.getText().isEmpty());
            }
        };
        saveButton.disableProperty().bind(projectNameBind);

        saveButton.setOnMouseClicked(event -> {
            try {
                Project project = AiravataManager.getInstance().createProject(
                        projectNameField.getText(),projectDescField.getText());
                Stage stage = (Stage) saveButton.getScene().getWindow();
                stage.close();
                SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.PROJECT_CREATED,project));
            } catch (Exception e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", saveButton.getScene().getWindow(),
                        "Failed tp save project !");
            }
        });
    }
}