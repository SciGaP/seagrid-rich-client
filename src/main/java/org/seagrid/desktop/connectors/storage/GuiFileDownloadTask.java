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
package org.seagrid.desktop.connectors.storage;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class GuiFileDownloadTask extends GuiFileTask {
    private final static Logger logger = LoggerFactory.getLogger(GuiFileDownloadTask.class);

    private String remoteFilePath, localFilePath;

    public GuiFileDownloadTask(String remoteFilePath, String localFilePath) throws JSchException {
        super();
        this.remoteFilePath = remoteFilePath;
        this.localFilePath = localFilePath;
    }

    @Override
    protected Boolean call() throws Exception {
        return downloadFile(remoteFilePath, localFilePath);
    }

    public Boolean downloadFile(String remoteFilePath, String localFilePath) throws SftpException, IOException {
        remoteFilePath = remoteFilePath.replace("\\","/");
        remoteFilePath = remoteFilePath.replaceAll(SEAGridContext.getInstance().getUserName(), "");
        InputStream remoteInputStream = new BufferedInputStream(channelSftp.get(remoteFilePath));
        File localFile = new File(localFilePath);
        if(!localFile.getParentFile().exists()){
            localFile.getParentFile().mkdirs();
        }
        OutputStream localOutputStream = new FileOutputStream(localFile);
        SftpATTRS attrs = channelSftp.lstat(remoteFilePath);
        long fileSize = attrs.getSize();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        long totalBytesRead = 0;
        double percentCompleted = 0;

        while ((bytesRead = remoteInputStream.read(buffer)) != -1) {
            localOutputStream.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
            percentCompleted = ((double)totalBytesRead)/ fileSize;
            updateMessage("Downloaded " + totalBytesRead + " bytes");
            updateProgress(percentCompleted, 1);
        }

        remoteInputStream.close();
        localOutputStream.close();

        return true;
    }
}