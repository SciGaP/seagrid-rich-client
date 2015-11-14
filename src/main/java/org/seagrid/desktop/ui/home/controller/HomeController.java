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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.experiment.ExperimentSearchFields;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.apache.airavata.model.status.ExperimentState;
import org.apache.airavata.model.workspace.Project;
import org.apache.thrift.TException;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.connectors.wso2is.AuthResponse;
import org.seagrid.desktop.connectors.wso2is.AuthenticationManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.ui.experiment.create.ExperimentCreateWindow;
import org.seagrid.desktop.ui.experiment.summary.ExperimentSummaryWindow;
import org.seagrid.desktop.ui.home.model.ExperimentListModel;
import org.seagrid.desktop.ui.home.model.ProjectTreeModel;
import org.seagrid.desktop.ui.home.model.TreeModel;
import org.seagrid.desktop.ui.login.LoginWindow;
import org.seagrid.desktop.ui.project.ProjectWindow;
import org.seagrid.desktop.ui.storage.MassStorageBrowserWindow;
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

    private ObservableList<ExperimentListModel> observableExperimentList;

    @FXML
    private Button browseMassStorageBtn;

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

    @FXML
    private Button logoutBtn;

    private Map<ExperimentSearchFields,String> previousExperimentListFilter;

    @SuppressWarnings("unused")
    public void initialize() {
        SEAGridEventBus.getInstance().register(this);
        initMenuBar();
        initProjectTreeView();
        try {
            initExperimentList();
        } catch (TException e) {
            e.printStackTrace();
            SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", tabbedPane.getScene().getWindow(),
                    "Failed initialising experiment list !");
        }
        initTokenUpdateDaemon();
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
        browseMassStorageBtn.setOnMouseClicked(event -> {
            MassStorageBrowserWindow massStorageBrowserWindow = new MassStorageBrowserWindow();
            try {
                massStorageBrowserWindow.displayFileBrowseAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", browseMassStorageBtn.getScene().getWindow(),
                        "Failed to open Mass Storage Browser");
            }
        });
        logoutBtn.setOnAction(event -> {
            logoutBtn.getScene().getWindow().hide();
            LoginWindow loginWindow = new LoginWindow();
            try {
                loginWindow.displayLoginAndWait();
                ((Stage)logoutBtn.getScene().getWindow()).show();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
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
                                    .getResourceAsStream("/images/file.png")));
                            setGraphic(experimentIcon);
                        }else{
                            Node projectIcon = new ImageView(new Image(HomeController.class
                                    .getResourceAsStream("/images/folder.png")));
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
                        try {
                            updateExperimentList(filters, -1, 0);
                        } catch (TException e) {
                            e.printStackTrace();
                            SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", tabbedPane.getScene().getWindow(),
                                    "Failed to update experiment list !");
                        }
                    } else if (treeModel.getItemType().equals(TreeModel.ITEM_TYPE.RECENT_EXPERIMENTS)) {
                        tabbedPane.getTabs().get(0).setText(treeModel.getDisplayName());
                        try {
                            updateExperimentList(filters, SEAGridContext.getInstance().getMaxRecentExpCount(), 0);
                        } catch (TException e) {
                            e.printStackTrace();
                            SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", tabbedPane.getScene().getWindow(),
                                    "Failed to update experiment list !");
                        }
                    } else if (event.getClickCount() == 2 && treeModel.getItemType().equals(TreeModel.ITEM_TYPE.EXPERIMENT)) {
                        try {
                            ExperimentSummaryWindow experimentSummaryWindow = new ExperimentSummaryWindow();
                            Parent parentNode = experimentSummaryWindow.getExperimentInfoNode(treeModel.getItemId());
                            Tab experimentTab = new Tab(treeModel.getDisplayName(), parentNode);
                            experimentTab.setClosable(true);
                            tabbedPane.getTabs().add(experimentTab);
                            tabbedPane.getSelectionModel().select(experimentTab);
                        } catch (Exception e) {
                            e.printStackTrace();
                            SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", tabbedPane.getScene().getWindow(),
                                    "Cannot open experiment information");
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
    public void initExperimentList() throws TException {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", tabbedPane.getScene().getWindow(),
                                "Cannot open experiment information");
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

        addContextMenuForExperimentSummaryTable();
    }

    private void addContextMenuForExperimentSummaryTable(){
        ContextMenu cm = new ContextMenu();
        MenuItem mi1 = new MenuItem("launch");
        mi1.setOnAction(event -> {
            try {
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if (experimentListModel != null) {
                    AiravataManager.getInstance().launchExperiment(experimentListModel.getId());
                    SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType
                            .EXPERIMENT_LAUNCHED, experimentListModel));
                }
            } catch (TException e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expSummaryTable.getScene()
                        .getWindow(), "Experiment launch failed");
            }
        });
        cm.getItems().add(mi1);
        MenuItem mi2 = new MenuItem("open in new tab");
        mi2.setOnAction(event -> {
            try {
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if(experimentListModel != null) {
                    ExperimentSummaryWindow experimentSummaryWindow = new ExperimentSummaryWindow();
                    Parent parentNode = experimentSummaryWindow.getExperimentInfoNode(experimentListModel.getId());
                    Tab experimentTab = new Tab(experimentListModel.getName(), parentNode);
                    experimentTab.setClosable(true);
                    tabbedPane.getTabs().add(experimentTab);
                    tabbedPane.getSelectionModel().select(experimentTab);
                }
            } catch (Exception e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", tabbedPane.getScene().getWindow(),
                        "Cannot open experiment information");
            }
        });
        cm.getItems().add(mi2);
        MenuItem mi3 = new MenuItem("open in new window");
        mi3.setOnAction(event -> {
            try {
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if(experimentListModel != null) {
                    ExperimentSummaryWindow experimentSummaryWindow = new ExperimentSummaryWindow();
                    experimentSummaryWindow.showExperimentSummaryWindow(experimentListModel.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", tabbedPane.getScene().getWindow(),
                        "Cannot open experiment information");
            }
        });
        cm.getItems().add(mi3);
        MenuItem mi4 = new MenuItem("delete");
        mi4.setOnAction(event -> {
            try {
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if (experimentListModel != null) {
                    AiravataManager.getInstance().deleteExperiment(experimentListModel.getId());
                    SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType
                            .EXPERIMENT_DELETED, experimentListModel));
                }
            } catch (TException e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expSummaryTable.getScene()
                        .getWindow(), "Experiment delete failed");
            }
        });
        cm.getItems().add(mi4);
        expSummaryTable.setContextMenu(cm);

        expSummaryTable.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY){
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if(experimentListModel != null && !experimentListModel.getStatus().equals("CREATED")){
                    mi1.setDisable(true);
                    mi4.setDisable(true);
                }else{
                    mi1.setDisable(false);
                    mi4.setDisable(false);
                }
            }
        });
    }

    //update the right pane with experiment list
    public void updateExperimentList(Map<ExperimentSearchFields, String> filters, int limit, int offset) throws TException {

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
                    } catch (Exception e) {
                        e.printStackTrace();
                        SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", projectsTreeView.getScene().getWindow(),
                                "Failed loading project list !");
                    }
                    super.getChildren().setAll(projChildern);
                }
                return super.getChildren();
            }
        };
        root.getChildren().add(projectRoot);

        return root;
    }


    private void initTokenUpdateDaemon() {
        Timeline oauthTokenUpdateTimer = new Timeline(new KeyFrame(
                Duration.millis((SEAGridContext.getInstance().getOAuthTokenExpirationTime()-System.currentTimeMillis())*5/6),
                ae -> {
                    AuthenticationManager authenticationManager = new AuthenticationManager();
                    try {
                        AuthResponse authResponse = authenticationManager.getRefreshedOAuthToken(SEAGridContext
                                .getInstance().getRefreshToken());
                        if(authResponse != null){
                            SEAGridContext.getInstance().setOAuthToken(authResponse.getAccess_token());
                            SEAGridContext.getInstance().setRefreshToken(authResponse.getAccess_token());
                            SEAGridContext.getInstance().setTokenExpiaryTime(authResponse.getExpires_in() * 1000
                                    + System.currentTimeMillis());
                        }else{
                            throw new Exception("AuthResponse is null");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        SEAGridDialogHelper.showExceptionDialog(e,"Exception Dialog",tabbedPane.getScene().getWindow(),
                                "Failed updating OAuth refresh token");
                    }
                }));
        oauthTokenUpdateTimer.setCycleCount(Timeline.INDEFINITE);
        oauthTokenUpdateTimer.play();
    }


    @SuppressWarnings("unused")
    @Subscribe
    public void listenSEAGridEvents(SEAGridEvent event) throws TException {
        if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.PROJECT_CREATED)){
            Project project = (Project)event.getPayload();
            SEAGridDialogHelper.showInformationNotification("Success","Project " +
                    project.getName() + " created successfully", createProjectButton.getScene().getWindow());
        } else if(event.getEventType().equals(SEAGridEvent.SEAGridEventType.FILE_DOWNLOADED)){
            String localFilePath = (String)event.getPayload();
            SEAGridDialogHelper.showInformationNotification("Success", Paths.get(localFilePath).getFileName()
                    +" was downloaded successfully", createProjectButton.getScene().getWindow());
        } else if(event.getEventType().equals(SEAGridEvent.SEAGridEventType.FILE_UPLOADED)){
            String localFilePath = (String)event.getPayload();
            SEAGridDialogHelper.showInformationNotification("Success", Paths.get(localFilePath).getFileName()
                    +" was uploaded successfully", createProjectButton.getScene().getWindow());
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
        } else if(event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_LAUNCHED)){
            if (event.getPayload() instanceof ExperimentModel) { // This is coming from create and launch experiment
                ExperimentModel experimentModel = (ExperimentModel) event.getPayload();
                SEAGridDialogHelper.showInformationNotification("Success", "Launched experiment " + experimentModel.getExperimentName(),
                        createProjectButton.getScene().getWindow());
            } else if (event.getPayload() instanceof  ExperimentListModel){ // This is coming from experiment list in home
                ExperimentListModel experimentListModel = (ExperimentListModel) event.getPayload();
                SEAGridDialogHelper.showInformationNotification("Success", "Launched experiment " + experimentListModel.getName(),
                        createProjectButton.getScene().getWindow());
            }
        } else if(event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_DELETED)){
            if(event.getPayload() instanceof ExperimentListModel){
                ExperimentListModel experimentListModel = (ExperimentListModel) event.getPayload();
                ExperimentListModel matchingModel = null;
                for(ExperimentListModel temp : observableExperimentList){
                    if(experimentListModel.getId().equals(experimentListModel.getId())){
                        matchingModel = temp;
                        break;
                    }
                }
                if(matchingModel != null){
                    observableExperimentList.remove(matchingModel);
                }
                SEAGridDialogHelper.showInformationNotification("Success", "Deleted experiment "
                        + experimentListModel.getName(), createProjectButton.getScene().getWindow());
            }
        }
    }

}