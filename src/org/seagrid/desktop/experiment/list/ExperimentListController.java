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
package org.seagrid.desktop.experiment.list;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentSearchFields;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.apache.airavata.model.status.ExperimentState;
import org.seagrid.desktop.apis.airavata.AiravataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExperimentListController {
    private final static Logger logger = LoggerFactory.getLogger(ExperimentListController.class);

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

    public ExperimentListController(){}

    @FXML
    private void initialize() {
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
                        if(!empty){
                            setText(item);
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

        updateExperimentList(new HashMap<>(), 100, 0);

        checkAllExps.setOnMouseClicked(event -> handleCheckAllExperiments());
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