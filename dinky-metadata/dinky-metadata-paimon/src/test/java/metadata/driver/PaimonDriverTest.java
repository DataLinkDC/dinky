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

package metadata.driver;

import static org.apache.paimon.disk.IOManagerImpl.splitPaths;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.dinky.data.model.QueryData;
import org.dinky.data.model.Table;
import org.dinky.metadata.config.PaimonConfig;
import org.dinky.metadata.driver.PaimonDriver;
import org.dinky.metadata.result.JdbcSelectResult;

import org.apache.commons.lang3.RandomUtils;
import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.catalog.Identifier;
import org.apache.paimon.data.BinaryRow;
import org.apache.paimon.data.BinaryRowWriter;
import org.apache.paimon.data.BinaryString;
import org.apache.paimon.disk.IOManager;
import org.apache.paimon.schema.Schema;
import org.apache.paimon.table.FileStoreTable;
import org.apache.paimon.table.sink.BatchTableCommit;
import org.apache.paimon.table.sink.BatchWriteBuilder;
import org.apache.paimon.table.sink.CommitMessage;
import org.apache.paimon.table.sink.StreamTableWrite;
import org.apache.paimon.table.sink.TableWrite;
import org.apache.paimon.types.DataTypes;
import org.apache.paimon.types.RowKind;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaimonDriverTest {

    private PaimonDriver paimonDriver;

    private final String databaseName = "test";
    private final String tableName = "test";
    private final String warehouse = "/tmp/paimon";
    private final String tmpDir = "/tmp/paimon";
    private final Identifier identifier = Identifier.create(databaseName, tableName);

    @BeforeEach
    public void setUp() throws Exception {
        paimonDriver = new PaimonDriver();
        PaimonConfig config = new PaimonConfig();
        config.setWarehouse(warehouse);
        config.setFileSystemType("local");
        paimonDriver.buildDriverConfig("paimon", "paimon", config);
        paimonDriver.test();
        paimonDriver.createSchema(databaseName);
        Catalog catalog = paimonDriver.getCatalog();
        Schema.Builder schemaBuilder = Schema.newBuilder();
        schemaBuilder.column("id", DataTypes.INT(), "主键");
        schemaBuilder.column("name", DataTypes.STRING(), "姓名");
        schemaBuilder.column("gender", DataTypes.STRING(), "性别");
        schemaBuilder.column("age", DataTypes.INT(), "年龄");
        schemaBuilder.primaryKey("id");
        schemaBuilder.comment("测试表");
        catalog.createTable(identifier, schemaBuilder.build(), true);

        FileStoreTable table = (FileStoreTable) catalog.getTable(identifier);
        BatchWriteBuilder batchWriteBuilder = table.newBatchWriteBuilder();
        try (TableWrite tableWrite = batchWriteBuilder.newWrite().withIOManager(IOManager.create(splitPaths(tmpDir)))) {
            for (int i = 0; i < 10; i++) {
                BinaryRow row = new BinaryRow(table.rowType().getFieldCount());
                BinaryRowWriter writer = new BinaryRowWriter(row);
                writer.writeInt(0, i);
                writer.writeString(1, BinaryString.fromString("jack" + i));
                writer.writeString(2, BinaryString.fromString(i % 2 == 0 ? "M" : "F"));
                writer.writeInt(3, RandomUtils.nextInt(i + 1, i + 30));
                row.setRowKind(RowKind.INSERT);
                tableWrite.write(row, 0);
            }
            List<CommitMessage> commitMessages = ((StreamTableWrite) tableWrite).prepareCommit(true, 1);
            try (BatchTableCommit batchTableCommit = batchWriteBuilder.newCommit()) {
                batchTableCommit.commit(commitMessages);
            }
        }
    }

    @Test
    public void query() {
        QueryData queryData = new QueryData();
        queryData.setSchemaName(databaseName);
        queryData.setTableName(tableName);
        QueryData.Option option = new QueryData.Option();
        option.setLimitEnd(10);
        option.setWhere("id > 5 and id <= 8 and gender = 'M'");
        queryData.setOption(option);
        JdbcSelectResult query = paimonDriver.query(queryData);
        assertEquals(2, query.getRowData().size());
    }

    @AfterEach
    public void tearDown() throws Exception {
        paimonDriver.dropTable(Table.build(tableName, databaseName));
        paimonDriver.getCatalog().dropDatabase(databaseName, true, true);
        paimonDriver.close();
    }
}
