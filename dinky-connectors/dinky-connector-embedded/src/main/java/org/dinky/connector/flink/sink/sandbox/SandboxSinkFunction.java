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

import org.dinky.assertion.Asserts;
import org.dinky.sandbox.metadata.ColumnInfo;
import org.dinky.sandbox.socket.SocketClient;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.table.types.utils.TypeConversions;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SandboxSinkFunction<IN> extends RichSinkFunction<IN> {

    private final String host;
    private final int port;
    private final String boxId;
    private final String tableId;
    private final RowType rowType;
    private final Integer maxRowNum;
    private final boolean autoCancel;
    private final boolean useChangeLog;
    private final List<ColumnInfo> columnInfos;
    private final AtomicInteger counter;

    private transient SocketClient socketClient;
    private transient boolean isConnected = false;
    private transient RowRowConverter rowRowConverter;

    public SandboxSinkFunction(
            String host,
            int port,
            String boxId,
            String tableId,
            RowType rowType,
            Integer maxRowNum,
            boolean autoCancel,
            boolean useChangeLog) {
        this.host = host;
        this.port = port;
        this.boxId = boxId;
        this.tableId = tableId;
        this.rowType = rowType;
        this.maxRowNum = maxRowNum;
        this.autoCancel = autoCancel;
        this.useChangeLog = useChangeLog;
        this.columnInfos = rowType.getFields().stream()
                .map(field -> ColumnInfo.buildByFlinkColumn(field, false))
                .collect(Collectors.toList());
        this.counter = new AtomicInteger(0);
    }

    public SandboxSinkFunction(
            String host,
            int port,
            String boxId,
            String tableId,
            List<ColumnInfo> columnInfos,
            Integer maxRowNum,
            boolean autoCancel,
            boolean useChangeLog) {
        this.host = host;
        this.port = port;
        this.boxId = boxId;
        this.tableId = tableId;
        this.rowType = null;
        this.maxRowNum = maxRowNum;
        this.autoCancel = autoCancel;
        this.useChangeLog = useChangeLog;
        this.columnInfos = columnInfos;
        this.counter = new AtomicInteger(0);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        try {
            if (rowType != null) {
                DataType dataType = TypeConversions.fromLogicalToDataType(rowType);
                rowRowConverter = RowRowConverter.create(dataType);
                rowRowConverter.open(Thread.currentThread().getContextClassLoader());
            }

            socketClient = new SocketClient(host, port);
            socketClient.connect();
            isConnected = true;

            autoCreateTable();
            log.info("SandboxSink {} Connected to the Socket: {}", tableId, host);
        } catch (Exception e) {
            log.error("SandboxSink {} Failed to connect to the Socket: {}", tableId, host, e);
            throw e;
        }
    }

    @Override
    public void invoke(IN value, Context context) throws Exception {

        if (Asserts.isNotNull(maxRowNum)) {
            long currentCount = counter.incrementAndGet();
            if (currentCount > maxRowNum) {
                if (autoCancel) {
                    log.warn(
                            "SandboxSink {} has sent {} pieces of data, and the task has been automatically stopped.",
                            tableId,
                            currentCount);
                    throw new Exception(String.format(
                            "SandboxSink %s has sent %d pieces of data, and the task has been automatically stopped.",
                            tableId, currentCount));
                } else {
                    log.warn(
                            "SandboxSink {} has sent {} pieces of data. Data collection has stopped",
                            tableId,
                            currentCount);
                    return;
                }
            }
        }

        if (!isConnected || socketClient == null || !socketClient.isConnected()) {
            log.warn("The Socket of SandboxSink {} is not connected. Attempting to reconnect.", tableId);
            reconnect();
        }

        try {
            if (value instanceof RowData && rowRowConverter != null) {
                Row row = rowRowConverter.toExternal((RowData) value);
                socketClient.sendData(boxId, tableId, row);
            } else {
                socketClient.sendData(boxId, tableId, value);
            }
        } catch (Exception e) {
            log.error("SandboxSink {} failed to send data.", tableId, e);
            reconnect();
            throw e;
        }
    }

    @Override
    public void close() throws Exception {
        if (socketClient != null) {
            socketClient.close();
            isConnected = false;
        }

        log.info("SandboxSink {} has been closed.", tableId);
        super.close();
    }

    private void autoCreateTable() {
        String tableType = useChangeLog ? "CHANGE_LOG" : "PRIMARY_KEY_TABLE";
        socketClient.sendCreateTableEvent(boxId, tableId, columnInfos, tableType);
    }

    private void reconnect() {
        try {
            if (socketClient != null) {
                socketClient.close();
            }

            socketClient = new SocketClient(host, port);
            socketClient.connect();
            isConnected = true;

            log.info("SandboxSink {} has reconnected successfully.", tableId);
        } catch (Exception e) {
            log.error("The reconnection of SandboxSink {} has failed.", tableId, e);
            isConnected = false;
        }
    }
}
