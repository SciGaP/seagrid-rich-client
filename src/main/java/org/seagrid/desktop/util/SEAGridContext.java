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

    private Map<String,String> properties = new HashMap<>();

    private static SEAGridContext instance;

    private boolean authenticated = false;

    private SEAGridContext(){}

    public static SEAGridContext getInstance(){
        if(SEAGridContext.instance == null){
            SEAGridContext.instance = new SEAGridContext();
        }
        return SEAGridContext.instance;
    }

    public void setProperty(String key, String value){
        properties.put(key,value);
    }

    public String getProperty(String key){
        return properties.get(key);
    }

    public ZoneOffset getTimeZoneOffset(){
        return ZoneOffset.UTC;
    }

    public String getFileDownloadLocation(){ return "/Users/supun/Desktop";}

    public String getAiravataGatewayId(){ return "default";}

    public String getUserName(){ return "master";}

    public int getMaxRecentExpCount(){ return 20; }

    public String getRecentExperimentsDummyId(){ return "$$$$$$"; }

    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }

    public boolean getAuthenticated() { return this.authenticated; }
}