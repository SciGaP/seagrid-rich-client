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
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class GuiDirUploadTask extends GuiFileTask {
    private final static Logger logger = LoggerFactory.getLogger(GuiDirUploadTask.class);

    private String remoteDirPath, localDirPath;
    private double totalBytesRead = 0, totalSize = 0;

    public GuiDirUploadTask(String remoteDirPath, String localDirPath) throws JSchException {
        super();
        this.remoteDirPath = remoteDirPath;
        this.localDirPath = localDirPath;
    }

    @Override
    protected Boolean call() throws Exception {
        calculateTotalSize(localDirPath);
        return downloadDir(remoteDirPath, localDirPath);
    }

    public Boolean downloadDir(String remoteDirPath, String localDirPath) throws SftpException, IOException {
        createRemoteDirsIfNotExists(remoteDirPath);
        File localDir = new File(localDirPath);
        for(File file : localDir.listFiles()){
            if(file.getName().equals(".") || file.getName().equals("..")){
                continue;
            }
            if(file.isDirectory()){
                String tempLocalDir = file.getAbsolutePath();
                String tempRemoteDir = remoteDirPath + "/" + file.getName();
                downloadDir(tempRemoteDir, tempLocalDir);
            }else{
                FileInputStream localInputStream = new FileInputStream(file);
                BufferedOutputStream remoteOutputStream = new BufferedOutputStream(channelSftp.put(remoteDirPath
                        + "/" + file.getName()));
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = localInputStream.read(buffer)) != -1) {
                    remoteOutputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    updateMessage("Uploaded " + totalBytesRead + " bytes");
                    updateProgress(totalBytesRead, totalSize);
                }
                remoteOutputStream.close();
                localInputStream.close();
            }
        }
        return true;
    }

    private void calculateTotalSize(String localDirPath) throws SftpException, IOException {
        for (File file : (new File(localDirPath)).listFiles()) {
            if (file.isFile())
                totalSize += file.length();
            else
                calculateTotalSize(file.getPath().toString());
        }
    }

    private void createRemoteDirsIfNotExists(String parentDirPath) throws SftpException {
        String[] folders = parentDirPath.split( "/" );
        for ( String folder : folders ) {
            if ( folder.length() > 0 ) {
                try {
                    channelSftp.cd(folder);
                }
                catch ( SftpException e ) {
                    channelSftp.mkdir( folder );
                    channelSftp.chmod(Integer.parseInt("777",8), folder);
                    channelSftp.cd( folder );
                }
            }
        }
    }
}