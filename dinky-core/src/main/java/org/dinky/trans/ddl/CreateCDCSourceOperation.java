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
            config.setSchemaFieldName(cdcBuilder.getSchemaFieldName());
            SinkBuilder sinkBuilder = SinkBuilderFactory.buildSinkBuilder(config);
            final List<String> tableRegList = cdcBuilder.getTableList();

            final List<Schema> schemaList = new LinkedList<>();
            final List<String> schemaTableNameList = new LinkedList<>();
            // Scenario of dividing databases and tables
            if (SplitUtil.isEnabled(cdcSource.getSplit())) {
                logger.info("Split table or database mode is enabled...");
                Map<String, String> confMap = cdcBuilder.generateMetaDataConfig("");
                Driver driver =
                        Driver.buildWithOutPool(confMap.get("name"), confMap.get("type"), JsonUtils.toMap(confMap));

                // This is passed directly to the regularization process
                schemaTableNameList.addAll(tableRegList.stream()
                        .map(x -> x.replaceFirst("\\\\.", "."))
                        .collect(Collectors.toList()));

                // target tables (merged tables)
                Set<Table> tables = driver.getSplitTables(tableRegList, cdcSource.getSplit());

                for (Table table : tables) {
                    // Filter out views
                    if (Asserts.isEquals(table.getType(), "VIEW")) {
                        continue;
                    }
                    String schemaName = table.getSchema();
                    // The structure of all tables in a database or table is the same, just take out the first table
                    // name from the list
                    String schemaTableName = table.getSchemaTableNameList().get(0);
                    // Real Table Name
                    String realSchemaName = schemaTableName.split("\\.")[0];
                    String tableName = schemaTableName.split("\\.")[1];
                    table.setColumns(driver.listColumnsSortByPK(realSchemaName, tableName));
                    boolean isExist = false;
                    for (Schema schemaItem : schemaList) {
                        if (schemaItem.getName().equals(schemaName)) {
                            schemaItem.getTables().add(table);
                            isExist = true;
                            break;
                        }
                    }
                    if (!isExist) {
                        Schema schema = Schema.build(schemaName);
                        schema.setTables(Collections.singletonList(table));
                        schemaList.add(schema);
                    }

                    // anto create schema and table.
                    if (!config.isAutoCreateSchemaAndTables()) {
                        continue;
                    }
                    checkAndCreateSinkSchema(config, schemaName);
                    Driver sinkDriver = buildSinkDriver(config, schemaName);
                    if (null != sinkDriver) {
                        Table sinkTable = (Table) table.clone();
                        sinkTable.setSchema(sinkBuilder.getSinkSchemaName(table));
                        sinkTable.setName(sinkBuilder.getSinkTableName(table));
                        checkAndCreateSinkTable(sinkDriver, sinkTable);
                    }
                }
            } else {
                for (String schemaName : cdcBuilder.getSchemaList()) {
                    if (Asserts.isNullString(schemaName)) {
                        continue;
                    }
                    Schema schema = Schema.build(schemaName);
                    Map<String, String> confMap = cdcBuilder.generateMetaDataConfig(schemaName);
                    Driver sourceDriver =
                            Driver.buildWithOutPool(confMap.get("name"), confMap.get("type"), JsonUtils.toMap(confMap));
                    for (Table table : sourceDriver.listTables(schemaName)) {
                        if ("VIEW".equals(table.getType())) {
                            continue;
                        }
                        if (Asserts.isNotNullCollection(tableRegList)) {
                            for (String tableReg : tableRegList) {
                                if (table.getSchemaTableName().matches(tableReg.trim())
                                        && !schemaTableNameList.contains(table.getSchemaTableName())) {
                                    schemaTableNameList.add(table.getSchemaTableName());
                                    table.setColumns(sourceDriver.listColumnsSortByPK(schemaName, table.getName()));
                                    schema.getTables().add(table);
                                    break;
                                }
                            }
                        } else {
                            schemaTableNameList.add(table.getSchemaTableName());
                            table.setColumns(sourceDriver.listColumnsSortByPK(schemaName, table.getName()));
                            schema.getTables().add(table);
                        }
                    }
                    schemaList.add(schema);

                    // anto create schema and table.
                    if (!config.isAutoCreateSchemaAndTables()) {
                        continue;
                    }
                    checkAndCreateSinkSchema(config, schemaName);
                    Driver sinkDriver = buildSinkDriver(config, schemaName);
                    if (null != sinkDriver) {
                        for (Table table : schema.getTables()) {
                            Table sinkTable = (Table) table.clone();
                            sinkTable.setSchema(sinkBuilder.getSinkSchemaName(table));
                            sinkTable.setName(sinkBuilder.getSinkTableName(table));
                            checkAndCreateSinkTable(sinkDriver, sinkTable);
                        }
                    }
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

    private void checkAndCreateSinkSchema(FlinkCDCConfig config, String schemaName) throws Exception {
        Map<String, String> sinkConfMap = config.getSink();
        String url = sinkConfMap.get("url");
        if (url.contains("#{schemaName}")) {
            url = SqlUtil.replaceAllParam(url, "schemaName", "");
        }
        Driver sinkDriver = Driver.build(
                sinkConfMap.get("connector"), url, sinkConfMap.get("username"), sinkConfMap.get("password"));
        if (null == sinkDriver) {
            return;
        }
        String schema = SqlUtil.replaceAllParam(sinkConfMap.get(FlinkCDCConfig.SINK_DB), "schemaName", schemaName);
        if (!sinkDriver.existSchema(schema)) {
            sinkDriver.createSchema(schema);
        }
        sinkConfMap.put(FlinkCDCConfig.SINK_DB, schema);
    }

    private Driver buildSinkDriver(FlinkCDCConfig config, String schemaName) throws Exception {
        Map<String, String> sinkConfMap = config.getSink();
        String url = sinkConfMap.get("url");
        String schema = SqlUtil.replaceAllParam(sinkConfMap.get(FlinkCDCConfig.SINK_DB), "schemaName", schemaName);
        if (url.contains("#{schemaName}")) {
            url = SqlUtil.replaceAllParam(url, "schemaName", schema);
        }
        return Driver.build(
                sinkConfMap.get("connector"), url, sinkConfMap.get("username"), sinkConfMap.get("password"));
    }

    void checkAndCreateSinkTable(Driver driver, Table table) throws Exception {
        if (null != driver && !driver.existTable(table)) {
            driver.createTable(table);
        }
    }
}
