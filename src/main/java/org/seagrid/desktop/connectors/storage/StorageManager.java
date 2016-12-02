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

import com.jcraft.jsch.*;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Vector;

public class StorageManager {
    private final static Logger logger = LoggerFactory.getLogger(StorageManager.class);

    private static StorageManager instance;

    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;

    private StorageManager() throws JSchException {
        connect();
    }

    private void connect() throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(
                SEAGridContext.getInstance().getUserName(), SEAGridContext.getInstance().getSFTPHost(),
                SEAGridContext.getInstance().getSFTPPort()
        );
        session.setPassword(SEAGridContext.getInstance().getOAuthToken());
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        channel = session.openChannel("sftp");
        channel.connect();
        channelSftp = (ChannelSftp) channel;
    }

    public static StorageManager getInstance() throws JSchException {
        if(instance==null){
            instance = new StorageManager();
        }
        return instance;
    }

    public Vector<ChannelSftp.LsEntry> getDirectoryListing(String path) throws SftpException, JSchException {
        //channel may get timeout
        if(channelSftp.isClosed()){
            connect();
        }
        channelSftp.cd(path);
        return channelSftp.ls(path);
    }

    public void createSymLink(String oldPath, String newPath) throws JSchException, SftpException {
        if(channelSftp.isClosed()){
            connect();
        }
        createRemoteParentDirsIfNotExists((new File(newPath)).getParentFile().getPath());
        channelSftp.symlink(oldPath, newPath);
    }

    private void createRemoteParentDirsIfNotExists(String parentDirPath) throws SftpException {
        String pwd = channelSftp.pwd();
        String[] folders = parentDirPath.split( "/" );
        for ( String folder : folders ) {
            if ( folder.length() > 0 ) {
                try {
                    channelSftp.cd(folder);
                }
                catch ( SftpException e ) {
                    channelSftp.mkdir( folder );
                    channelSftp.cd( folder );
                }
            }
        }
        channelSftp.cd(pwd);
    }

    public void createDirIfNotExists(String dirPath) throws SftpException {
        String pwd = channelSftp.pwd();
        channelSftp.cd("/");
        String[] folders = dirPath.split( "/" );
        for ( String folder : folders ) {
            if ( folder.length() > 0 ) {
                try {
                    channelSftp.cd(folder);
                }
                catch ( SftpException e ) {
                    channelSftp.mkdir( folder );
                    channelSftp.cd( folder );
                }
            }
        }
        channelSftp.cd(pwd);
    }
}