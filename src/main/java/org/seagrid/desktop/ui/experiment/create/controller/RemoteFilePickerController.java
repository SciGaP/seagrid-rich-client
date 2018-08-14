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
package org.seagrid.desktop.ui.experiment.create.controller;

import com.github.sardine.DavResource;
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
import org.seagrid.desktop.connectors.NextcloudStorage.NextcloudStorageManager;
import org.seagrid.desktop.ui.commons.SEAGridDialogHelper;
import org.seagrid.desktop.ui.storage.model.FileListModel;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Vector;

public class RemoteFilePickerController {
    private final static Logger logger = LoggerFactory.getLogger(RemoteFilePickerController.class);

    @FXML
    private TableView<FileListModel> fbRemoteFileTable;

    @FXML
    private TableColumn<FileListModel,FileListModel> fbRemoteFileTblFileName;

    @FXML
    private TableColumn<FileListModel,String> fbRemoteFileTblFileSize;

    @FXML
    public TableColumn<FileListModel,String> fbRemoteFileTblLastMod;

    @FXML
    private TextField fbRemotePath;

    @FXML
    private Button selectRemoteFileBtn;

    private Path currentRemotePath;

    private ObservableList<FileListModel> currentRemoteFileList;

    private String selectedFilePath = null;

    @SuppressWarnings("unused")
    public void initialize(){
        try{
            initialiseColumnWidths();
            initializeRemoteFileTable();
            fbRemotePath.setAlignment(Pos.BASELINE_LEFT);
            selectRemoteFileBtn.setOnAction(e->{
                if(selectedFilePath != null){
                    ((Stage)selectRemoteFileBtn.getScene().getWindow()).close();
                }
            });
            selectRemoteFileBtn.setDisable(true);
        }catch (Exception e){
            SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", null,
                    "Failed opening mass storage browser");
            if(fbRemotePath != null && fbRemotePath.getScene() != null && fbRemotePath.getScene().getWindow() != null)
                ((Stage)fbRemotePath.getScene().getWindow()).close();
        }
    }

    private void initialiseColumnWidths(){
        fbRemoteFileTblFileName.prefWidthProperty().bind(fbRemoteFileTable.widthProperty().divide(3));
        fbRemoteFileTblFileSize.prefWidthProperty().bind(fbRemoteFileTable.widthProperty().divide(3));
        fbRemoteFileTblLastMod.prefWidthProperty().bind(fbRemoteFileTable.widthProperty().divide(3));
    }

    private void initializeRemoteFileTable() throws SftpException, JSchException, IOException {
        String remoteHome = "/";
        this.currentRemotePath = Paths.get(remoteHome);

        fbRemoteFileTblFileName.setCellValueFactory(cellData-> new SimpleObjectProperty(cellData.getValue()));
        fbRemoteFileTblFileName.setCellFactory(param -> new TableCell<FileListModel, FileListModel>(){
            @Override
            public void updateItem(FileListModel item, boolean empty){
                if(item != null){
                    HBox hBox = new HBox(2);
                    Image fileImage;
                    if(item.getFileListModelType().equals(FileListModel.FileListModelType.FILE)){
                        fileImage = new Image(RemoteFilePickerController.class.getResourceAsStream("/images/file.png"));
                    }else{
                        fileImage = new Image(RemoteFilePickerController.class.getResourceAsStream("/images/folder.png"));
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
            if(fileListModel != null) {
                selectRemoteFileBtn.setDisable(true);

                //File Selected
                if (event.getClickCount() == 1) {
                    if (fileListModel.getFileListModelType().equals(FileListModel.FileListModelType
                            .FILE)) {
                        selectedFilePath = fileListModel.getFilePath();
                        selectRemoteFileBtn.setDisable(false);
                    }
                }
                if (event.getClickCount() == 2) {
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
                        SEAGridDialogHelper.showExceptionDialogAndWait(e, "Exception Dialog", fbRemoteFileTable.getScene().getWindow(),
                                "Failed to load remote directory information");
                    }
                }
            }
        });

        fbRemoteFileTable.setRowFactory(tv -> {
            TableRow<FileListModel> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                if (!row.isEmpty() && !currentRemoteFileList.get(row.getIndex()).getFileName().equals("..")
                        && currentRemoteFileList.get(row.getIndex()).getFileListModelType().equals(FileListModel
                        .FileListModelType.FILE)) {
                    FileListModel selectedFileListModel = fbRemoteFileTable.getSelectionModel().getSelectedItem();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(new DataFormat("application/x-java-serialized-object"), selectedFileListModel);
                    db.setContent(cc);
                    event.consume();
                }
            });
            return row;
        });

        populateRemoteFileTable();
    }

    private void populateRemoteFileTable() throws JSchException, SftpException, IOException {
        currentRemoteFileList.clear();
        int count = 0;
        fbRemotePath.setText(SEAGridContext.getInstance().getUserName() + currentRemotePath.toString());
        FileListModel fileListModel;
        if(currentRemotePath.getParent() != null){
            fileListModel = new FileListModel("..", FileListModel.FileListModelType.PARENT_DIR,0,0, FileListModel.FileLocation.REMOTE,
                    currentRemotePath.getParent().toString());
            currentRemoteFileList.add(fileListModel);
        }
        List<DavResource> resources = NextcloudStorageManager.getInstance().listDirectories(currentRemotePath.toString());

        for (DavResource res : resources) {
            if (count != 0) {
                if (res.getName().equals(".") || res.getName().equals("..")) continue;
                fileListModel = new FileListModel(res.getName(), res.isDirectory() == false
                        ? FileListModel.FileListModelType.FILE : FileListModel.FileListModelType.DIR, res.getContentLength().intValue(),
                        res.getModified().getTime(), FileListModel.FileLocation.REMOTE, currentRemotePath.toString()
                        + "/" + res.getName());
                currentRemoteFileList.add(fileListModel);
            }
            count++;
        }
    }

    public String getSelectedFilePath() {
        return selectedFilePath;
    }
}