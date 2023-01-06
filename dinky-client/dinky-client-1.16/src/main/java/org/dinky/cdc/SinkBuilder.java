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

import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Table;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * SinkBuilder
 *
 * @author wenmo
 * @since 2022/11/04
 **/
public interface SinkBuilder {

    String getHandle();

    SinkBuilder create(FlinkCDCConfig config);

    DataStreamSource build(CDCBuilder cdcBuilder, StreamExecutionEnvironment env,
                           CustomTableEnvironment customTableEnvironment, DataStreamSource<String> dataStreamSource);

    String getSinkSchemaName(Table table);

    String getSinkTableName(Table table);
}
