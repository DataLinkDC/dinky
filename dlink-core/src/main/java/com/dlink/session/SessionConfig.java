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

package com.dlink.session;

import com.dlink.executor.ExecutorSetting;

import lombok.Getter;
import lombok.Setter;

/**
 * SessionConfig
 *
 * @author wenmo
 * @since 2021/7/6 21:59
 */
@Getter
@Setter
public class SessionConfig {
    private SessionType type;
    private boolean useRemote;
    private Integer clusterId;
    private String clusterName;
    private String address;

    public enum SessionType {
        PUBLIC,
        PRIVATE
    }

    public SessionConfig(SessionType type, boolean useRemote, Integer clusterId, String clusterName, String address) {
        this.type = type;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
        this.clusterName = clusterName;
        this.address = address;
    }

    public static SessionConfig build(String type, boolean useRemote, Integer clusterId, String clusterName, String address) {
        return new SessionConfig(SessionType.valueOf(type), useRemote, clusterId, clusterName, address);
    }

    public ExecutorSetting getExecutorSetting() {
        return new ExecutorSetting(true);
    }

}
