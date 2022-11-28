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

package com.dlink.model;

import com.dlink.db.model.SuperEntity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Cluster
 *
 * @author wenmo
 * @since 2021/5/28 13:53
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_cluster")
public class Cluster extends SuperEntity {

    private static final long serialVersionUID = 3104721227014487321L;

    private Integer tenantId;

    private String type;

    private String hosts;

    private String jobManagerHost;

    private String version;

    private Integer status;

    private String note;

    private Boolean autoRegisters;

    private Integer clusterConfigurationId;

    private Integer taskId;

    public static Cluster autoRegistersCluster(String hosts, String name, String type, Integer clusterConfigurationId, Integer taskId) {
        Cluster cluster = new Cluster();
        cluster.setName(name);
        cluster.setHosts(hosts);
        cluster.setType(type);
        cluster.setClusterConfigurationId(clusterConfigurationId);
        cluster.setTaskId(taskId);
        cluster.setAutoRegisters(true);
        cluster.setEnabled(true);
        return cluster;
    }
}
