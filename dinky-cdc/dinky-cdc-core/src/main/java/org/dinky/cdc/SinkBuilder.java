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

package org.dinky.cdc;

import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Table;
import org.dinky.executor.CustomTableEnvironment;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public interface SinkBuilder {

    String getHandle();

    SinkBuilder create(FlinkCDCConfig config);

    DataStreamSource<String> build(
            CDCBuilder cdcBuilder,
            StreamExecutionEnvironment env,
            CustomTableEnvironment customTableEnvironment,
            DataStreamSource<String> dataStreamSource);

    String getSinkSchemaName(Table table);

    String getSinkTableName(Table table);
}
