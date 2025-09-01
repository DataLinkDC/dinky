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

package org.dinky.connector.flink.sink.sandbox;

import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.types.logical.RowType;

import java.io.Serializable;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SandboxDynamicTableSink implements DynamicTableSink, Serializable {

    private static final long serialVersionUID = 1L;

    private final String host;
    private final int port;
    private final String boxId;
    private final String table;
    private final RowType rowType;
    private final Integer maxRowNum;
    private final boolean autoCancel;
    private final boolean useChangeLog;

    public SandboxDynamicTableSink(
            String host,
            int port,
            String boxId,
            String table,
            RowType rowType,
            Integer maxRowNum,
            boolean autoCancel,
            boolean useChangeLog) {
        this.host = host;
        this.port = port;
        this.boxId = boxId;
        this.table = table;
        this.rowType = rowType;
        this.maxRowNum = maxRowNum;
        this.autoCancel = autoCancel;
        this.useChangeLog = useChangeLog;
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        return requestedMode;
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
        return SinkFunctionProvider.of(
                new SandboxSinkFunction(host, port, boxId, table, rowType, maxRowNum, autoCancel, useChangeLog), 1);
    }

    @Override
    public DynamicTableSink copy() {
        return new SandboxDynamicTableSink(host, port, boxId, table, rowType, maxRowNum, autoCancel, useChangeLog);
    }

    @Override
    public String asSummaryString() {
        return String.format("SandboxSink(host=%s, port=%d, boxId=%s, table=%s)", host, port, boxId, table);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SandboxDynamicTableSink that = (SandboxDynamicTableSink) o;
        return Objects.equals(host, that.host)
                && Objects.equals(port, that.port)
                && Objects.equals(boxId, that.boxId)
                && Objects.equals(table, that.table)
                && Objects.equals(rowType, that.rowType)
                && Objects.equals(maxRowNum, that.maxRowNum)
                && Objects.equals(autoCancel, that.autoCancel)
                && Objects.equals(useChangeLog, that.useChangeLog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, boxId, table, rowType, maxRowNum, autoCancel, useChangeLog);
    }
}
