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
package org.seagrid.desktop.connectors.file;

import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class FileUploadTask extends FileTask {
    private final static Logger logger = LoggerFactory.getLogger(FileUploadTask.class);

    private String remoteFilePath, localFilePath;

    public FileUploadTask(String remoteFilePath, String localFilePath) throws JSchException {
        super();
        this.remoteFilePath = remoteFilePath;
        this.localFilePath = localFilePath;
    }

    @Override
    protected Void call() throws Exception {
        uploadFile(remoteFilePath, localFilePath);
        return null;
    }

    public void uploadFile(String remoteFilePath, String localFilePath) throws IOException {

    }
}