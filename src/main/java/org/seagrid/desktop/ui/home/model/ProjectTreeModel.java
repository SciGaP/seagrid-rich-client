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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectTreeModel extends TreeItem<TreeModel> {
    private final static Logger logger = LoggerFactory.getLogger(ProjectTreeModel.class);

    private boolean isFirstTimeChildren = true;

    public ProjectTreeModel(TreeModel treeModel) {
        super(treeModel);
        SEAGridEventBus.getInstance().register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleSEAGridEvents(SEAGridEvent event) {
        if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_CREATED)) {
            ExperimentModel experiment = (ExperimentModel) event.getPayload();
            if (!isFirstTimeChildren && (((getValue().getItemType().equals(TreeModel.ITEM_TYPE.PROJECT))
                    && getValue().getItemId().equals(experiment.getProjectId())) || (getValue().getItemType()
                    .equals(TreeModel.ITEM_TYPE.RECENT_EXPERIMENTS)))) {
                getChildren().add(0, new TreeItem<>(new TreeModel(TreeModel.ITEM_TYPE.EXPERIMENT, experiment.getExperimentId(),
                        experiment.getExperimentName())));
            }
        } else if (event.getEventType().equals(SEAGridEvent.SEAGridEventType.EXPERIMENT_DELETED)) {
            if (event.getPayload() instanceof ExperimentListModel) {
                ExperimentListModel experimentListModel = (ExperimentListModel) event.getPayload();
                if (!isFirstTimeChildren && (((getValue().getItemType().equals(TreeModel.ITEM_TYPE.PROJECT))
                        && getValue().getItemId().equals(experimentListModel.getProjectId())) || (getValue().getItemType()
                        .equals(TreeModel.ITEM_TYPE.RECENT_EXPERIMENTS)))) {
                    TreeItem matchingTreeItem = null;
                    for (TreeItem<TreeModel> temp : getChildren()) {
                        if (temp.getValue().getItemId().equals(experimentListModel.getId())) {
                            matchingTreeItem = temp;
                            break;
                        }
                    }
                    getChildren().remove(matchingTreeItem);
                }
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
            ObservableList<TreeItem<TreeModel>> expChildren = FXCollections.observableArrayList();
            List<ExperimentSummaryModel> experiments = new ArrayList<>();
            try {
                if (getValue().getItemType().equals(TreeModel.ITEM_TYPE.PROJECT)) {
                    experiments = AiravataManager.getInstance()
                            .getExperimentSummariesInProject(this.getValue().getItemId());
                } else if (getValue().getItemType().equals(TreeModel.ITEM_TYPE.RECENT_EXPERIMENTS)) {
                    experiments = AiravataManager.getInstance().getRecentExperimentSummaries(SEAGridContext
                            .getInstance().getMaxRecentExpCount());
                }
                expChildren.addAll(experiments.stream().map(experimentModel -> new TreeItem<TreeModel>(
                        new TreeModel(
                                TreeModel.ITEM_TYPE.EXPERIMENT, experimentModel.getExperimentId(),
                                experimentModel.getName()
                        )) {
                }).collect(Collectors.toList()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.getChildren().setAll(expChildren);
        }
        return super.getChildren();
    }
}