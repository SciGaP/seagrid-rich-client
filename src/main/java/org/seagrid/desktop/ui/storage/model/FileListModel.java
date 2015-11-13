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
package org.seagrid.desktop.ui.storage.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class FileListModel implements Serializable{
    private final static Logger logger = LoggerFactory.getLogger(FileListModel.class);

    public static enum FileListModelType{
        FILE, DIR, PARENT_DIR
    }

    public static enum FileLocation{
        REMOTE, LOCAL
    }

    private String fileName;
    private FileListModelType fileListModelType;
    private FileLocation fileLocation;
    private long size;
    private long lastModifiedTime;
    private String filePath;

    public FileListModel(String fileName, FileListModelType fileListModelType, long size, long lastModifiedTime, FileLocation
                         fileLocation, String filePath) {
        this.fileName = fileName;
        this.fileListModelType = fileListModelType;
        this.size = size;
        this.lastModifiedTime = lastModifiedTime;
        this.fileLocation = fileLocation;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileListModelType getFileListModelType() {
        return fileListModelType;
    }

    public void setFileListModelType(FileListModelType fileListModelType) {
        this.fileListModelType = fileListModelType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public FileLocation getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(FileLocation fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}