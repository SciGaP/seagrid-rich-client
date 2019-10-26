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
package org.seagrid.desktop.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SEAGridConfig {
    private final static Logger logger = LoggerFactory.getLogger(SEAGridConfig.class);

    public static final boolean DEV = false; //true

    public static final String USER_NAME = "user.name";
    public static final String AUTHENTICATED = "authenticated";
    public static final String OAUTH_TOKEN = "oauth.token";
    public static final String OAUTH_REFRESH_TOKEN = "oauth.refresh.token";
    public static final String OAUTH_TOKEN_EXPIRATION_TIME = "oauth.expiration.time";
    public static final String AIRAVATA_HOST = "airavata.host";
    public static final String DEV_AIRAVATA_HOST = "dev.airavata.host";
    public static final String AIRAVATA_PORT = "airavata.port";
    public static final String DEV_AIRAVATA_PORT = "dev.airavata.port";
    public static final String AIRAVATA_GATEWAY_ID = "airavata.gateway-id";
    public static final String DEV_AIRAVATA_GATEWAY_ID = "dev.airavata.gateway-id";
    public static final String SFTP_HOST = "sftp.host";
    public static final String DEV_SFTP_HOST = "dev.sftp.host";
    public static final String SFTP_PORT = "sftp.port";
    public static final String DEV_SFTP_PORT = "dev.sftp.port";

    public static final String DEFAULT_FILE_DOWNLOAD_PATH = "default.file.download.path";
    public static final java.lang.String GATEWAY_STORAGE_ID = "gateway.storage.resource.id";
    public static final java.lang.String DEV_GATEWAY_STORAGE_ID = "dev.gateway.storage.resource.id";
    public static final java.lang.String REMOTE_DATA_DIR_ROOT = "remote.data.dir.root";
    public static final java.lang.String DEV_REMOTE_DATA_DIR_ROOT = "dev.remote.data.dir.root";
    public static final java.lang.String REMOTE_DATA_DIR_PREFIX = "remote.data.dir.prefix";
    public static final java.lang.String DEV_REMOTE_DATA_DIR_PREFIX = "dev.remote.data.dir.prefix";
    public static final String GROUP_RESOURCE_PROFILE_ID = "group.resource.profile.id";
    public static final String DEV_GROUP_RESOURCE_PROFILE_ID = "dev.group.resource.profile.id";
}