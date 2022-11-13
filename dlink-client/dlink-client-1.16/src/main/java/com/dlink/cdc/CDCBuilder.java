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

package com.dlink.cdc;

import com.dlink.exception.SplitTableException;
import com.dlink.model.FlinkCDCConfig;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;
import java.util.Map;

/**
 * CDCBuilder
 *
 * @author wenmo
 * @since 2022/11/04
 **/
public interface CDCBuilder {

    String getHandle();

    CDCBuilder create(FlinkCDCConfig config);

    DataStreamSource<String> build(StreamExecutionEnvironment env);

    List<String> getSchemaList();

    List<String> getTableList();

    Map<String, Map<String, String>> parseMetaDataConfigs();

    String getSchemaFieldName();

    default Map<String, String> parseMetaDataConfig() {
        throw new SplitTableException("此数据源并未实现分库分表");
    }
}
