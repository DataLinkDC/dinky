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

package org.dinky.sandbox;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinky.sandbox.metadata.ColumnInfo;
import org.dinky.sandbox.metadata.TableId;
import org.dinky.sandbox.metadata.TableInfo;
import org.dinky.sandbox.metadata.TableType;
import org.dinky.sandbox.metadata.Tuple;
import org.dinky.sandbox.socket.SandboxSocketServer;
import org.dinky.sandbox.socket.SocketClient;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.catalog.Column;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class SandboxSocketServerTest {

    private static final int SERVER_PORT = 9999;

    @Test
    public void createTableTest() {
        List<ColumnInfo> columns = Arrays.asList(
                ColumnInfo.buildByFlinkColumn(Column.physical("id", DataTypes.INT()), false),
                ColumnInfo.buildByFlinkColumn(Column.physical("name", DataTypes.STRING()), false),
                ColumnInfo.buildByFlinkColumn(Column.physical("age", DataTypes.INT()), false),
                ColumnInfo.buildByFlinkColumn(Column.physical("email", DataTypes.STRING()), false));
        TableId tableId = TableId.of("test_db", "test_create_table");
        Sandbox sandbox = SandboxFactory.getDefaultSandbox();

        SandboxSocketServer sandboxSocketServer = new SandboxSocketServer(sandbox, SERVER_PORT);
        sandboxSocketServer.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try (SocketClient client = new SocketClient("localhost", SERVER_PORT)) {
            client.connect();

            boolean success = client.sendCreateTableEvent("public", tableId.identifier(), columns, "PRIMARY_KEY_TABLE");
            assertThat(success).isTrue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sandboxSocketServer.stop();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThat(sandbox.existTable(tableId)).isTrue();

        TableInfo tableInfo = sandbox.getTableInfo(tableId);
        assertThat(tableInfo).isNotNull();
        assertThat(tableInfo.getTableId()).isEqualTo(tableId);
        assertThat(tableInfo.getTableType()).isEqualTo(TableType.PRIMARY_KEY_TABLE);
        assertThat(tableInfo.getColumns()).hasSize(4);

        assertThat(tableInfo.getColumns().get(0).getName()).isEqualTo("id");
        assertThat(tableInfo.getColumns().get(1).getName()).isEqualTo("name");
        assertThat(tableInfo.getColumns().get(2).getName()).isEqualTo("age");
        assertThat(tableInfo.getColumns().get(3).getName()).isEqualTo("email");

        List<Tuple> data = sandbox.getData(tableId);
        assertThat(data).isEmpty();
    }

    @Test
    public void appendDataTest() {
        List<Column> columns =
                Arrays.asList(Column.physical("id", DataTypes.INT()), Column.physical("name", DataTypes.STRING()));
        List<Row> data = Arrays.asList(
                Row.ofKind(RowKind.INSERT, 1, "Alice"),
                Row.ofKind(RowKind.INSERT, 2, "Bob"),
                Row.ofKind(RowKind.UPDATE_BEFORE, 1, "Alice"),
                Row.ofKind(RowKind.UPDATE_AFTER, 1, "Alice Updated"));
        TableId tableId = TableId.of("test_db", "test_table_append");
        Sandbox sandbox = SandboxFactory.getDefaultSandbox();
        sandbox.registerTable(tableId, TableType.APPEND_TABLE, columns, new int[] {0});

        SandboxSocketServer sandboxSocketServer = new SandboxSocketServer(sandbox, SERVER_PORT);
        sandboxSocketServer.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try (SocketClient client = new SocketClient("localhost", SERVER_PORT)) {
            client.connect();

            data.forEach(dataRow -> {
                client.sendData("public", tableId.identifier(), dataRow, "UTC");
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sandboxSocketServer.stop();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<Tuple> sanboxData = sandbox.getData(tableId);
        assertThat(sanboxData).isNotEmpty();
        assertThat(sanboxData.size()).isEqualTo(4);
        String[] actualOutputs = sanboxData.stream().map(Object::toString).toArray(String[]::new);
        assertThat(actualOutputs).containsExactly("[1, Alice]", "[2, Bob]", "[1, Alice]", "[1, Alice Updated]");
    }

    @Test
    public void upsertDataTest() {
        List<Column> columns =
                Arrays.asList(Column.physical("id", DataTypes.INT()), Column.physical("name", DataTypes.STRING()));
        List<Row> data = Arrays.asList(
                Row.ofKind(RowKind.INSERT, 1, "Alice"),
                Row.ofKind(RowKind.INSERT, 2, "Bob"),
                Row.ofKind(RowKind.UPDATE_BEFORE, 1, "Alice"),
                Row.ofKind(RowKind.UPDATE_AFTER, 1, "Alice Updated"));
        TableId tableId = TableId.of("test_db", "test_table");
        Sandbox sandbox = SandboxFactory.getDefaultSandbox();
        sandbox.registerTable(tableId, TableType.PRIMARY_KEY_TABLE, columns, new int[] {0});

        SandboxSocketServer sandboxSocketServer = new SandboxSocketServer(sandbox, SERVER_PORT);
        sandboxSocketServer.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try (SocketClient client = new SocketClient("localhost", SERVER_PORT)) {
            client.connect();

            data.forEach(dataRow -> {
                client.sendData("public", tableId.identifier(), dataRow, "UTC");
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sandboxSocketServer.stop();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<Tuple> sanboxData = sandbox.getData(tableId);
        assertThat(sanboxData).isNotEmpty();
        assertThat(sanboxData.size()).isEqualTo(2);
        String[] actualOutputs = sanboxData.stream().map(Object::toString).toArray(String[]::new);
        assertThat(actualOutputs).containsExactly("[2, Bob]", "[1, Alice Updated]");
    }
}
