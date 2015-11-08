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
package org.seagrid.desktop.home.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.experiment.ExperimentSummaryModel;
import org.seagrid.desktop.apis.airavata.AiravataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class TreeProjectItemModel extends TreeItemModel {
    private final static Logger logger = LoggerFactory.getLogger(TreeProjectItemModel.class);

    public TreeProjectItemModel(ITEM_TYPE itemType, String itemId, String displayName) {
        super(itemType, itemId, displayName);
    }

    private boolean isFirstTimeChildren = true;

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public ObservableList<TreeItem<TreeItemModel>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;
            ObservableList<TreeItem<TreeItemModel>> expChildren = FXCollections.observableArrayList();
            List<ExperimentSummaryModel> experiments;
            try {
                experiments = AiravataManager.getInstance()
                        .getExperimentSummariesInProject(this.getItemId());
                expChildren.addAll(experiments.stream().map(experimentModel -> new TreeItem<TreeItemModel>(
                        new TreeItemModel(
                                TreeItemModel.ITEM_TYPE.EXPERIMENT, experimentModel.getExperimentId(),
                                experimentModel.getName()
                        )) {
                }).collect(Collectors.toList()));
            } catch (AiravataClientException e) {
                e.printStackTrace();
            }
            super.getChildren().setAll(expChildren);
        }
        return super.getChildren();
    }
}