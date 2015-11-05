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


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentSearchFields;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.apache.airavata.model.status.ExperimentState;
import org.apache.airavata.model.workspace.Project;
import org.seagrid.desktop.apis.airavata.AiravataManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Controls the home screen */
public class HomeController {

    @FXML
    private ListView<Project> projectsListView;

    ObservableList observableProjectList = FXCollections.observableArrayList();

    @FXML
    private TableView<ExperimentSummaryFXModel> expSummaryTable;

    @FXML
    private TableColumn<ExperimentSummaryFXModel, Boolean> expCheckedColumn;

    @FXML
    private TableColumn<ExperimentSummaryFXModel, String> expApplicationColumn;

    @FXML
    private TableColumn<ExperimentSummaryFXModel, String> expHostColumn;

    @FXML
    private TableColumn<ExperimentSummaryFXModel, String> expStatusColumn;

    @FXML
    private TableColumn<ExperimentSummaryFXModel, String> expNameColumn;

    @FXML
    private TableColumn<ExperimentSummaryFXModel, LocalDateTime> expCreateTimeColumn;

    @FXML
    private CheckBox checkAllExps;

    public void initialize() {
        // Initialize the experiment table
        expSummaryTable.setEditable(true);

        expCheckedColumn.setCellValueFactory(new PropertyValueFactory<>("checked"));
        expCheckedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(expCheckedColumn));
        expCheckedColumn.setEditable(true);
        expNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        expApplicationColumn.setCellValueFactory(cellData -> cellData.getValue().applicationProperty());
        expHostColumn.setCellValueFactory(cellData -> cellData.getValue().hostProperty());
        expStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        expStatusColumn.setCellFactory(new Callback<TableColumn<ExperimentSummaryFXModel, String>,
                TableCell<ExperimentSummaryFXModel, String>>() {
            @Override
            public TableCell<ExperimentSummaryFXModel, String> call(TableColumn<ExperimentSummaryFXModel, String> param) {
                return new TableCell<ExperimentSummaryFXModel, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        if(!empty){
                            if (item.equals(ExperimentState.COMPLETED.toString())) {
                                this.setTextFill(Color.GREEN);
                            }else if(item.equals(ExperimentState.FAILED.toString())){
                                this.setTextFill(Color.RED);
                            }else if(item.equals(ExperimentState.CREATED.toString())){
                                this.setTextFill(Color.BLUE);
                            }else{
                                this.setTextFill(Color.ORANGE);
                            }
                        }

                    }
                };
            }
        });
        expCreateTimeColumn.setCellValueFactory(cellData -> cellData.getValue().createdTimeProperty());

        checkAllExps.setOnMouseClicked(event -> handleCheckAllExperiments());

        // Initialize the project list
        projectsListView.setCellFactory(new Callback<ListView<Project>, ListCell<Project>>() {
            @Override
            public ListCell<Project> call(ListView<Project> param) {
                ListCell<Project> cell = new ListCell<Project>() {
                    @Override
                    protected void updateItem(Project t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        }
                    }
                };
                return cell;
            }
        });

        projectsListView.setOnMouseClicked(event -> {
            Project project = projectsListView.getSelectionModel().getSelectedItem();
            if(project != null){
                Map<ExperimentSearchFields,String> filters = new HashMap<ExperimentSearchFields, String>();
                if(!project.getProjectID().startsWith("$$$$$$$")){
                    filters.put(ExperimentSearchFields.PROJECT_ID,project.getProjectID());
                    updateExperimentList(filters,-1,0);
                }else{
                    updateExperimentList(filters,-1,0);
                }
            }
        });

        initProjectList();
        initExperimentList();
    }

    //init the left pane with project list
    public void initProjectList(){
        try {
            List<Project> projectList = AiravataManager.getInstance().getProjects();
            observableProjectList = FXCollections.observableArrayList();
            observableProjectList.add(new Project("$$$$$$$$","dummy-user","last 100 experiments"));
            observableProjectList.addAll(projectList);
            projectsListView.setItems(observableProjectList);
            projectsListView.getSelectionModel().select(0);
        } catch (AiravataClientException e) {
            e.printStackTrace();
        }
    }

    //init the right pane with experiment list
    public void initExperimentList(){
        Map<ExperimentSearchFields,String> filters = new HashMap<>();
        updateExperimentList(filters,-1,0);
    }

    public void handleCheckAllExperiments(){
        if(checkAllExps.isSelected()){
            for(ExperimentSummaryFXModel experimentSummaryFXModel : expSummaryTable.getItems()){
                experimentSummaryFXModel.setChecked(true);
            }
        }else{
            for(ExperimentSummaryFXModel experimentSummaryFXModel : expSummaryTable.getItems()){
                experimentSummaryFXModel.setChecked(false);
            }
        }
    }

    //update the right pane with experiment list
    public void updateExperimentList(Map<ExperimentSearchFields, String> filters, int limit, int offset){
        try {
            List<ExperimentSummaryModel> experimentSummaryModelList = AiravataManager.getInstance()
                    .getExperimentSummaries(filters, limit, offset);
            ObservableList<ExperimentSummaryFXModel> experimentSummaryFXModels = FXCollections.observableArrayList();
            for(ExperimentSummaryModel expModel : experimentSummaryModelList){
                ExperimentSummaryFXModel expFXModel = new ExperimentSummaryFXModel(expModel);
                experimentSummaryFXModels.add(expFXModel);
            }
            expSummaryTable.setItems(experimentSummaryFXModels);
        } catch (AiravataClientException e) {
            e.printStackTrace();
        }
    }

}