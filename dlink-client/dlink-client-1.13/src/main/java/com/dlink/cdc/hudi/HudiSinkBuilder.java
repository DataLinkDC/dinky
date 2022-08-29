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

package com.dlink.cdc.hudi;

import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Table;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.hudi.common.model.HoodieRecord;
import org.apache.hudi.common.model.HoodieTableType;
import org.apache.hudi.common.model.WriteOperationType;
import org.apache.hudi.configuration.FlinkOptions;
import org.apache.hudi.configuration.OptionsResolver;
import org.apache.hudi.sink.utils.Pipelines;
import org.apache.hudi.util.AvroSchemaConverter;
import org.apache.hudi.util.StreamerUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * HudiSinkBuilder
 *
 * @author wenmo
 * @since 2022/4/22 23:50
 */
public class HudiSinkBuilder extends AbstractSinkBuilder implements Serializable {

    private static final String KEY_WORD = "datastream-hudi";
    private static final long serialVersionUID = 5324199407472847422L;

    public HudiSinkBuilder() {
    }

    public HudiSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new HudiSinkBuilder(config);
    }

    @Override
    public void addSink(
        StreamExecutionEnvironment env,
        DataStream<RowData> rowDataDataStream,
        Table table,
        List<String> columnNameList,
        List<LogicalType> columnTypeList) {

        final String[] columnNames = columnNameList.toArray(new String[columnNameList.size()]);
        final LogicalType[] columnTypes = columnTypeList.toArray(new LogicalType[columnTypeList.size()]);

        final String tableName = getSinkTableName(table);

        Integer parallelism = 1;
        boolean isMor = true;
        Map<String, String> sink = config.getSink();
        Configuration configuration = Configuration.fromMap(sink);
        if (sink.containsKey("parallelism")) {
            parallelism = Integer.valueOf(sink.get("parallelism"));
        }
        if (configuration.contains(FlinkOptions.PATH)) {
            configuration.set(FlinkOptions.PATH, configuration.getValue(FlinkOptions.PATH) + tableName);
        }
        if (sink.containsKey(FlinkOptions.TABLE_TYPE.key())) {
            isMor = HoodieTableType.MERGE_ON_READ.name().equals(sink.get(FlinkOptions.TABLE_TYPE.key()));
        }
        configuration.set(FlinkOptions.TABLE_NAME, tableName);
        configuration.set(FlinkOptions.HIVE_SYNC_DB, getSinkSchemaName(table));
        configuration.set(FlinkOptions.HIVE_SYNC_TABLE, tableName);

        long ckpTimeout = rowDataDataStream.getExecutionEnvironment()
            .getCheckpointConfig().getCheckpointTimeout();
        configuration.setLong(FlinkOptions.WRITE_COMMIT_ACK_TIMEOUT, ckpTimeout);

        RowType rowType = RowType.of(false, columnTypes, columnNames);
        configuration.setString(FlinkOptions.SOURCE_AVRO_SCHEMA,
            AvroSchemaConverter.convertToSchema(rowType).toString());

        // bulk_insert mode
        final String writeOperation = configuration.get(FlinkOptions.OPERATION);
        if (WriteOperationType.fromValue(writeOperation) == WriteOperationType.BULK_INSERT) {
            Pipelines.bulkInsert(configuration, rowType, rowDataDataStream);
        } else
            // Append mode
            if (OptionsResolver.isAppendMode(configuration)) {
                Pipelines.append(configuration, rowType, rowDataDataStream);
            } else {

                DataStream<HoodieRecord> hoodieRecordDataStream = Pipelines.bootstrap(configuration, rowType, parallelism, rowDataDataStream);
                DataStream<Object> pipeline = Pipelines.hoodieStreamWrite(configuration, parallelism, hoodieRecordDataStream);

                // compaction
                if (StreamerUtil.needsAsyncCompaction(configuration)) {
                    Pipelines.compact(configuration, pipeline);
                } else {
                    Pipelines.clean(configuration, pipeline);
                }
                if (isMor) {
                    Pipelines.clean(configuration, pipeline);
                    Pipelines.compact(configuration, pipeline);
                }
            }
    }
}
