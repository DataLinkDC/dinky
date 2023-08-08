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

package org.dinky.data.model;

import org.dinky.gateway.enums.GatewayType;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.mybatis.model.SuperEntity;

import com.baomidou.mybatisplus.annotation.TableName;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ClusterConfig
 *
 * @since 2021/11/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_cluster_configuration")
public class ClusterConfiguration extends SuperEntity {

    private static final long serialVersionUID = 5830130188542066241L;

    private Integer tenantId;

    private String type;
    private String configJson;

    private Boolean isAvailable;

    private String note;

    public FlinkClusterConfig getFlinkClusterCfg() {
        JSONObject json = new JSONObject(getConfigJson());
        FlinkClusterConfig flinkClusterConfig = json.toBean(FlinkClusterConfig.class);
        flinkClusterConfig.setType(GatewayType.get(type));
        return flinkClusterConfig;
    }
}
