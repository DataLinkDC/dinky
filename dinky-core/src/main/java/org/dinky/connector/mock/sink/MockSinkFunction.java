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

package org.dinky.connector.mock.sink;

import org.dinky.constant.FlinkConstant;

import org.apache.flink.api.common.accumulators.SerializedListAccumulator;
import org.apache.flink.api.common.typeutils.base.MapSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.RowType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockSinkFunction extends RichSinkFunction<RowData> {
    private final RowType rowType;
    private final String tableIdentifier;
    // when columns is in VARCHAR or STRING type, rowData will be generated to BinaryStringData, which is not
    // serialized, as a result, SerializedListAccumulator is used here
    private final SerializedListAccumulator<Map<String, String>> rowDataList;

    public MockSinkFunction(String tableName, RowType rowType) {
        this.rowType = rowType;
        this.tableIdentifier = tableName;
        this.rowDataList = new SerializedListAccumulator<>();
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        getRuntimeContext().addAccumulator(tableIdentifier, rowDataList);
        super.open(parameters);
    }

    @Override
    public void invoke(RowData rowData, Context context) throws Exception {
        List<String> fieldNames = rowType.getFieldNames();
        Map<String, String> rowDataMap = new HashMap<>();
        rowDataMap.put(FlinkConstant.OP, rowData.getRowKind().shortString());
        for (int i = 0; i < fieldNames.size(); i++) {
            RowData.FieldGetter fieldGetter = RowData.createFieldGetter(rowType.getTypeAt(i), i);
            switch (rowType.getTypeAt(i).getTypeRoot()) {
                case BINARY:
                case VARBINARY:
                    rowDataMap.put(
                            fieldNames.get(i),
                            new String((byte[]) fieldGetter.getFieldOrNull(rowData), StandardCharsets.UTF_8));
                    break;
                default:
                    rowDataMap.put(fieldNames.get(i), String.valueOf(fieldGetter.getFieldOrNull(rowData)));
            }
        }
        rowDataList.add(rowDataMap, new MapSerializer<>(new StringSerializer(), new StringSerializer()));
    }
}
