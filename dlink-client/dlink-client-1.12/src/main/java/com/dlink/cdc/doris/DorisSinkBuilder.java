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

package com.dlink.cdc.doris;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Table;

import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.cfg.DorisSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.LogicalType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * DorisSinkBuilder
 *
 * @author wenmo
 * @since 2022/4/20 19:20
 **/
public class DorisSinkBuilder extends AbstractSinkBuilder implements Serializable {

    private static final String KEY_WORD = "datastream-doris";
    private static final long serialVersionUID = 8330362249137471854L;

    public DorisSinkBuilder() {
    }

    public DorisSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new DorisSinkBuilder(config);
    }

    @Override
    public void addSink(
        StreamExecutionEnvironment env,
        DataStream<RowData> rowDataDataStream,
        Table table,
        List<String> columnNameList,
        List<LogicalType> columnTypeList) {

        DorisExecutionOptions.Builder dorisExecutionOptionsBuilder = DorisExecutionOptions.builder();
        Map<String, String> sink = config.getSink();
        if (sink.containsKey("sink.batch.size")) {
            dorisExecutionOptionsBuilder.setBatchSize(Integer.valueOf(sink.get("sink.batch.size")));
        }
        if (sink.containsKey("sink.batch.interval")) {
            dorisExecutionOptionsBuilder.setBatchIntervalMs(Long.valueOf(sink.get("sink.batch.interval")));
        }
        if (sink.containsKey("sink.max-retries")) {
            dorisExecutionOptionsBuilder.setMaxRetries(Integer.valueOf(sink.get("sink.max-retries")));
        }
        if (sink.containsKey("sink.enable-delete")) {
            dorisExecutionOptionsBuilder.setEnableDelete(Boolean.valueOf(sink.get("sink.enable-delete")));
        }
        dorisExecutionOptionsBuilder.setStreamLoadProp(getProperties());

        final String[] columnNames = columnNameList.toArray(new String[columnNameList.size()]);
        final LogicalType[] columnTypes = columnTypeList.toArray(new LogicalType[columnTypeList.size()]);

        rowDataDataStream.addSink(
            DorisSink.sink(
                columnNames,
                columnTypes,
                DorisReadOptions.builder().build(),
                dorisExecutionOptionsBuilder.build(),
                DorisOptions.builder()
                    .setFenodes(config.getSink().get("fenodes"))
                    .setTableIdentifier(getSinkSchemaName(table) + "." + getSinkTableName(table))
                    .setUsername(config.getSink().get("username"))
                    .setPassword(config.getSink().get("password")).build()
            ));
    }

    @Override
    protected Properties getProperties() {
        Properties properties = new Properties();
        Map<String, String> sink = config.getSink();
        for (Map.Entry<String, String> entry : sink.entrySet()) {
            if (Asserts.isNotNullString(entry.getKey()) && entry.getKey().startsWith("sink.properties") && Asserts.isNotNullString(entry.getValue())) {
                properties.setProperty(entry.getKey().replace("sink.properties.", ""), entry.getValue());
            }
        }
        return properties;
    }
}
