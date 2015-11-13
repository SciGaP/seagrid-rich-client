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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.seagrid.desktop.connectors.storage.StorageManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.ui.storage.model.FileListModel;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Vector;

public class MassStorageBrowserController {
    private final static Logger logger = LoggerFactory.getLogger(MassStorageBrowserController.class);

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    @FXML
    private TableView<FileListModel> fbLocalFileTable;

    @FXML
    private TableColumn<FileListModel,FileListModel> fbLocalFileTblFileName;

    @FXML
    private TableColumn<FileListModel,String> fbLocalFileTblFileSize;

    @FXML
    private TableColumn<FileListModel,String> fbLocalFileTblLastMod;

    @FXML
    private TableView<FileListModel> fbRemoteFileTable;

    @FXML
    private TableColumn<FileListModel,FileListModel> fbRemoteFileTblFileName;

    @FXML
    private TableColumn<FileListModel,String> fbRemoteFileTblFileSize;

    @FXML
    public TableColumn<FileListModel,String> fbRemoteFileTblLastMod;

    @FXML
    private TextField fbLocalPath;

    @FXML
    private TextField fbRemotePath;

    private Path currentLocalPath, currentRemotePath;

    ObservableList<FileListModel> currentLocalFileList, currentRemoteFileList;

    @SuppressWarnings("unused")
    public void initialize(){
        try{
            initialiseColumnWidths();
            initializeLocalFileTable();
            initializeRemoteFileTable();

            fbLocalPath.setAlignment(Pos.BASELINE_LEFT);
            fbRemotePath.setAlignment(Pos.BASELINE_LEFT);
        }catch (Exception e){
            SEAGridDialogHelper.showExceptionDialog(e,"Exception Dialog", fbLocalPath.getScene().getWindow(),
                    "Failed opening mass storage browser");
            ((Stage)fbLocalPath.getScene().getWindow()).close();
        }
    }

    private void initialiseColumnWidths(){
        fbLocalFileTblFileName.prefWidthProperty().bind(fbLocalFileTable.widthProperty().divide(3));
        fbLocalFileTblFileSize.prefWidthProperty().bind(fbLocalFileTable.widthProperty().divide(3));
        fbLocalFileTblLastMod.prefWidthProperty().bind(fbLocalFileTable.widthProperty().divide(3));

        fbRemoteFileTblFileName.prefWidthProperty().bind(fbRemoteFileTable.widthProperty().divide(3));
        fbRemoteFileTblFileSize.prefWidthProperty().bind(fbRemoteFileTable.widthProperty().divide(3));
        fbRemoteFileTblLastMod.prefWidthProperty().bind(fbRemoteFileTable.widthProperty().divide(3));
    }

