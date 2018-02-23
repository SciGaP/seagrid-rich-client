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


import cct.JamberooMolecularEditor;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import g03input.G03MenuTree;
import gamess.GamessGUI;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import nanocad.nanocadMain;
import org.apache.airavata.model.experiment.ExperimentModel;
import org.apache.airavata.model.experiment.ExperimentSearchFields;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.apache.airavata.model.status.ExperimentState;
import org.apache.airavata.model.workspace.Notification;
import org.apache.airavata.model.workspace.Project;
import org.apache.thrift.TException;
import org.seagrid.desktop.connectors.airavata.AiravataManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.ui.experiment.create.ExperimentCreateWindow;
import org.seagrid.desktop.ui.experiment.summary.ExperimentSummaryWindow;
import org.seagrid.desktop.ui.home.model.ExperimentListModel;
import org.seagrid.desktop.ui.home.model.ProjectTreeModel;
import org.seagrid.desktop.ui.home.model.TreeModel;
import org.seagrid.desktop.ui.project.ProjectWindow;
import org.seagrid.desktop.ui.storage.MassStorageBrowserWindow;
import org.seagrid.desktop.util.SEAGridConfig;
import org.seagrid.desktop.util.SEAGridContext;
import org.seagrid.desktop.util.messaging.SEAGridEvent;
import org.seagrid.desktop.util.messaging.SEAGridEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controls the home screen
 */
