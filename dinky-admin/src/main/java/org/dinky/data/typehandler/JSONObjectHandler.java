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

package org.dinky.data.typehandler;

import org.dinky.data.flink.checkpoint.CheckPointOverView;
import org.dinky.data.flink.config.CheckpointConfigInfo;
import org.dinky.data.flink.config.FlinkJobConfigInfo;
import org.dinky.data.flink.exceptions.FlinkJobExceptionsDetail;
import org.dinky.data.flink.job.FlinkJobDetailInfo;
import org.dinky.data.model.ext.TaskExtConfig;
import org.dinky.data.model.mapping.ClusterConfigurationMapping;
import org.dinky.data.model.mapping.ClusterInstanceMapping;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.job.JobConfig;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Map;

import com.alibaba.fastjson2.JSONValidator;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@MappedJdbcTypes(value = JdbcType.VARCHAR, includeNullJdbcType = true)
@MappedTypes({
    FlinkJobDetailInfo.class,
    FlinkJobExceptionsDetail.class,
    CheckPointOverView.class,
    CheckpointConfigInfo.class,
    FlinkJobConfigInfo.class,
    ClusterInstanceMapping.class,
    ClusterConfigurationMapping.class,
    FlinkClusterConfig.class,
    TaskExtConfig.class,
    JobConfig.class,
    Map.class
})
public class JSONObjectHandler<T> extends AbstractJsonTypeHandler<T> {

    private final Class<T> type;

    public JSONObjectHandler(Class<T> type) {
        this.type = type;
    }

    @Override
    protected T parse(String content) {
        if (content == null || !JSONValidator.from(content).validate()) {
            log.debug("unknown json：{}", content);
            return null;
        }
        return JSONUtil.toBean(content, type);
    }

    @Override
    protected String toJson(T object) {
        return JSONUtil.toJsonStr(object);
    }
}
