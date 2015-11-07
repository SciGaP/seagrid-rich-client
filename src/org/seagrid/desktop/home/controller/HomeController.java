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
package org.seagrid.desktop.home.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import org.seagrid.desktop.home.model.ExperimentSummaryFXModel;
import org.seagrid.desktop.home.model.ProjectTreeItemFXModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Controls the home screen */
public class HomeController {
    private ObservableList observableProjectList = FXCollections.observableArrayList();

    private ObservableList<ExperimentSummaryFXModel> observableExperimentList = FXCollections.observableArrayList();

    @FXML
    private TreeView<ProjectTreeItemFXModel> projectsTreeView;

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

    @FXML
    private TextField filterField;

    @FXML
    private TabPane tabbedPane;

    public void initialize() {
        initProjectTreeView();
        initExperimentList();
    }


    public void initProjectTreeView(){
        projectsTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        projectsTreeView.setCellFactory(param -> {
            TreeCell<ProjectTreeItemFXModel> cell = new TreeCell<ProjectTreeItemFXModel>(){
                @Override
                public void updateItem(ProjectTreeItemFXModel item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getDisplayName());
                    }
                }
            };
            cell.setOnMouseClicked(event->{
                if (! cell.isEmpty()) {
                    TreeItem<ProjectTreeItemFXModel> treeItem = cell.getTreeItem();
                    ProjectTreeItemFXModel projectTreeItemFXModel = treeItem.getValue();
                    Map<ExperimentSearchFields,String> filters = new HashMap<>();
                    if(projectTreeItemFXModel.getItemType().equals(ProjectTreeItemFXModel.ITEM_TYPE.PROJECT)){
                        filters.put(ExperimentSearchFields.PROJECT_ID,projectTreeItemFXModel.getItemId());
                        tabbedPane.getTabs().get(0).setText(projectTreeItemFXModel.getDisplayName());
                        updateExperimentList(filters,-1,0);
                    }else if(projectTreeItemFXModel.getItemType().equals(ProjectTreeItemFXModel.ITEM_TYPE.RECENT_EXPERIMENTS)){
                        tabbedPane.getTabs().get(0).setText(projectTreeItemFXModel.getDisplayName());
                        updateExperimentList(filters,-1,0);
                    }
                }
            });
            return cell;
        });
        TreeItem root = new TreeItem();
        TreeItem recentExps = new TreeItem<>(
                new ProjectTreeItemFXModel(ProjectTreeItemFXModel.ITEM_TYPE.RECENT_EXPERIMENTS,"no-id","Recent Experiments"));
        root.getChildren().add(recentExps);

        List<Project> projects = new ArrayList<>();
        try {
            projects = AiravataManager.getInstance().getProjects();

        } catch (AiravataClientException e) {
            e.printStackTrace();
        }
        TreeItem projectRoot = new TreeItem<>(
                new ProjectTreeItemFXModel(ProjectTreeItemFXModel.ITEM_TYPE.PROJECT_ROOT_NODE,"no-id","Projects"));
        root.setExpanded(true);
        for (Project itemProject: projects) {
            projectRoot.getChildren().add(new TreeItem<>(new ProjectTreeItemFXModel(
                    ProjectTreeItemFXModel.ITEM_TYPE.PROJECT,itemProject.getProjectID(),itemProject.getName())));
        }
        root.getChildren().add(projectRoot);

        projectsTreeView.setRoot(root);
        projectsTreeView.setShowRoot(false);

    }

    //init the right pane with experiment list
    public void initExperimentList(){
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

        Map<ExperimentSearchFields,String> filters = new HashMap<>();
        tabbedPane.getTabs().get(0).setText("Recent Experiments");
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
            observableExperimentList = FXCollections.observableArrayList();
            for(ExperimentSummaryModel expModel : experimentSummaryModelList){
                ExperimentSummaryFXModel expFXModel = new ExperimentSummaryFXModel(expModel);
                observableExperimentList.add(expFXModel);
            }
            //Set the filter Predicate whenever the filter changes.
            FilteredList<ExperimentSummaryFXModel> filteredExpSummaryData = new FilteredList<>(observableExperimentList, p -> true);
            filterField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredExpSummaryData.setPredicate(experiment -> {
                    // If filter text is empty, display all persons.
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    // Compare first name and last name of every person with filter text.
                    String lowerCaseFilter = newValue.toLowerCase();

                    if (experiment.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches first name.
                    } else if (experiment.getApplication().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches last name.
                    } else if (experiment.getHost().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches last name.
                    } else if (experiment.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches last name.
                    }
                    return false; // Does not match.
                });
            });
            SortedList<ExperimentSummaryFXModel> sortedExperimentListData = new SortedList<>(filteredExpSummaryData);
            sortedExperimentListData.comparatorProperty().bind(expSummaryTable.comparatorProperty());
            expSummaryTable.setItems(sortedExperimentListData);

            filterField.setText("");
        } catch (AiravataClientException e) {
            e.printStackTrace();
        }
    }

}