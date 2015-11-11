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
package org.seagrid.desktop.ui.home.controller;


import com.google.common.eventbus.Subscribe;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.experiment.ExperimentSearchFields;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.apache.airavata.model.status.ExperimentState;
import org.apache.airavata.model.workspace.Project;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.ui.experiment.create.ExperimentCreateWindow;
import org.seagrid.desktop.ui.experiment.summary.ExperimentSummaryWindow;
import org.seagrid.desktop.ui.home.model.ExperimentListModel;
import org.seagrid.desktop.ui.home.model.ProjectTreeModel;
import org.seagrid.desktop.ui.home.model.TreeModel;
import org.seagrid.desktop.ui.project.ProjectWindow;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Controls the home screen */
public class HomeController {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentListModel.class);

    private ObservableList<ExperimentListModel> observableExperimentList = FXCollections.observableArrayList();

    @FXML
    public Button createProjectButton;

    @FXML
    private Button createExperimentButton;

    @FXML
    private TreeView<TreeModel> projectsTreeView;

    @FXML
    private TableView<ExperimentListModel> expSummaryTable;

    @FXML
    private TableColumn<ExperimentListModel, Boolean> expCheckedColumn;

    @FXML
    private TableColumn<ExperimentListModel, String> expApplicationColumn;

    @FXML
    private TableColumn<ExperimentListModel, String> expHostColumn;

    @FXML
    private TableColumn<ExperimentListModel, String> expStatusColumn;

    @FXML
    private TableColumn<ExperimentListModel, String> expNameColumn;

    @FXML
    private TableColumn<ExperimentListModel, LocalDateTime> expCreateTimeColumn;

    @FXML
    private TextField filterField;

    @FXML
    private TabPane tabbedPane;

    private Map<ExperimentSearchFields,String> previousExperimentListFilter;

    @SuppressWarnings("unused")
    public void initialize() {
        SEAGridEventBus.getInstance().register(this);
        initMenuBar();
        initProjectTreeView();
        initExperimentList();
    }


    public void initMenuBar(){
        createProjectButton.setOnMouseClicked(event -> {
            ProjectWindow projectWindow = new ProjectWindow();
            try {
                projectWindow.displayCreateProjectAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        createExperimentButton.setOnMouseClicked(event -> {
            ExperimentCreateWindow experimentCreateWindow = new ExperimentCreateWindow();
            try {
                experimentCreateWindow.displayCreateExperimentAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void initProjectTreeView(){
        projectsTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        projectsTreeView.setCellFactory(param -> {
            TreeCell<TreeModel> cell = new TreeCell<TreeModel>(){
                @Override
                public void updateItem(TreeModel item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.getDisplayName());
                        if(item.getItemType().equals(TreeModel.ITEM_TYPE.EXPERIMENT)){
                            Node experimentIcon = new ImageView(new Image(HomeController.class
                                    .getResourceAsStream("/images/experiment.png")));
                            setGraphic(experimentIcon);
                        }else{
                            Node projectIcon = new ImageView(new Image(HomeController.class
                                    .getResourceAsStream("/images/project.png")));
                            setGraphic(projectIcon);
                        }
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty() && cell.isSelected()) {
                    TreeItem<TreeModel> treeItem = cell.getTreeItem();
                    TreeModel treeModel = treeItem.getValue();
                    Map<ExperimentSearchFields, String> filters = new HashMap<>();
                    if (treeModel.getItemType().equals(TreeModel.ITEM_TYPE.PROJECT)) {
                        filters.put(ExperimentSearchFields.PROJECT_ID, treeModel.getItemId());
                        tabbedPane.getTabs().get(0).setText(treeModel.getDisplayName());
                        updateExperimentList(filters, -1, 0);
                    } else if (treeModel.getItemType().equals(TreeModel.ITEM_TYPE.RECENT_EXPERIMENTS)) {
                        tabbedPane.getTabs().get(0).setText(treeModel.getDisplayName());
                        updateExperimentList(filters, SEAGridContext.getInstance().getMaxRecentExpCount(), 0);
                    } else if (event.getClickCount() == 2 && treeModel.getItemType().equals(TreeModel.ITEM_TYPE.EXPERIMENT)) {
                        try {
                            ExperimentSummaryWindow experimentSummaryWindow = new ExperimentSummaryWindow();
                            Parent parentNode = experimentSummaryWindow.getExperimentInfoNode(treeModel.getItemId());
                            Tab experimentTab = new Tab(treeModel.getDisplayName(), parentNode);
                            experimentTab.setClosable(true);
                            tabbedPane.getTabs().add(experimentTab);
                            tabbedPane.getSelectionModel().select(experimentTab);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return cell;
        });

        TreeItem root = createProjectTreeModel();
        root.setExpanded(true);
        projectsTreeView.setRoot(root);
        projectsTreeView.setShowRoot(false);
    }

    //init the right pane with experiment list
    public void initExperimentList(){
        expSummaryTable.setEditable(true);

        expSummaryTable.setRowFactory(tv -> {
            TableRow<ExperimentListModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ExperimentListModel rowData = row.getItem();
                    try {
                        ExperimentSummaryWindow experimentSummaryWindow = new ExperimentSummaryWindow();
                        Parent parentNode = experimentSummaryWindow.getExperimentInfoNode(rowData.getId());
                        Tab experimentTab = new Tab(rowData.getName(),parentNode);
                        experimentTab.setClosable(true);
                        tabbedPane.getTabs().add(experimentTab);
                        tabbedPane.getSelectionModel().select(experimentTab);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });

        expCheckedColumn.setCellValueFactory(new PropertyValueFactory<>("checked"));
        expCheckedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(expCheckedColumn));
        expCheckedColumn.setEditable(true);
        expNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        expApplicationColumn.setCellValueFactory(cellData -> cellData.getValue().applicationProperty());
        expHostColumn.setCellValueFactory(cellData -> cellData.getValue().hostProperty());
        expStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        expStatusColumn.setCellFactory(new Callback<TableColumn<ExperimentListModel, String>,
                TableCell<ExperimentListModel, String>>() {
            @Override
            public TableCell<ExperimentListModel, String> call(TableColumn<ExperimentListModel, String> param) {
                return new TableCell<ExperimentListModel, String>() {
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

        Map<ExperimentSearchFields,String> filters = new HashMap<>();
        tabbedPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        tabbedPane.getTabs().get(0).setText("Recent Experiments");
        tabbedPane.getTabs().get(0).setClosable(false);
        updateExperimentList(filters,SEAGridContext.getInstance().getMaxRecentExpCount(),0);
    }

    //update the right pane with experiment list
    public void updateExperimentList(Map<ExperimentSearchFields, String> filters, int limit, int offset){
        try {
            this.previousExperimentListFilter = filters;

            List<ExperimentSummaryModel> experimentSummaryModelList = AiravataManager.getInstance()
                    .getExperimentSummaries(filters, limit, offset);
            observableExperimentList = FXCollections.observableArrayList(new Callback<ExperimentListModel, Observable[]>() {
                @Override
                public Observable[] call(ExperimentListModel param) {
                    return new Observable[]{param.statusProperty()};
                }
            });
            for(ExperimentSummaryModel expModel : experimentSummaryModelList){
                ExperimentListModel expFXModel = new ExperimentListModel(expModel);
                observableExperimentList.add(expFXModel);
            }
            //Set the filter Predicate whenever the filter changes.
            FilteredList<ExperimentListModel> filteredExpSummaryData = new FilteredList<>(observableExperimentList, p -> true);
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
            SortedList<ExperimentListModel> sortedExperimentListData = new SortedList<>(filteredExpSummaryData);
            sortedExperimentListData.comparatorProperty().bind(expSummaryTable.comparatorProperty());
            expSummaryTable.setItems(filteredExpSummaryData);

            filterField.setText("");
            tabbedPane.getSelectionModel().select(0);
        } catch (AiravataClientException e) {
            e.printStackTrace();
        }
    }

    //Creates the project tree model
    private TreeItem createProjectTreeModel(){

        TreeItem root = new TreeItem();
        TreeItem recentExps = new ProjectTreeModel(
                new TreeModel(TreeModel.ITEM_TYPE.RECENT_EXPERIMENTS,"no-id","Recent Experiments"));
        root.getChildren().add(recentExps);

        TreeItem projectRoot = new TreeItem<TreeModel>(
                new TreeModel(TreeModel.ITEM_TYPE.PROJECT_ROOT_NODE,"no-id","Projects")){
            {
                SEAGridEventBus.getInstance().register(this);
            }

            private boolean isFirstTimeChildren = true;

            @SuppressWarnings("unused")
            @Subscribe
            public void handleNewProjectEvent(SEAGridEvent event) {
                if(event.getEventType().equals(SEAGridEvent.SEAGridEventType.PROJECT_CREATED)){
                    if(!isFirstTimeChildren){
                        Project project = (Project)event.getPayload();
                        getChildren().add(0, new ProjectTreeModel(new TreeModel(TreeModel.ITEM_TYPE.PROJECT,
                                project.getProjectID(),project.getName())));
                    }
                }
            }

            @Override
            public boolean isLeaf() {
                return false;
            }

            @Override
            public ObservableList<TreeItem<TreeModel>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    ObservableList<TreeItem<TreeModel>> projChildern = FXCollections.observableArrayList();
                    List<Project> projects;
                    try {
                        projects = AiravataManager.getInstance().getProjects();
                        projChildern.addAll(projects.stream().map(project -> new ProjectTreeModel(
                                new TreeModel(
                                        TreeModel.ITEM_TYPE.PROJECT, project.getProjectID(),
                                        project.getName()
                                ))).collect(Collectors.toList()));
                    } catch (AiravataClientException e) {
                        e.printStackTrace();
                    }
                    super.getChildren().setAll(projChildern);
                }
                return super.getChildren();
            }
        };
        root.getChildren().add(projectRoot);

        return root;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void listenSEAGridEvents(SEAGridEvent event) {
        if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.PROJECT_CREATED)){
            Project project = (Project)event.getPayload();
            SEAGridDialogHelper.showInformationNotification("Success","Project " +
                    project.getName() + " created successfully", createProjectButton.getScene().getWindow());
        } else if(event.getEventType().equals(SEAGridEvent.SEAGridEventType.FILE_DOWNLOADED)){
            String localFilePath = (String)event.getPayload();
            SEAGridDialogHelper.showInformationNotification("Success", Paths.get(localFilePath).getFileName()
                    +" was downloaded successfully", createProjectButton.getScene().getWindow());
        } else if(event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_CREATED)){
            ExperimentModel experiment = (ExperimentModel) event.getPayload();
            SEAGridDialogHelper.showInformationNotification("Success","Experiment " +
                    experiment.getExperimentName() + " created successfully", createProjectButton.getScene().getWindow());
            ExperimentSummaryModel experimentSummaryModel = new ExperimentSummaryModel();
            experimentSummaryModel.setExperimentId(experiment.getExperimentId());
            experimentSummaryModel.setName(experiment.getExperimentName());
            experimentSummaryModel.setExecutionId(experiment.getExecutionId());
            experimentSummaryModel.setResourceHostId(experiment.getUserConfigurationData()
                    .getComputationalResourceScheduling().getResourceHostId());
            experimentSummaryModel.setProjectId(experiment.getProjectId());
            experimentSummaryModel.setGatewayId(experiment.getGatewayId());
            experimentSummaryModel.setUserName(experiment.getUserName());
            experimentSummaryModel.setDescription(experiment.getDescription());
            experimentSummaryModel.setExperimentStatus("CREATED");
            long time = System.currentTimeMillis();
            experimentSummaryModel.setCreationTime(time);
            experimentSummaryModel.setStatusUpdateTime(time);

            ExperimentListModel experimentListModel = new ExperimentListModel(experimentSummaryModel);
            if(this.previousExperimentListFilter == null ||
                    this.previousExperimentListFilter.get(ExperimentSearchFields.PROJECT_ID) == null ||
                    this.previousExperimentListFilter.get(ExperimentSearchFields.PROJECT_ID).equals(
                            SEAGridContext.getInstance().getRecentExperimentsDummyId()) ||
                    this.previousExperimentListFilter.get(ExperimentSearchFields.PROJECT_ID).equals(experiment.getProjectId())) {
                observableExperimentList.add(0,experimentListModel);
            }
        }
    }

}