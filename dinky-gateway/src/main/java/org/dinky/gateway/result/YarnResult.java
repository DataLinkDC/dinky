/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.gateway.result;

import org.dinky.gateway.enums.GatewayType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * YarnResult
 *
 * @since 2021/10/29
 */
public class YarnResult extends AbstractGatewayResult {

    private String appId;
    private String webURL;
    private List<String> jids;

    public YarnResult(GatewayType type, LocalDateTime startTime) {
        super(type, startTime);
    }

    public YarnResult(
            String appId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            boolean isSuccess,
            String exceptionMsg) {
        super(startTime, endTime, isSuccess, exceptionMsg);
        this.appId = appId;
    }

    public String getId() {
        return appId;
    }

    @Override
    public void setId(String id) {
        this.appId = id;
    }

    public String getWebURL() {
        return webURL;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    public List<String> getJids() {
        return jids;
    }

    public void setJids(List<String> jids) {
        this.jids = jids;
    }

    public static YarnResult build(GatewayType type) {
        return new YarnResult(type, LocalDateTime.now());
    }
}
