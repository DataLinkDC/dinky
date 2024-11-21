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

package org.dinky.trans.ddl;

import org.dinky.assertion.Asserts;
import org.dinky.cdc.CDCBuilder;
import org.dinky.cdc.CDCBuilderFactory;
import org.dinky.cdc.SinkBuilder;
import org.dinky.cdc.SinkBuilderFactory;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.executor.CustomTableResultImpl;
import org.dinky.executor.Executor;
import org.dinky.metadata.driver.Driver;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.Operation;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.SplitUtil;
import org.dinky.utils.SqlUtil;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.ResultKind;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.Column;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.apache.flink.table.types.AtomicDataType;
import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CreateCDCSourceOperation
 *
 * @since 2022/1/29 23:25
 */
public class CreateCDCSourceOperation extends AbstractOperation implements Operation {

    private static final String KEY_WORD = "EXECUTE CDCSOURCE";

    public CreateCDCSourceOperation() {}

    public CreateCDCSourceOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new CreateCDCSourceOperation(statement);
    }

    @Override
    public TableResult execute(Executor executor) {
        final CustomTableResultImpl.Builder tableResultBuilder = CustomTableResultImpl.builder();
        logger.info("Start build CDCSOURCE Task...");
        CDCSource cdcSource = CDCSource.build(statement);
        FlinkCDCConfig config = cdcSource.buildFlinkCDCConfig();
        config.setMockTest(executor.isMockTest());
        try {
            CDCBuilder cdcBuilder = CDCBuilderFactory.buildCDCBuilder(config);
            Map<String, Map<String, String>> allConfigMap = cdcBuilder.parseMetaDataConfigs();
            config.setSchemaFieldName(cdcBuilder.getSchemaFieldName());
            SinkBuilder sinkBuilder = SinkBuilderFactory.buildSinkBuilder(config);
            final List<String> schemaNameList = cdcBuilder.getSchemaList();
            final List<String> tableRegList = cdcBuilder.getTableList();

            final List<Schema> schemaList = new LinkedList<>();
            final List<String> schemaTableNameList = new LinkedList<>();
            // Scenario of dividing databases and tables
            if (SplitUtil.isEnabled(cdcSource.getSplit())) {
                logger.info("Split table or database mode is enabled...");
                Map<String, String> confMap = cdcBuilder.parseMetaDataConfig();
                Driver driver =
                        Driver.buildWithOutPool(confMap.get("name"), confMap.get("type"), JsonUtils.toMap(confMap));

                // This is passed directly to the regularization process
                schemaTableNameList.addAll(tableRegList.stream()
                        .map(x -> x.replaceFirst("\\\\.", "."))
                        .collect(Collectors.toList()));

                Driver sinkDriver = checkAndCreateSinkSchema(config, schemaTableNameList.get(0));

                Set<Table> tables = driver.getSplitTables(tableRegList, cdcSource.getSplit());

                for (Table table : tables) {
                    // Filter out views
                    if (Asserts.isEquals(table.getType(), "VIEW")) {
                        continue;
                    }
                    String schemaName = table.getSchema();
                    Schema schema = Schema.build(schemaName);
                    schema.setTables(Collections.singletonList(table));
                    // The structure of all tables in a database or table is the same, just take out the first table
                    // name from the list
                    String schemaTableName = table.getSchemaTableNameList().get(0);
                    // Real Table Name
                    String realSchemaName = schemaTableName.split("\\.")[0];
                    String tableName = schemaTableName.split("\\.")[1];
                    table.setColumns(driver.listColumnsSortByPK(realSchemaName, tableName));
                    schemaList.add(schema);

                    if (null != sinkDriver) {
                        Table sinkTable = (Table) table.clone();
                        sinkTable.setSchema(sinkBuilder.getSinkSchemaName(table));
                        sinkTable.setName(sinkBuilder.getSinkTableName(table));
                        checkAndCreateSinkTable(sinkDriver, sinkTable);
                    }
                }
            } else {
                for (String schemaName : schemaNameList) {
                    Schema schema = Schema.build(schemaName);
                    if (!allConfigMap.containsKey(schemaName)) {
                        continue;
                    }

                    Driver sinkDriver = checkAndCreateSinkSchema(config, schemaName);
                    Map<String, String> confMap = allConfigMap.get(schemaName);
                    Driver driver = Driver.build(confMap.get("name"), confMap.get("type"), JsonUtils.toMap(confMap));

                    final List<Table> tables = driver.listTables(schemaName);
                    for (Table table : tables) {
                        if (!Asserts.isEquals(table.getType(), "VIEW")) {
                            if (Asserts.isNotNullCollection(tableRegList)) {
                                for (String tableReg : tableRegList) {
                                    if (table.getSchemaTableName().matches(tableReg.trim())
                                            && !schema.getTables().contains(Table.build(table.getName()))) {
                                        table.setColumns(driver.listColumnsSortByPK(schemaName, table.getName()));
                                        schema.getTables().add(table);
                                        schemaTableNameList.add(table.getSchemaTableName());
                                        break;
                                    }
                                }
                            } else {
                                table.setColumns(driver.listColumnsSortByPK(schemaName, table.getName()));
                                schemaTableNameList.add(table.getSchemaTableName());
                                schema.getTables().add(table);
                            }
                        }
                    }

                    if (null != sinkDriver) {
                        for (Table table : schema.getTables()) {
                            Table sinkTable = (Table) table.clone();
                            sinkTable.setSchema(sinkBuilder.getSinkSchemaName(table));
                            sinkTable.setName(sinkBuilder.getSinkTableName(table));
                            checkAndCreateSinkTable(sinkDriver, sinkTable);
                        }
                    }
                    schemaList.add(schema);
                }
            }

            logger.info("A total of {} tables were detected...", schemaTableNameList.size());
            for (int i = 0; i < schemaTableNameList.size(); i++) {
                logger.info("{}: {}", i + 1, schemaTableNameList.get(i));
            }
            config.setSchemaTableNameList(schemaTableNameList);
            config.setSchemaList(schemaList.stream()
                    .sorted(Comparator.comparing(Schema::getName))
                    .collect(Collectors.toList()));
            StreamExecutionEnvironment streamExecutionEnvironment = executor.getStreamExecutionEnvironment();
            if (Asserts.isNotNull(config.getParallelism())) {
                streamExecutionEnvironment.setParallelism(config.getParallelism());
                logger.info("Set parallelism: {}", config.getParallelism());
            }
            if (Asserts.isNotNull(config.getCheckpoint())) {
                streamExecutionEnvironment.enableCheckpointing(config.getCheckpoint());
                logger.info("Set checkpoint: {}", config.getCheckpoint());
            }
            DataStreamSource<String> streamSource = cdcBuilder.build(streamExecutionEnvironment);
            logger.info("Build {} successful...", config.getType());
            sinkBuilder.build(streamExecutionEnvironment, executor.getCustomTableEnvironment(), streamSource);
            logger.info("Build CDCSOURCE Task successful!");
            final List<Column> columns = new ArrayList<>();
            final List<Row> rowList = new ArrayList<>();
            for (Schema schema : config.getSchemaList()) {
                for (Table table : schema.getTables()) {
                    columns.add(Column.physical(
                            "default_catalog.default_database." + sinkBuilder.getSinkTableName(table),
                            new AtomicDataType(new BigIntType())));
                    rowList.add(Row.of(-1));
                }
            }
            tableResultBuilder.schema(ResolvedSchema.of(columns)).data(rowList).resultKind(ResultKind.SUCCESS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return tableResultBuilder.build();
    }

    private Driver checkAndCreateSinkSchema(FlinkCDCConfig config, String schemaName) throws Exception {
        Map<String, String> sink = config.getSink();
        String autoCreate = sink.get(FlinkCDCConfig.AUTO_CREATE);
        if (!Asserts.isEqualsIgnoreCase(autoCreate, "true") || Asserts.isNullString(schemaName)) {
            return null;
        }
        String url = sink.get("url");
        String schema = SqlUtil.replaceAllParam(sink.get(FlinkCDCConfig.SINK_DB), "schemaName", schemaName);
        Driver driver = Driver.build(sink.get("connector"), url, sink.get("username"), sink.get("password"));
        if (null != driver && !driver.existSchema(schema)) {
            driver.createSchema(schema);
        }
        sink.put(FlinkCDCConfig.SINK_DB, schema);
        // todo: There is a bug that can cause the problem of URL duplicate concatenation of schema, for example: jdbc:
        // mysql://localhost:3306/test?useSSL=false/test -1
        if (!url.contains(schema)) {
            sink.put("url", url + "/" + schema);
        }
        return driver;
    }

    void checkAndCreateSinkTable(Driver driver, Table table) throws Exception {
        if (null != driver && !driver.existTable(table)) {
            driver.generateCreateTable(table);
        }
    }
}
