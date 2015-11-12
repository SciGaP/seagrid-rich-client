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

import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeModel extends TreeItem {
    private final static Logger logger = LoggerFactory.getLogger(TreeModel.class);

    public enum ITEM_TYPE {
        PROJECT_ROOT_NODE, PROJECT, RECENT_EXPERIMENTS, EXPERIMENT
    }

    private ITEM_TYPE itemType;
    private String itemId;
    private String displayName;

    public TreeModel(ITEM_TYPE itemType, String itemId, String displayName){
        this.itemType = itemType;
        this.itemId = itemId;
        this.displayName = displayName;
    }

    public ITEM_TYPE getItemType() {
        return itemType;
    }

    public void setItemType(ITEM_TYPE itemType) {
        this.itemType = itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}