    private void initializeLocalFileTable(){
        String userHome = System.getProperty("user.home");
        this.currentLocalPath = Paths.get(userHome);
        fbLocalFileTblFileName.setCellValueFactory(cellData-> new SimpleObjectProperty(cellData.getValue()));
        fbLocalFileTblFileName.setCellFactory(param -> new TableCell<FileListModel, FileListModel>(){
          @Override
          public void updateItem(FileListModel item, boolean empty){
              if(item != null){
                  HBox hBox = new HBox(2);
                  Image fileImage;
                  if(item.getFileListModelType().equals(FileListModel.FileListModelType.FILE)){
                      fileImage = new Image(MassStorageBrowserController.class.getResourceAsStream("/images/file.png"));
                  }else{
                      fileImage = new Image(MassStorageBrowserController.class.getResourceAsStream("/images/folder.png"));
                  }
                  hBox.getChildren().add(new javafx.scene.image.ImageView(fileImage));
                  hBox.getChildren().add(new Label(item.getFileName()));
                  setGraphic(hBox);
              }else{
                  setGraphic(null);
              }
          }
        });
        fbLocalFileTblFileSize.setCellValueFactory(cellData -> cellData.getValue().getFileListModelType()
                .equals(FileListModel.FileListModelType.FILE) ? new SimpleStringProperty(cellData.getValue().getSize()+" bytes")
                : null);
        fbLocalFileTblFileSize.setCellFactory(param -> new TableCell<FileListModel, String>(){
            @Override
            public void updateItem(String item, boolean empty){
                setText(item);
                setAlignment(Pos.CENTER_RIGHT);
            }
        });
        fbLocalFileTblLastMod.setCellValueFactory(cellData -> cellData.getValue().getFileListModelType()
                .equals(FileListModel.FileListModelType.PARENT_DIR) ? null
                : new SimpleStringProperty(LocalDateTime.ofEpochSecond(cellData.getValue().getLastModifiedTime() / 1000, 0,
                SEAGridContext.getInstance().getTimeZoneOffset()).toString()));
        fbLocalFileTblLastMod.setCellFactory(param -> new TableCell<FileListModel, String>(){
            @Override
            public void updateItem(String item, boolean empty){
                setText(item);
                setAlignment(Pos.CENTER_RIGHT);
            }
        });

        currentLocalFileList = FXCollections.observableArrayList();
        fbLocalFileTable.setItems(currentLocalFileList);

        fbLocalFileTable.setOnMouseClicked(event -> {
            FileListModel fileListModel = fbLocalFileTable.getSelectionModel().getSelectedItem();
            if(fileListModel != null && event.getClickCount()==2) {
                if (fileListModel.getFileListModelType().equals(FileListModel.FileListModelType.DIR)) {
                    currentLocalPath = Paths.get(currentLocalPath.toString() + File.separator + fileListModel.getFileName());
                } else if (fileListModel.getFileListModelType().equals(FileListModel.FileListModelType
                        .PARENT_DIR)) {
                    currentLocalPath = currentLocalPath.getParent();
                }
                populateLocalFileList();
            }
        });

        fbLocalFileTable.setRowFactory(tv -> {
            TableRow<FileListModel> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                if (!row.isEmpty() && !currentLocalFileList.get(row.getIndex()).getFileName().equals("..")) {
                    FileListModel selectedFileListModel = fbLocalFileTable.getSelectionModel().getSelectedItem();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, selectedFileListModel);
                    db.setContent(cc);
                    event.consume();
                }
            });
            return row;
        });
        fbLocalFileTable.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                if (((FileListModel) db.getContent(SERIALIZED_MIME_TYPE)).getFileLocation().equals(FileListModel.FileLocation.REMOTE)) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    event.consume();
                }
            }
        });
        fbLocalFileTable.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                FileListModel draggedFileListModel = (FileListModel) db.getContent(SERIALIZED_MIME_TYPE);
                int dropIndex = fbLocalFileTable.getItems().size();
                fbLocalFileTable.getItems().add(dropIndex, draggedFileListModel);
                event.setDropCompleted(true);
                fbLocalFileTable.getSelectionModel().select(dropIndex);
                event.consume();
            }
        });

        populateLocalFileList();
    }

    private void populateLocalFileList(){
        currentLocalFileList.clear();
        fbLocalPath.setText(currentLocalPath.toString());
        FileListModel fileListModel;
        if(currentLocalPath.getParent() != null){
            fileListModel = new FileListModel("..", FileListModel.FileListModelType.PARENT_DIR,0,0, FileListModel.FileLocation.LOCAL,
                    currentLocalPath.getParent().toString());
            currentLocalFileList.add(fileListModel);
        }
        File parent = new File(currentLocalPath.toString());
        File[] children = parent.listFiles();
        for(File child : children){
            if(child.getName().equals(".") || child.getName().equals("..")) continue;
            fileListModel = new FileListModel(child.getName(),child.isFile() == true
                    ? FileListModel.FileListModelType.FILE : FileListModel.FileListModelType.DIR, child.length(),
                    child.lastModified(), FileListModel.FileLocation.LOCAL, currentLocalPath.toString() +
                    File.separator + child.getName());
            currentLocalFileList.add(fileListModel);
        }
    }

    private void initializeRemoteFileTable() throws SftpException, JSchException {
        String remoteHome = "/var/www/portal";
        this.currentRemotePath = Paths.get(remoteHome);

        fbRemoteFileTblFileName.setCellValueFactory(cellData-> new SimpleObjectProperty(cellData.getValue()));
        fbRemoteFileTblFileName.setCellFactory(param -> new TableCell<FileListModel, FileListModel>(){
            @Override
            public void updateItem(FileListModel item, boolean empty){
                if(item != null){
                    HBox hBox = new HBox(2);
                    Image fileImage;
                    if(item.getFileListModelType().equals(FileListModel.FileListModelType.FILE)){
                        fileImage = new Image(MassStorageBrowserController.class.getResourceAsStream("/images/file.png"));
                    }else{
                        fileImage = new Image(MassStorageBrowserController.class.getResourceAsStream("/images/folder.png"));
                    }
                    hBox.getChildren().add(new javafx.scene.image.ImageView(fileImage));
                    hBox.getChildren().add(new Label(item.getFileName()));
                    setGraphic(hBox);
                }else{
                    setGraphic(null);
                }
            }
        });
        fbRemoteFileTblFileSize.setCellValueFactory(cellData -> cellData.getValue().getFileListModelType()
                .equals(FileListModel.FileListModelType.FILE) ? new SimpleStringProperty(cellData.getValue().getSize()+" bytes")
                : null);
        fbRemoteFileTblFileSize.setCellFactory(param -> new TableCell<FileListModel, String>(){
            @Override
            public void updateItem(String item, boolean empty){
                setText(item);
                setAlignment(Pos.CENTER_RIGHT);
            }
        });
        fbRemoteFileTblLastMod.setCellValueFactory(cellData -> cellData.getValue().getFileListModelType()
                .equals(FileListModel.FileListModelType.PARENT_DIR) ? null
                : new SimpleStringProperty(LocalDateTime.ofEpochSecond(cellData.getValue().getLastModifiedTime() / 1000, 0,
                SEAGridContext.getInstance().getTimeZoneOffset()).toString()));
        fbRemoteFileTblLastMod.setCellFactory(param -> new TableCell<FileListModel, String>(){
            @Override
            public void updateItem(String item, boolean empty){
                setText(item);
                setAlignment(Pos.CENTER_RIGHT);
            }
        });

        currentRemoteFileList = FXCollections.observableArrayList();
        fbRemoteFileTable.setItems(currentRemoteFileList);

        fbRemoteFileTable.setOnMouseClicked(event -> {
            FileListModel fileListModel = fbRemoteFileTable.getSelectionModel().getSelectedItem();
            if(fileListModel != null && event.getClickCount()==2) {
                if (fileListModel.getFileListModelType().equals(FileListModel.FileListModelType.DIR)) {
                    currentRemotePath = Paths.get(currentRemotePath.toString() + "/" + fileListModel.getFileName());
                } else if (fileListModel.getFileListModelType().equals(FileListModel.FileListModelType
                        .PARENT_DIR)) {
                    currentRemotePath = currentRemotePath.getParent();
                }
                try {
                    populateRemoteFileTable();
                } catch (Exception e) {
                    e.printStackTrace();
                    SEAGridDialogHelper.showExceptionDialog(e,"Exception Dialog",fbRemoteFileTable.getScene().getWindow(),
                            "Failed to load remote directory information");
                }
            }
        });

        fbRemoteFileTable.setRowFactory(tv -> {
            TableRow<FileListModel> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                if (!row.isEmpty() && !currentRemoteFileList.get(row.getIndex()).getFileName().equals("..")) {
                    FileListModel selectedFileListModel = fbRemoteFileTable.getSelectionModel().getSelectedItem();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, selectedFileListModel);
                    db.setContent(cc);
                    event.consume();
                }
            });
            return row;
        });
        fbRemoteFileTable.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                if (((FileListModel) db.getContent(SERIALIZED_MIME_TYPE)).getFileLocation().equals(FileListModel.FileLocation.LOCAL)) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    event.consume();
                }
            }
        });
        fbRemoteFileTable.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                FileListModel draggedFileListModel = (FileListModel) db.getContent(SERIALIZED_MIME_TYPE);
                int dropIndex = fbRemoteFileTable.getItems().size();
                fbRemoteFileTable.getItems().add(dropIndex, draggedFileListModel);
                event.setDropCompleted(true);
                fbRemoteFileTable.getSelectionModel().select(dropIndex);
                event.consume();
            }
        });

        populateRemoteFileTable();
    }

    private void populateRemoteFileTable() throws JSchException, SftpException {
        currentRemoteFileList.clear();
        fbRemotePath.setText(currentRemotePath.toString());
        FileListModel fileListModel;
        if(currentRemotePath.getParent() != null){
            fileListModel = new FileListModel("..", FileListModel.FileListModelType.PARENT_DIR,0,0, FileListModel.FileLocation.REMOTE,
                    currentRemotePath.getParent().toString());
            currentRemoteFileList.add(fileListModel);
        }
        Vector<ChannelSftp.LsEntry> children = StorageManager.getInstance().getDirectoryListing(currentRemotePath.toString());
        for(ChannelSftp.LsEntry lsEntry : children){
            if(lsEntry.getFilename().equals(".") || lsEntry.getFilename().equals("..")) continue;
            fileListModel = new FileListModel(lsEntry.getFilename(), lsEntry.getAttrs().isDir() == false
                    ? FileListModel.FileListModelType.FILE : FileListModel.FileListModelType.DIR, lsEntry.getAttrs().getSize(),
                    lsEntry.getAttrs().getATime() * 1000L, FileListModel.FileLocation.REMOTE, currentRemotePath.toString()
                    + "/" + lsEntry.getFilename());
            currentRemoteFileList.add(fileListModel);
        }
    }
}