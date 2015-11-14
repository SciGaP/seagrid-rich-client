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

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class SEAGridContext {
    private final static Logger logger = LoggerFactory.getLogger(SEAGridContext.class);

    private Map<String,String> dynamicConfigurations = new HashMap<>();

    private static SEAGridContext instance;

    private SEAGridContext(){}

    public static SEAGridContext getInstance(){
        if(SEAGridContext.instance == null){
            SEAGridContext.instance = new SEAGridContext();
        }
        return SEAGridContext.instance;
    }

    public ZoneOffset getTimeZoneOffset(){
        return ZoneOffset.UTC;
    }

    public String getFileDownloadLocation(){ return "/Users/supun/Desktop";}

    public String getAiravataGatewayId(){ return "default";}

    public void setUserName(String userName){ dynamicConfigurations.put(SEAGridConfig.USER_NAME, userName);}

    public String getUserName(){ return dynamicConfigurations.get(SEAGridConfig.USER_NAME);}

    public int getMaxRecentExpCount(){ return 20; }

    public String getRecentExperimentsDummyId(){ return "$$$$$$"; }

    public void setAuthenticated(boolean authenticated) {
        if(authenticated)
            dynamicConfigurations.put(SEAGridConfig.AUTHENTICATED, "true");
    }

    public boolean getAuthenticated() { return dynamicConfigurations.containsKey(SEAGridConfig.AUTHENTICATED); }

    public void setOAuthToken(String oauthToken) {
        dynamicConfigurations.put(SEAGridConfig.OAUTH_TOKEN,oauthToken);
    }

    public String getOAuthToken(){
        return dynamicConfigurations.get(SEAGridConfig.OAUTH_TOKEN);
    }

    public void setRefreshToken(String refreshToken){
        dynamicConfigurations.put(SEAGridConfig.OAUTH_REFRESH_TOKEN, refreshToken);
    }

    public String getRefreshToken(){
        return dynamicConfigurations.get(SEAGridConfig.OAUTH_REFRESH_TOKEN);
    }

    public void setTokenExpiaryTime(long tokenExpiarationTime) {
        dynamicConfigurations.put(SEAGridConfig.OAUTH_TOKEN_EXPIRATION_TIME, tokenExpiarationTime+"");
    }

    public long getOAuthTokenExpirationTime(){
        return Long.parseLong(dynamicConfigurations.get(SEAGridConfig.OAUTH_TOKEN_EXPIRATION_TIME));
    }
}