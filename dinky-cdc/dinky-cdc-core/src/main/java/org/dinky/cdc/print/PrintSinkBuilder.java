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

package org.dinky.cdc.print;

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

import org.dinky.cdc.AbstractSinkBuilder;
import org.dinky.cdc.SinkBuilder;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Table;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.LogicalType;

import java.io.Serializable;
import java.util.List;

public class PrintSinkBuilder extends AbstractSinkBuilder implements Serializable {

    public static final String KEY_WORD = "datastream-print";

    public PrintSinkBuilder() {}

    public PrintSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    protected void addSink(
            DataStream<RowData> rowDataDataStream,
            Table table,
            List<String> columnNameList,
            List<LogicalType> columnTypeList) {
        rowDataDataStream
                .addSink(new RichSinkFunction<RowData>() {
                    @Override
                    public void invoke(RowData value, Context context) throws Exception {
                        System.out.println(value.toString());
                    }
                })
                .name(String.format("Print Sink(table=[%s.%s])", getSinkSchemaName(table), getSinkTableName(table)));
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new PrintSinkBuilder(config);
    }
}
