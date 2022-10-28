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
import org.apache.doris.flink.sink.DorisSink;
import org.apache.doris.flink.sink.writer.RowDataSerializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.utils.TypeConversions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * DorisSinkBuilder
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

        Map<String, String> sink = config.getSink();

        // Create FieldNames and FieldType for RowDataSerializer.
        final String[] columnNames = columnNameList.toArray(new String[columnNameList.size()]);
        final List<DataType> dataTypeList = new ArrayList<>();
        for (LogicalType logicalType : columnTypeList) {
            dataTypeList.add(TypeConversions.fromLogicalToDataType(logicalType));
        }
        final DataType[] columnTypes = dataTypeList.toArray(new DataType[dataTypeList.size()]);

        // Create DorisReadOptions for DorisSink.
        final DorisReadOptions.Builder readOptionBuilder = DorisReadOptions.builder();
        if (sink.containsKey(DorisSinkOptions.DORIS_DESERIALIZE_ARROW_ASYNC.key())) {
            readOptionBuilder.setDeserializeArrowAsync(Boolean.valueOf(sink.get(DorisSinkOptions.DORIS_DESERIALIZE_ARROW_ASYNC.key())));
        }
        if (sink.containsKey(DorisSinkOptions.DORIS_DESERIALIZE_QUEUE_SIZE.key())) {
            readOptionBuilder.setDeserializeQueueSize(Integer.valueOf(sink.get(DorisSinkOptions.DORIS_DESERIALIZE_QUEUE_SIZE.key())));
        }
        if (sink.containsKey(DorisSinkOptions.DORIS_EXEC_MEM_LIMIT.key())) {
            readOptionBuilder.setExecMemLimit(Long.valueOf(sink.get(DorisSinkOptions.DORIS_EXEC_MEM_LIMIT.key())));
        }
        if (sink.containsKey(DorisSinkOptions.DORIS_FILTER_QUERY.key())) {
            readOptionBuilder.setFilterQuery(String.valueOf(sink.get(DorisSinkOptions.DORIS_FILTER_QUERY.key())));
        }
        if (sink.containsKey(DorisSinkOptions.DORIS_READ_FIELD.key())) {
            readOptionBuilder.setReadFields(sink.get(DorisSinkOptions.DORIS_READ_FIELD.key()));
        }
        if (sink.containsKey(DorisSinkOptions.DORIS_BATCH_SIZE.key())) {
            readOptionBuilder.setRequestBatchSize(Integer.valueOf(sink.get(DorisSinkOptions.DORIS_BATCH_SIZE.key())));
        }
        if (sink.containsKey(DorisSinkOptions.DORIS_REQUEST_CONNECT_TIMEOUT_MS.key())) {
            readOptionBuilder.setRequestConnectTimeoutMs(Integer.valueOf(sink.get(DorisSinkOptions.DORIS_REQUEST_CONNECT_TIMEOUT_MS.key())));
        }
        if (sink.containsKey(DorisSinkOptions.DORIS_REQUEST_QUERY_TIMEOUT_S.key())) {
            readOptionBuilder.setRequestQueryTimeoutS(Integer.valueOf(sink.get(DorisSinkOptions.DORIS_REQUEST_QUERY_TIMEOUT_S.key())));
        }
        if (sink.containsKey(DorisSinkOptions.DORIS_REQUEST_READ_TIMEOUT_MS.key())) {
            readOptionBuilder.setRequestReadTimeoutMs(Integer.valueOf(sink.get(DorisSinkOptions.DORIS_REQUEST_READ_TIMEOUT_MS.key())));
        }
        if (sink.containsKey(DorisSinkOptions.DORIS_REQUEST_RETRIES.key())) {
            readOptionBuilder.setRequestRetries(Integer.valueOf(sink.get(DorisSinkOptions.DORIS_REQUEST_RETRIES.key())));
        }
        if (sink.containsKey(DorisSinkOptions.DORIS_REQUEST_TABLET_SIZE.key())) {
            readOptionBuilder.setRequestTabletSize(Integer.valueOf(sink.get(DorisSinkOptions.DORIS_REQUEST_TABLET_SIZE.key())));
        }

        // Create DorisOptions for DorisSink.
        DorisOptions.Builder dorisBuilder = DorisOptions.builder();
        dorisBuilder.setFenodes(config.getSink().get(DorisSinkOptions.FENODES.key()))
            .setTableIdentifier(getSinkSchemaName(table) + "." + getSinkTableName(table))
            .setUsername(config.getSink().get(DorisSinkOptions.USERNAME.key()))
            .setPassword(config.getSink().get(DorisSinkOptions.PASSWORD.key())).build();

        // Create DorisExecutionOptions for DorisSink.
        DorisExecutionOptions.Builder executionBuilder = DorisExecutionOptions.builder();
        if (sink.containsKey(DorisSinkOptions.SINK_BUFFER_COUNT.key())) {
            executionBuilder.setBufferCount(Integer.valueOf(sink.get(DorisSinkOptions.SINK_BUFFER_COUNT.key())));
        }
        if (sink.containsKey(DorisSinkOptions.SINK_BUFFER_SIZE.key())) {
            executionBuilder.setBufferSize(Integer.valueOf(sink.get(DorisSinkOptions.SINK_BUFFER_SIZE.key())));
        }
        if (sink.containsKey(DorisSinkOptions.SINK_ENABLE_DELETE.key())) {
            executionBuilder.setDeletable(Boolean.valueOf(sink.get(DorisSinkOptions.SINK_ENABLE_DELETE.key())));
        } else {
            executionBuilder.setDeletable(true);
        }
        if (sink.containsKey(DorisSinkOptions.SINK_LABEL_PREFIX.key())) {
            executionBuilder.setLabelPrefix(sink.get(DorisSinkOptions.SINK_LABEL_PREFIX.key()) + "-" + getSinkSchemaName(table) + "_" + getSinkTableName(table));
        } else {
            executionBuilder.setLabelPrefix("dlink-" + getSinkSchemaName(table) + "_" + getSinkTableName(table) + UUID.randomUUID());
        }
        if (sink.containsKey(DorisSinkOptions.SINK_MAX_RETRIES.key())) {
            executionBuilder.setMaxRetries(Integer.valueOf(sink.get(DorisSinkOptions.SINK_MAX_RETRIES.key())));
        }

        Properties properties = getProperties();
        // Doris 1.1 need to this para to support delete
        properties.setProperty("columns", String.join(",", columnNameList) + ",__DORIS_DELETE_SIGN__");

        executionBuilder.setStreamLoadProp(properties);

        // Create DorisSink.
        DorisSink.Builder<RowData> builder = DorisSink.builder();
        builder.setDorisReadOptions(readOptionBuilder.build())
            .setDorisExecutionOptions(executionBuilder.build())
            .setSerializer(RowDataSerializer.builder()
                .setFieldNames(columnNames)
                .setType("json")
                .enableDelete(true)
                .setFieldType(columnTypes).build())
            .setDorisOptions(dorisBuilder.build());

        rowDataDataStream.sinkTo(builder.build()).name("Doris Sink(table=[" + getSinkSchemaName(table) + "." + getSinkTableName(table) + "])");
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