public class HomeController {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentListModel.class);

    private ObservableList<ExperimentListModel> observableExperimentList;

    @FXML
    public Label notificationLabel;

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
    private TableColumn<ExperimentListModel, String> ownerNameColumn;

    @FXML
    private TableColumn<ExperimentListModel, LocalDateTime> expCreateTimeColumn;

    @FXML
    private TextField filterField;

    @FXML
    private TabPane tabbedPane;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button nanocadBtn;

    @FXML
    private Button jamberooBtn;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private MenuItem appExitMenuItem;

    @FXML
    private MenuItem nanocadMenuBtn;

    @FXML
    private MenuItem jamberooMenuBtn;

    @FXML
    private MenuItem g03MenuBtn;

    @FXML
    private MenuItem gamessMenuBtn;

    @FXML
    private Button launchSelectedBtn;

    @FXML
    private Button deleteSelectedBtn;

    @FXML
    private Button gamessBtn;

    @FXML
    private Button g03Btn;

    @FXML
    public MenuItem projCreateMenuItem;

    @FXML
    public MenuItem expCreateMenuItem;

    private Map<ExperimentSearchFields, String> previousExperimentListFilter;

    private ContextMenu contextMenu;

    //Dummy class used for storing notification list index
    private class Index{
        int index;
    }

    @SuppressWarnings("unused")
    public void initialize() {
        SEAGridEventBus.getInstance().register(this);
        initMenuBar();
        initProjectTreeView();
        try {
            initExperimentList();
        } catch (TException e) {
            e.printStackTrace();
            SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", null,
                    "Failed initialising experiment list !");
        }
        initTokenUpdateDaemon();

        //initializing notification messages
        notificationLabel.setCursor(javafx.scene.Cursor.HAND);
        notificationLabel.setStyle("-fx-border-color: white;");
        notificationLabel.setMaxWidth(Double.MAX_VALUE);
        try{
            java.util.List<Notification> messages = AiravataManager.getInstance().getNotifications();
            final Index index = new Index();
            index.index = 0;
            if (messages != null && messages.size() > 0) {
                notificationLabel.setText(messages.get(index.index).getTitle() + " : "
                        + messages.get(index.index).getNotificationMessage().split("\r|\n")[0]);
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0),
                        event -> {
                            index.index++;
                            index.index = index.index % messages.size();
                            switch (messages.get(index.index).getPriority()){
                                case HIGH:
                                    notificationLabel.setTextFill(Color.web("#ff0000"));
                                    break;
                                case NORMAL:
                                    notificationLabel.setTextFill(Color.web("#ffa500"));
                                    break;
                                case LOW:
                                    notificationLabel.setTextFill(Color.web("#808080"));
                                    break;
                            }
                            notificationLabel.setText(messages.get(index.index).getTitle() + " : "
                                    + messages.get(index.index).getNotificationMessage().split("\r|\n")[0]);

                            notificationLabel.setOnMouseClicked(event1 -> {
                                SEAGridDialogHelper.showInformationDialog("Notification", messages.get(index.index).getTitle(),
                                        messages.get(index.index).getNotificationMessage(), null);
                            });
                        }),
                        new KeyFrame(Duration.seconds(5)));
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
            }
        }catch (Exception ex){
            //cannot connect to Airavata
            ex.printStackTrace();
        }
    }

    public void initMenuBar() {
        expCreateMenuItem.setOnAction(event -> {
            try {
                ExperimentCreateWindow.displayCreateExperiment();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        projCreateMenuItem.setOnAction(event -> {
            try {
                ProjectWindow.displayCreateProject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        createProjectButton.setOnMouseClicked(event -> {
            try {
                ProjectWindow.displayCreateProject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        createExperimentButton.setOnMouseClicked(event -> {
            try {
                ExperimentCreateWindow.displayCreateExperiment();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        browseMassStorageBtn.setOnMouseClicked(event -> {
            try {
                MassStorageBrowserWindow.displayFileBrowse(null);
            } catch (Exception e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", browseMassStorageBtn.getScene().getWindow(),
                        "Failed to open Storage Browser");
            }
        });
        nanocadBtn.setOnAction(event ->
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        nanocadMain.showNanocad();
                    }
                }));
        jamberooBtn.setOnAction(event -> {
            try{
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JamberooMolecularEditor.showJamberoo();
                    }
                });

            }catch (Exception ex){
                ex.printStackTrace();
            }

        });
        g03Btn.setOnAction(event1 -> SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                G03MenuTree.showG03MenuTree();
            }
        }));
        gamessBtn.setOnAction(event1 -> SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GamessGUI.showGamesGUI();
            }
        }));
        logoutBtn.setOnAction(event -> {
            java.net.CookieHandler.setDefault(new com.sun.webkit.network.CookieManager());
            ((Stage) logoutBtn.getScene().getWindow()).close();
            SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.LOGOUT, null));
        });
        aboutMenuItem.setOnAction(event -> {
            String imgtext = "<img src=\"data:image/png;base64," + SEAGridContext.logoBase64 + "\" height=50 width=50>";
            String textinfo1 = "<div style=\"background-color:#E7EEF6; color:#000000\">" +
                    "<div style=\"background-color:#A7B3C7; color:#FFFFFF;\">" +
                    imgtext + "<font size=5> Welcome to SEAGrid !! - Science and Engineering Applications Grid" +
                    "</font>" +
                    "<br></div>" +
                    "<p>You are running the " +
                    "<Font color='green'>SEAGrid Desktop Client </font>" +
                    "Application. </p>";
            String textinfo2 = "<p>To use Web Portal and for more information, " +
                    " visit <a href='https://seagrid.org/'>https://seagrid.org/</a></div></p>";
            String textinfo3 = "<p>If you do not have SEAGrid account, you may request one on the web portal." +
                    "</div></p>";

            String textinfo4 = "<br><p><Font color='red'>Note: This version is in active development and will" +
                    " be auto-updated automatically.</font></p>";

            WebView webView = new WebView();
            webView.getEngine().loadContent(textinfo1 + textinfo2 + textinfo3 + textinfo4);
            SEAGridDialogHelper.showInformationDialog(
                    "Information Dialog",
                    "SEAGrid Desktop Client",
                    webView,
                    logoutBtn.getScene().getWindow());
        });
        appExitMenuItem.setOnAction(event -> {
            boolean result = SEAGridDialogHelper.showConfirmDialog("Confirmation Dialog", "Confirm your action",
                    "Are sure you want to exit the application?");
            if (result) {
                System.exit(0);
            }
        });
        nanocadMenuBtn.setOnAction(event ->
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        nanocadMain.showNanocad();
                    }
                }));
        jamberooMenuBtn.setOnAction(event ->
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JamberooMolecularEditor.showJamberoo();
                    }
                }));
        g03MenuBtn.setOnAction(event -> SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                G03MenuTree.showG03MenuTree();
            }
        }));
        gamessMenuBtn.setOnAction(event -> SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GamessGUI.showGamesGUI();
            }
        }));
        launchSelectedBtn.setOnAction(event -> expSummaryTable.getItems().stream()
                .filter(e -> e.getChecked() && e.getStatus().equals("CREATED")).forEach(e -> {
                    try {
                        AiravataManager.getInstance().launchExperiment(e.getId());
                        SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_LAUNCHED, e));
                    } catch (Exception ex) {
                        SEAGridDialogHelper.showExceptionDialog(ex, "Exception Dialog", launchSelectedBtn.getScene().getWindow(),
                                "Failed to launch experiment!");
                    }
                }));
        deleteSelectedBtn.setOnAction(event -> {
            List<ExperimentListModel> experimentListModels = FXCollections.observableArrayList(expSummaryTable.getItems());
            experimentListModels.stream()
                    .filter(e -> e.getChecked() && e.getStatus().equals("CREATED")).forEach(e -> {
                try {
                    AiravataManager.getInstance().deleteExperiment(e.getId());
                    SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.EXPERIMENT_DELETED, e));
                } catch (Exception ex) {
                    SEAGridDialogHelper.showExceptionDialog(ex, "Exception Dialog", launchSelectedBtn.getScene().getWindow(),
                            "Failed to delete experiment!");
                }
            });
        });
    }

    public void initProjectTreeView() {
        projectsTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        projectsTreeView.setCellFactory(param -> {
            TreeCell<TreeModel> cell = new TreeCell<TreeModel>() {
                @Override
                public void updateItem(TreeModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.getDisplayName());
                        if (item.getItemType().equals(TreeModel.ITEM_TYPE.EXPERIMENT)) {
                            Node experimentIcon = new ImageView(new Image(HomeController.class
                                    .getResourceAsStream("/images/file.png")));
                            setGraphic(experimentIcon);
                        } else {
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
                        Tab experimentTab = new Tab(rowData.getName(), parentNode);
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
        ownerNameColumn.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());
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
                        if (!empty) {
                            if (item.equals(ExperimentState.COMPLETED.toString())) {
                                this.setTextFill(Color.GREEN);
                            } else if (item.equals(ExperimentState.FAILED.toString())) {
                                this.setTextFill(Color.RED);
                            } else if (item.equals(ExperimentState.CREATED.toString())) {
                                this.setTextFill(Color.BLUE);
                            } else {
                                this.setTextFill(Color.ORANGE);
                            }
                        }
                    }
                };
            }
        });
        expCreateTimeColumn.setCellValueFactory(cellData -> cellData.getValue().createdTimeProperty());

        Map<ExperimentSearchFields, String> filters = new HashMap<>();
        tabbedPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        tabbedPane.getTabs().get(0).setText("Recent Experiments");
        tabbedPane.getTabs().get(0).setClosable(false);
        updateExperimentList(filters, SEAGridContext.getInstance().getMaxRecentExpCount(), 0);

        createContextMenuForExperimentSummaryTable();

        if (expSummaryTable.getItems().size() > 0) {
            expSummaryTable.setContextMenu(contextMenu);
        } else {
            expSummaryTable.setContextMenu(null);
        }
    }

    private void createContextMenuForExperimentSummaryTable() {
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

        MenuItem mi2 = new MenuItem("cancel");
        mi2.setOnAction(event -> {
            try {
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if (experimentListModel != null) {
                    AiravataManager.getInstance().cancelExperiment(experimentListModel.getId());
                    SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType
                            .EXPERIMENT_CANCELLED, experimentListModel));
                }
            } catch (TException e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expSummaryTable.getScene()
                        .getWindow(), "Experiment cancel failed");
            }
        });
        cm.getItems().add(mi2);

        MenuItem mi3 = new MenuItem("edit");
        mi3.setOnAction(event -> {
            try {
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if (experimentListModel != null) {
                    ExperimentModel experimentModel = AiravataManager.getInstance().getExperiment(experimentListModel.getId());
                    SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType
                            .EXPERIMENT_EDIT_REQUEST, experimentModel));
                }
            } catch (TException e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expSummaryTable.getScene()
                        .getWindow(), "Experiment edit failed");
            }
        });
        cm.getItems().add(mi3);

        MenuItem mi4 = new MenuItem("open in new tab");
        mi4.setOnAction(event -> {
            try {
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if (experimentListModel != null) {
                    ExperimentSummaryWindow experimentSummaryWindow = new ExperimentSummaryWindow();
                    Parent parentNode = experimentSummaryWindow.getExperimentInfoNode(experimentListModel.getId());
                    Tab experimentTab = new Tab(experimentListModel.getName(), parentNode);
                    experimentTab.setId(experimentListModel.getId());
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
        cm.getItems().add(mi4);
        MenuItem mi5 = new MenuItem("open in new window");
        mi5.setOnAction(event -> {
            try {
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if (experimentListModel != null) {
                    ExperimentSummaryWindow experimentSummaryWindow = new ExperimentSummaryWindow();
                    experimentSummaryWindow.showExperimentSummaryWindow(experimentListModel.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", tabbedPane.getScene().getWindow(),
                        "Cannot open experiment information");
            }
        });
        cm.getItems().add(mi5);
        MenuItem mi6 = new MenuItem("delete");
        mi6.setOnAction(event -> {
            try {
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if (experimentListModel != null) {
                    if(SEAGridDialogHelper.showConfirmDialog("Confirmation Dialog", "Confirm your action",
                            "Are sure you want to delete the experiment?")){
                        AiravataManager.getInstance().deleteExperiment(experimentListModel.getId());
                        SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType
                                .EXPERIMENT_DELETED, experimentListModel));
                    }
                }
            } catch (TException e) {
                e.printStackTrace();
                SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", expSummaryTable.getScene()
                        .getWindow(), "Experiment delete failed");
            }
        });
        cm.getItems().add(mi6);

        this.contextMenu = cm;

        expSummaryTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                ExperimentListModel experimentListModel = expSummaryTable.getSelectionModel().getSelectedItem();
                if (experimentListModel != null) {
                    mi1.setDisable(true);
                    mi2.setDisable(true);
                    mi3.setDisable(true);
                    mi4.setDisable(false);
                    mi5.setDisable(false);
                    mi6.setDisable(true);
                    if (experimentListModel.getStatus().equals(ExperimentState.CREATED.toString())) {
                        mi1.setDisable(false);
                        mi3.setDisable(false);
                        mi6.setDisable(false);
                    } else if (experimentListModel.getStatus().equals(ExperimentState.EXECUTING.toString())) {
                        mi2.setDisable(false);
                    }
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
        for (ExperimentSummaryModel expModel : experimentSummaryModelList) {
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


        if (expSummaryTable.getItems().size() > 0) {
            expSummaryTable.setContextMenu(contextMenu);
        } else {
            expSummaryTable.setContextMenu(null);
        }
    }

    //Creates the project tree model
    private TreeItem createProjectTreeModel() {

        TreeItem root = new TreeItem();
        TreeItem recentExps = new ProjectTreeModel(
                new TreeModel(TreeModel.ITEM_TYPE.RECENT_EXPERIMENTS, "no-id", "Recent Experiments"));
        root.getChildren().add(recentExps);

        TreeItem projectRoot = new TreeItem<TreeModel>(
                new TreeModel(TreeModel.ITEM_TYPE.PROJECT_ROOT_NODE, "no-id", "Projects")) {
            {
                SEAGridEventBus.getInstance().register(this);
            }

            private boolean isFirstTimeChildren = true;

            @SuppressWarnings("unused")
            @Subscribe
            public void handleNewProjectEvent(SEAGridEvent event) {
                if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.PROJECT_CREATED)) {
                    if (!isFirstTimeChildren) {
                        Project project = (Project) event.getPayload();
                        getChildren().add(0, new ProjectTreeModel(new TreeModel(TreeModel.ITEM_TYPE.PROJECT,
                                project.getProjectID(), project.getName())));
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
                //It seems the OAuthTokenExpiration time is in GMT
                Duration.millis(60*60*1000),
                ae -> {
                    try {
                        String url;
                        if(SEAGridConfig.DEV){
                            url = "https://dev.seagrid.org/refreshed-token-desktop?refresh_code="
                                    + SEAGridContext.getInstance().getRefreshToken();
                        }else{
                            url = "https://seagrid.org/refreshed-token-desktop?refresh_code="
                                    + SEAGridContext.getInstance().getRefreshToken();
                        }
                        String json = readUrl(url);
                        Gson gson = new Gson();
                        Type type = new TypeToken<Map<String, String>>(){}.getType();
                        Map<String, String> params = gson.fromJson(json, type);

                        if(!params.get("status").equals("ok")){
                            throw new Exception("Token refresh failed.");
                        }else{
                            SEAGridContext.getInstance().setOAuthToken(params.get("code"));
                            SEAGridContext.getInstance().setRefreshToken(params.get("refresh_code"));
                            SEAGridContext.getInstance().setTokenExpiaryTime(Integer.parseInt(params.get("valid_time").trim()));
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        //Initiating a logout
                        SEAGridDialogHelper.showInformationDialog("Session Timed Out...", "Session Timed Out...",
                                "Your session has timed out. Please re-login to continue work.", null);
                        ((Stage) logoutBtn.getScene().getWindow()).close();
                        SEAGridEventBus.getInstance().post(new SEAGridEvent(SEAGridEvent.SEAGridEventType.LOGOUT, null));
                    }
                }));
        oauthTokenUpdateTimer.setCycleCount(Timeline.INDEFINITE);
        oauthTokenUpdateTimer.play();
    }


    @SuppressWarnings("unused")
    @Subscribe
    public void listenSEAGridEvents(SEAGridEvent event) throws TException {
        if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.PROJECT_CREATED)) {
            Project project = (Project) event.getPayload();
            SEAGridDialogHelper.showInformationNotification("Success", "Project " +
                    project.getName() + " created successfully", createProjectButton.getScene().getWindow());
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.FILE_DOWNLOADED)) {
            String localFilePath = (String) event.getPayload();
            SEAGridDialogHelper.showInformationNotification("Success", Paths.get(localFilePath).getFileName()
                    + " was downloaded successfully", createProjectButton.getScene().getWindow());
            //Opening the file in system proffered editor
            try {
                Desktop.getDesktop().open(new File(localFilePath));
            } catch (IOException e) {
                logger.error("Cannot open file. Opening parent directory");
                try {
                    Desktop.getDesktop().open(new File(localFilePath).getParentFile());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.FILE_UPLOADED)) {
            String localFilePath = (String) event.getPayload();
            SEAGridDialogHelper.showInformationNotification("Success", Paths.get(localFilePath).getFileName()
                    + " was uploaded successfully", createProjectButton.getScene().getWindow());
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_CREATED)) {
            ExperimentModel experiment = (ExperimentModel) event.getPayload();
            SEAGridDialogHelper.showInformationNotification("Success", "Experiment " +
                    experiment.getExperimentName() + " created successfully", createProjectButton.getScene().getWindow());
            ExperimentListModel experimentListModel = assembleExperimentListModel(experiment);
            if (this.previousExperimentListFilter == null ||
                    this.previousExperimentListFilter.get(ExperimentSearchFields.PROJECT_ID) == null ||
                    this.previousExperimentListFilter.get(ExperimentSearchFields.PROJECT_ID).equals(
                            SEAGridContext.getInstance().getRecentExperimentsDummyId()) ||
                    this.previousExperimentListFilter.get(ExperimentSearchFields.PROJECT_ID).equals(experiment.getProjectId())) {
                observableExperimentList.add(0, experimentListModel);
            }
            if (expSummaryTable.getContextMenu() == null) {
                expSummaryTable.setContextMenu(contextMenu);
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_LAUNCHED)) {
            if (event.getPayload() instanceof ExperimentModel) { // This is coming from create and launch experiment
                ExperimentModel experimentModel = (ExperimentModel) event.getPayload();
                SEAGridDialogHelper.showInformationNotification("Success", "Launched experiment " + experimentModel.getExperimentName(),
                        createProjectButton.getScene().getWindow());
            } else if (event.getPayload() instanceof ExperimentListModel) { // This is coming from experiment list in home
                ExperimentListModel experimentListModel = (ExperimentListModel) event.getPayload();
                SEAGridDialogHelper.showInformationNotification("Success", "Launched experiment " + experimentListModel.getName(),
                        createProjectButton.getScene().getWindow());
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_DELETED)) {
            if (event.getPayload() instanceof ExperimentListModel) {
                ExperimentListModel experimentListModel = (ExperimentListModel) event.getPayload();
                ExperimentListModel matchingModel = null;
                for (ExperimentListModel temp : observableExperimentList) {
                    if (temp.getId().equals(experimentListModel.getId())) {
                        matchingModel = temp;
                        break;
                    }
                }
                if (matchingModel != null) {
                    observableExperimentList.remove(matchingModel);
                }
                if (expSummaryTable.getItems().size() == 0) {
                    expSummaryTable.setContextMenu(null);
                }
                SEAGridDialogHelper.showInformationNotification("Success", "Deleted experiment "
                        + experimentListModel.getName(), createProjectButton.getScene().getWindow());
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_CANCELLED)) {
            if (event.getPayload() instanceof ExperimentModel) { // This is coming from experiment summary
                ExperimentModel experimentModel = (ExperimentModel) event.getPayload();
                SEAGridDialogHelper.showInformationNotification("Success", "Cancelled experiment "
                                + experimentModel.getExperimentName(),
                        createProjectButton.getScene().getWindow());
            } else if (event.getPayload() instanceof ExperimentListModel) { // This is coming from browse experiment
                ExperimentListModel experimentListModel = (ExperimentListModel) event.getPayload();
                SEAGridDialogHelper.showInformationNotification("Success", "Cancelled experiment "
                                + experimentListModel.getName(),
                        createProjectButton.getScene().getWindow());
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_EDIT_REQUEST)) {
            if (event.getPayload() instanceof ExperimentModel) { // This is coming from experiment summary
                ExperimentModel experimentModel = (ExperimentModel) event.getPayload();
                try {
                    ExperimentCreateWindow.displayEditExperiment(experimentModel);
                } catch (Exception e) {
                    SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog", expSummaryTable.getScene().getWindow(),
                            "Failed to launch edit experiment dialog");
                }
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_UPDATED)) {
            if (event.getPayload() instanceof ExperimentModel) { // This is coming from experiment edit
                ExperimentModel experimentModel = (ExperimentModel) event.getPayload();
                SEAGridDialogHelper.showInformationNotification("Success", "Updated experiment "
                        + experimentModel.getExperimentName(), expSummaryTable.getScene().getWindow());
                tabbedPane.getTabs().stream().forEach(t->{
                    String tabId = t.getId();
                    if(experimentModel.getExperimentId().equals(tabId)){
                        t.setText(experimentModel.getExperimentName());
                    }
                });
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_CLONED)) {
            if (event.getPayload() instanceof ExperimentModel) { // This is coming from experiment edit
                ExperimentModel experimentModel = (ExperimentModel) event.getPayload();
                SEAGridDialogHelper.showInformationNotification("Success", "Cloned experiment "
                        + experimentModel.getExperimentName(), createProjectButton.getScene().getWindow());
                ExperimentListModel experimentListModel = assembleExperimentListModel(experimentModel);
                if (this.previousExperimentListFilter == null ||
                        this.previousExperimentListFilter.get(ExperimentSearchFields.PROJECT_ID) == null ||
                        this.previousExperimentListFilter.get(ExperimentSearchFields.PROJECT_ID).equals(
                                SEAGridContext.getInstance().getRecentExperimentsDummyId()) ||
                        this.previousExperimentListFilter.get(ExperimentSearchFields.PROJECT_ID).equals(experimentModel.getProjectId())) {
                    observableExperimentList.add(0, experimentListModel);
                }
                try {
                    ExperimentSummaryWindow experimentSummaryWindow = new ExperimentSummaryWindow();
                    Parent parentNode = experimentSummaryWindow.getExperimentInfoNode(experimentListModel.getId());
                    Tab experimentTab = new Tab(experimentModel.getExperimentName(), parentNode);
                    experimentTab.setId(experimentModel.getExperimentId());
                    experimentTab.setClosable(true);
                    tabbedPane.getTabs().add(experimentTab);
                    tabbedPane.getSelectionModel().select(experimentTab);

                    ExperimentCreateWindow.displayEditExperiment(experimentModel);
                } catch (Exception e) {
                    SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog", expSummaryTable.getScene().getWindow(),
                            "Failed to launch edit experiment dialog");
                }
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPORT_GAUSSIAN_EXP)) {
            if (event.getPayload() instanceof String) {
                String gaussianInput = (String) event.getPayload();
                try {
                    ExperimentCreateWindow.displayCreateGaussianExp(gaussianInput);
                } catch (Exception e) {
                    SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog", expSummaryTable.getScene().getWindow(),
                            "Failed to launch gaussian experiment dialog");
                }
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPORT_GAMESS_EXP)) {
            if (event.getPayload() instanceof String) {
                String gamessInput = (String) event.getPayload();
                try {
                    ExperimentCreateWindow.displayCreateGamessExp(gamessInput);
                } catch (Exception e) {
                    SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog", expSummaryTable.getScene().getWindow(),
                            "Failed to launch gamess experiment dialog");
                }
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPORT_NWCHEM_EXP)) {
            if (event.getPayload() instanceof String) {
                String nwchemInput = (String) event.getPayload();
                try {
                    ExperimentCreateWindow.displayCreateNwchemExp( nwchemInput );
                } catch (Exception e) {
                    SEAGridDialogHelper.showExceptionDialog( e, "Exception Dialog", expSummaryTable.getScene().getWindow(),
                            "Failed to launch nwchem experiment dialog" );
                }
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPORT_PSI4_EXP)) {
                if (event.getPayload() instanceof String) {
                    String psi4Input = (String) event.getPayload();
                    String processors = "$SLURM_NPROCS";
                    try {
                        ExperimentCreateWindow.displayCreatePsi4Exp(processors, psi4Input);
                    } catch (Exception e) {
                        SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog", expSummaryTable.getScene().getWindow(),
                                "Failed to launch PSI4 experiment dialog");
                    }
                }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPORT_MOLCAS_EXP)) {
        if (event.getPayload() instanceof String) {
            String molcasInput = (String) event.getPayload();
            String processors = "$SLURM_NPROCS";
            try {
                ExperimentCreateWindow.displayCreateMolcasExp(processors, molcasInput);
            } catch (Exception e) {
                SEAGridDialogHelper.showExceptionDialog(e, "Exception Dialog", expSummaryTable.getScene().getWindow(),
                        "Failed to launch Molcas experiment dialog");
            }
        }
    }

    }

    private  String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    private ExperimentListModel assembleExperimentListModel(ExperimentModel experiment) throws TException {
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
        experimentSummaryModel.setExperimentStatus(experiment.getExperimentStatus().get(0).getState().toString());
        long time = System.currentTimeMillis();
        experimentSummaryModel.setCreationTime(time);
        experimentSummaryModel.setStatusUpdateTime(time);
        return new ExperimentListModel(experimentSummaryModel);
    }
}