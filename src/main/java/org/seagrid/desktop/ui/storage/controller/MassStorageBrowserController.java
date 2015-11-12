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
package org.seagrid.desktop.ui.storage.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.seagrid.desktop.ui.storage.model.FileListModel;

import java.io.File;
import java.nio.file.Path;

import org.seagrid.desktop.util.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class MassStorageBrowserController {
    private final static Logger logger = LoggerFactory.getLogger(MassStorageBrowserController.class);

    @FXML
    private TableView<FileListModel> fbLocalFileTable;

    @FXML
    private TableColumn<FileListModel,String> fbLocalFileTblFileName;

    @FXML
    private TableColumn<FileListModel,String> fbLocalFileTblFileSize;

    @FXML
    private TableColumn<FileListModel,String> fbLocalFileTblFileType;

    @FXML
    private TableColumn<FileListModel,String> fbLocalFileTblLastMod;

    @FXML
    private TableView<FileListModel> fbRemoteFileTable;

    @FXML
    private TableColumn<FileListModel,String> fbRemoteFileTblFileName;

    @FXML
    private TableColumn<FileListModel,String> fbRemoteFileTblFileSize;

    @FXML
    private TableColumn<FileListModel,String> fbRemoteFileTblFileType;

    @FXML
    public TableColumn<FileListModel,String> fbRemoteFileTblLastMod;

    private Path currentLocalPath, currentRemotePath;

    ObservableList<FileListModel> currentLocalFileList, currentRemoteFileList;

    @SuppressWarnings("unused")
    public void initialize(){
        initialiseColumnWidths();
        initializeLocalFileTable();
        initializeRemoteFileTable();
    }

    private void initialiseColumnWidths(){
        fbLocalFileTblFileName.prefWidthProperty().bind(fbLocalFileTable.widthProperty().divide(4));
        fbLocalFileTblFileSize.prefWidthProperty().bind(fbLocalFileTable.widthProperty().divide(4));
        fbLocalFileTblFileType.prefWidthProperty().bind(fbLocalFileTable.widthProperty().divide(4));
        fbLocalFileTblLastMod.prefWidthProperty().bind(fbLocalFileTable.widthProperty().divide(4));

        fbRemoteFileTblFileName.prefWidthProperty().bind(fbRemoteFileTable.widthProperty().divide(4));
        fbRemoteFileTblFileSize.prefWidthProperty().bind(fbRemoteFileTable.widthProperty().divide(4));
        fbRemoteFileTblFileType.prefWidthProperty().bind(fbRemoteFileTable.widthProperty().divide(4));
        fbRemoteFileTblLastMod.prefWidthProperty().bind(fbRemoteFileTable.widthProperty().divide(4));
    }

    private void initializeLocalFileTable(){
        String userHome = System.getProperty("user.home");
        this.currentLocalPath = Paths.get(userHome);

        fbLocalFileTblFileName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        fbLocalFileTblFileSize.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSize()
                + " bytes"));
        fbLocalFileTblFileType.setCellValueFactory(cellData -> {
            if(cellData.getValue().getFileListModelType().equals(FileListModel.FileListModelType.FILE)){
                return new SimpleStringProperty("file");
            }
            return new SimpleStringProperty("dir");
        });
        fbLocalFileTblLastMod.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .getLastModifiedTime()+""));
        currentLocalFileList = FXCollections.observableArrayList();
        fbLocalFileTable.setItems(currentLocalFileList);
        populateLocalFileList();
    }

    private void populateLocalFileList(){
        File parent = new File(currentLocalPath.toString());
        if(parent.exists() && parent.isDirectory()){
            FileListModel fileListModel = new FileListModel("..", FileListModel.FileListModelType.DIR,0,232423235);
            currentLocalFileList.add(fileListModel);
            File[] children = parent.listFiles();
            for(File child : children){
                fileListModel = new FileListModel(child.getName(),child.isFile() == true
                        ? FileListModel.FileListModelType.FILE : FileListModel.FileListModelType.DIR, child.length(),
                        child.lastModified());
                currentLocalFileList.add(fileListModel);
            }
        }
    }

    private void initializeRemoteFileTable(){
        String remoteHome = "/";
        this.currentRemotePath = Paths.get(remoteHome);

        fbRemoteFileTblFileName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        fbRemoteFileTblFileSize.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSize()
                + " bytes"));
        fbRemoteFileTblFileType.setCellValueFactory(cellData -> {
            if(cellData.getValue().getFileListModelType().equals(FileListModel.FileListModelType.FILE)){
                return new SimpleStringProperty("file");
            }
            return new SimpleStringProperty("dir");
        });
        fbRemoteFileTblLastMod.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .getLastModifiedTime()+""));
        populateRemoteFileTable();
    }

    private void populateRemoteFileTable(){

    }
}