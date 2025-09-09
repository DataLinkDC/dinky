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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.dinky.sandbox.Sandbox;
import org.dinky.sandbox.SandboxFactory;
import org.dinky.sandbox.metadata.TableId;
import org.dinky.sandbox.metadata.TableType;
import org.dinky.sandbox.metadata.Tuple;
import org.dinky.sandbox.socket.SandboxSocketServer;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.Column;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SandboxDynamicTableSinkTest {

    @Test
    public void testSql() throws Exception {
        List<Column> columns = Arrays.asList(Column.physical("id", DataTypes.INT()));
        TableId tableId = TableId.of("default_database", "test_table");
        Sandbox sandbox = SandboxFactory.getDefaultSandbox();
        sandbox.registerTable(tableId, TableType.PRIMARY_KEY_TABLE, columns, new int[] {0});

        SandboxSocketServer sandboxSocketServer = new SandboxSocketServer(sandbox, 9999);
        sandboxSocketServer.start();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        env.setParallelism(1);

        String sourceDDL = "CREATE TABLE source_table (" + "id INT"
                + ") WITH ("
                + "'connector' = 'datagen',"
                + "'rows-per-second' = '10',"
                + "'number-of-rows' = '10',"
                + "'fields.id.kind' = 'sequence',"
                + "'fields.id.start' = '1',"
                + "'fields.id.end' = '10'"
                + ")";

        String sinkDDL = "CREATE TABLE socket_sink (" + "id INT"
                + ") WITH ("
                + "'connector' = 'dinky-sandbox',"
                + "'host' = 'localhost',"
                + "'port' = '9999',"
                + "'table' = 'default_database.test_table'"
                + ")";

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);

        String insertSQL = "INSERT INTO socket_sink SELECT * FROM source_table";
        tableEnv.executeSql(insertSQL).await();

        sandboxSocketServer.stop();

        List<Tuple> sanboxData = sandbox.getData(tableId);
        assertNotNull(sanboxData);
        assertEquals(10, sanboxData.size());
        String[] actualSandboxData = sanboxData.stream().map(Object::toString).toArray(String[]::new);
        assertArrayEquals(
                new String[] {"[1]", "[2]", "[3]", "[4]", "[5]", "[6]", "[7]", "[8]", "[9]", "[10]"},
                actualSandboxData);
    }

    @Test
    public void testMaxRowNum() throws Exception {
        List<Column> columns = Arrays.asList(Column.physical("id", DataTypes.INT()));
        TableId tableId = TableId.of("default_database", "test_max_row_table");
        Sandbox sandbox = SandboxFactory.getDefaultSandbox();
        sandbox.registerTable(tableId, TableType.PRIMARY_KEY_TABLE, columns, new int[] {0});

        SandboxSocketServer sandboxSocketServer = new SandboxSocketServer(sandbox, 9998);
        sandboxSocketServer.start();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        env.setParallelism(1);

        String sourceDDL = "CREATE TABLE source_table_max_row (" + "id INT"
                + ") WITH ("
                + "'connector' = 'datagen',"
                + "'rows-per-second' = '10',"
                + "'number-of-rows' = '20',"
                + "'fields.id.kind' = 'sequence',"
                + "'fields.id.start' = '1',"
                + "'fields.id.end' = '20'"
                + ")";

        String sinkDDL = "CREATE TABLE socket_sink_max_row (" + "id INT"
                + ") WITH ("
                + "'connector' = 'dinky-sandbox',"
                + "'host' = 'localhost',"
                + "'port' = '9998',"
                + "'table' = 'default_database.test_max_row_table',"
                + "'max-row-num' = '5',"
                + "'auto-cancel' = 'false'"
                + ")";

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);

        String insertSQL = "INSERT INTO socket_sink_max_row SELECT * FROM source_table_max_row";
        tableEnv.executeSql(insertSQL).await();

        sandboxSocketServer.stop();

        List<Tuple> sandboxData = sandbox.getData(tableId);
        assertNotNull(sandboxData);
        assertEquals(5, sandboxData.size());
        String[] actualSandboxData = sandboxData.stream().map(Object::toString).toArray(String[]::new);
        assertArrayEquals(new String[] {"[1]", "[2]", "[3]", "[4]", "[5]"}, actualSandboxData);
    }

    @Test
    public void testAutoCancel() {
        List<Column> columns = Arrays.asList(Column.physical("id", DataTypes.INT()));
        TableId tableId = TableId.of("default_database", "test_auto_cancel_table");
        Sandbox sandbox = SandboxFactory.getDefaultSandbox();
        sandbox.registerTable(tableId, TableType.PRIMARY_KEY_TABLE, columns, new int[] {0});

        SandboxSocketServer sandboxSocketServer = new SandboxSocketServer(sandbox, 9997);
        sandboxSocketServer.start();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        env.setParallelism(1);

        String sourceDDL = "CREATE TABLE source_table_auto_cancel (" + "id INT"
                + ") WITH ("
                + "'connector' = 'datagen',"
                + "'rows-per-second' = '5',"
                + "'number-of-rows' = '10',"
                + "'fields.id.kind' = 'sequence',"
                + "'fields.id.start' = '1',"
                + "'fields.id.end' = '10'"
                + ")";

        String sinkDDL = "CREATE TABLE socket_sink_auto_cancel (" + "id INT"
                + ") WITH ("
                + "'connector' = 'dinky-sandbox',"
                + "'host' = 'localhost',"
                + "'port' = '9997',"
                + "'table' = 'default_database.test_auto_cancel_table',"
                + "'max-row-num' = '3',"
                + "'auto-cancel' = 'true'"
                + ")";

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);

        String insertSQL = "INSERT INTO socket_sink_auto_cancel SELECT * FROM source_table_auto_cancel";
        try {
            tableEnv.executeSql(insertSQL).await();
        } catch (Exception e) {
            // Ignore the exception because the task should be automatically cancelled.
        } finally {
            sandboxSocketServer.stop();

            List<Tuple> sandboxData = sandbox.getData(tableId);
            assertNotNull(sandboxData);
            assertEquals(3, sandboxData.size());
            String[] actualSandboxData =
                    sandboxData.stream().map(Object::toString).toArray(String[]::new);
            assertArrayEquals(new String[] {"[1]", "[2]", "[3]"}, actualSandboxData);
        }
    }

    @Test
    public void testUseChangeLog() throws Exception {
        TableId tableId = TableId.of("default_database", "test_change_log_table");
        Sandbox sandbox = SandboxFactory.getDefaultSandbox();

        // Note: Do not register the table in advance. Let the Sink automatically create a table of the CHANGE_LOG type.
        SandboxSocketServer sandboxSocketServer = new SandboxSocketServer(sandbox, 9996);
        sandboxSocketServer.start();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        env.setParallelism(1);

        String sourceDDL = "CREATE TABLE source_table_change_log (" + "id INT"
                + ") WITH ("
                + "'connector' = 'datagen',"
                + "'rows-per-second' = '10',"
                + "'number-of-rows' = '5',"
                + "'fields.id.kind' = 'sequence',"
                + "'fields.id.start' = '1',"
                + "'fields.id.end' = '5'"
                + ")";

        String sinkDDL = "CREATE TABLE socket_sink_change_log (" + "id INT"
                + ") WITH ("
                + "'connector' = 'dinky-sandbox',"
                + "'host' = 'localhost',"
                + "'port' = '9996',"
                + "'table' = 'default_database.test_change_log_table',"
                + "'use-change-log' = 'true'"
                + ")";

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);

        String insertSQL = "INSERT INTO socket_sink_change_log SELECT * FROM source_table_change_log";
        tableEnv.executeSql(insertSQL).await();

        sandboxSocketServer.stop();

        List<Tuple> sandboxData = sandbox.getData(tableId);
        assertNotNull(sandboxData);
        assertEquals(5, sandboxData.size());
        String[] actualSandboxData = sandboxData.stream().map(Object::toString).toArray(String[]::new);
        assertArrayEquals(new String[] {"[+I, 1]", "[+I, 2]", "[+I, 3]", "[+I, 4]", "[+I, 5]"}, actualSandboxData);

        assertNotNull(sandbox.getTableInfo(tableId));
        assertEquals(TableType.CHANGE_LOG, sandbox.getTableInfo(tableId).getTableType());
    }

    @Test
    public void testCombinedParameters() throws Exception {
        TableId tableId = TableId.of("default_database", "test_combined_table");
        Sandbox sandbox = SandboxFactory.getDefaultSandbox();

        SandboxSocketServer sandboxSocketServer = new SandboxSocketServer(sandbox, 9995);
        sandboxSocketServer.start();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        env.setParallelism(1);

        String sourceDDL = "CREATE TABLE source_table_combined (" + "id INT"
                + ") WITH ("
                + "'connector' = 'datagen',"
                + "'rows-per-second' = '10',"
                + "'number-of-rows' = '15',"
                + "'fields.id.kind' = 'sequence',"
                + "'fields.id.start' = '1',"
                + "'fields.id.end' = '15'"
                + ")";

        String sinkDDL = "CREATE TABLE socket_sink_combined (" + "id INT"
                + ") WITH ("
                + "'connector' = 'dinky-sandbox',"
                + "'host' = 'localhost',"
                + "'port' = '9995',"
                + "'table' = 'default_database.test_combined_table',"
                + "'max-row-num' = '7',"
                + "'auto-cancel' = 'false',"
                + "'use-change-log' = 'true'"
                + ")";

        tableEnv.executeSql(sourceDDL);
        tableEnv.executeSql(sinkDDL);

        String insertSQL = "INSERT INTO socket_sink_combined SELECT * FROM source_table_combined";
        tableEnv.executeSql(insertSQL).await();

        sandboxSocketServer.stop();

        List<Tuple> sandboxData = sandbox.getData(tableId);
        assertNotNull(sandboxData);
        assertEquals(7, sandboxData.size());
        String[] actualSandboxData = sandboxData.stream().map(Object::toString).toArray(String[]::new);
        assertArrayEquals(
                new String[] {"[+I, 1]", "[+I, 2]", "[+I, 3]", "[+I, 4]", "[+I, 5]", "[+I, 6]", "[+I, 7]"},
                actualSandboxData);
    }
}
