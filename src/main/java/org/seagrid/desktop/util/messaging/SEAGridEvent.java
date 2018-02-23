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
package org.seagrid.desktop.util.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SEAGridEvent {
    private final static Logger logger = LoggerFactory.getLogger(SEAGridEvent.class);

    public enum SEAGridEventType{
        EXPERIMENT_CREATED, FILE_DOWNLOADED, EXPERIMENT_LAUNCHED, EXPERIMENT_DELETED, FILE_UPLOADED, LOGOUT,
        EXPERIMENT_CANCELLED, EXPERIMENT_EDIT_REQUEST, EXPERIMENT_UPDATED, EXPERIMENT_CLONED, EXPORT_GAUSSIAN_EXP,
        EXPORT_GAMESS_EXP, EXPORT_NWCHEM_EXP, EXPORT_PSI4_EXP, EXPORT_MOLCAS_EXP, PROJECT_CREATED
    }

    private SEAGridEventType eventType;

    private Object payload;

    public SEAGridEvent(SEAGridEventType eventType, Object payload){
        this.eventType = eventType;
        this.payload = payload;
    }

    public SEAGridEventType getEventType() {
        return eventType;
    }

    public void setEventType(SEAGridEventType eventType) {
        this.eventType = eventType;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}