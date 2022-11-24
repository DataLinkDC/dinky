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

package com.dlink.trans.ddl;

import static com.dlink.cdc.SinkBuilderFactory.buildSinkBuilder;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.CDCBuilderFactory;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.Executor;
import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.trans.AbstractOperation;
import com.dlink.trans.Operation;
import com.dlink.utils.SplitUtil;
import com.dlink.utils.SqlUtil;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CreateCDCSourceOperation
 *
 * @author wenmo
 * @since 2022/1/29 23:25
 */
public class CreateCDCSourceOperation extends AbstractOperation implements Operation {
    private static final String KEY_WORD = "EXECUTE CDCSOURCE";

    public CreateCDCSourceOperation() {
    }

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
    public TableResult build(Executor executor) {
        logger.info("Start build CDCSOURCE Task...");
        CDCSource cdcSource = CDCSource.build(statement);
        FlinkCDCConfig config = new FlinkCDCConfig(cdcSource.getConnector(), cdcSource.getHostname(), cdcSource.getPort(), cdcSource.getUsername()
            , cdcSource.getPassword(), cdcSource.getCheckpoint(), cdcSource.getParallelism(), cdcSource.getDatabase(), cdcSource.getSchema()
            , cdcSource.getTable(), cdcSource.getStartupMode(), cdcSource.getSplit(), cdcSource.getDebezium(), cdcSource.getSource(), cdcSource.getSink(), cdcSource.getJdbc());
        try {
            CDCBuilder cdcBuilder = CDCBuilderFactory.buildCDCBuilder(config);
            Map<String, Map<String, String>> allConfigMap = cdcBuilder.parseMetaDataConfigs();
            config.setSchemaFieldName(cdcBuilder.getSchemaFieldName());
            SinkBuilder sinkBuilder = buildSinkBuilder(config);
            List<Schema> schemaList = new ArrayList<>();
            final List<String> schemaNameList = cdcBuilder.getSchemaList();
            final List<String> tableRegList = cdcBuilder.getTableList();
            final List<String> schemaTableNameList = new ArrayList<>();
            if (SplitUtil.isEnabled(cdcSource.getSplit())) {
                DriverConfig driverConfig = DriverConfig.build(cdcBuilder.parseMetaDataConfig());
                Driver driver = Driver.build(driverConfig);

                // 这直接传正则过去
                schemaTableNameList.addAll(tableRegList.stream().map(x -> x.replaceFirst("\\\\.", ".")).collect(Collectors.toList()));

                Driver sinkDriver = checkAndCreateSinkSchema(config, schemaTableNameList.get(0));

                Set<Table> tables = driver.getSplitTables(tableRegList, cdcSource.getSplit());

                for (Table table : tables) {
                    String schemaName = table.getSchema();
                    Schema schema = Schema.build(schemaName);
                    schema.setTables(Collections.singletonList(table));
                    //分库分表所有表结构都是一样的，取出列表中第一个表名即可
                    String schemaTableName = table.getSchemaTableNameList().get(0);
                    //真实的表名
                    String tableName = schemaTableName.split("\\.")[1];
                    table.setColumns(driver.listColumnsSortByPK(schemaName, tableName));
                    table.setColumns(driver.listColumnsSortByPK(schemaName, table.getName()));
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

                    DriverConfig driverConfig = DriverConfig.build(allConfigMap.get(schemaName));
                    Driver driver = Driver.build(driverConfig);
                    final List<Table> tables = driver.listTables(schemaName);
                    for (Table table : tables) {
                        if (!Asserts.isEquals(table.getType(), "VIEW")) {
                            if (Asserts.isNotNullCollection(tableRegList)) {
                                for (String tableReg : tableRegList) {
                                    if (table.getSchemaTableName().matches(tableReg.trim()) && !schema.getTables().contains(Table.build(table.getName()))) {
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

            logger.info("A total of " + schemaTableNameList.size() + " tables were detected...");
            for (int i = 0; i < schemaTableNameList.size(); i++) {
                logger.info((i + 1) + ": " + schemaTableNameList.get(i));
            }
            config.setSchemaTableNameList(schemaTableNameList);
            config.setSchemaList(schemaList);
            StreamExecutionEnvironment streamExecutionEnvironment = executor.getStreamExecutionEnvironment();
            if (Asserts.isNotNull(config.getParallelism())) {
                streamExecutionEnvironment.setParallelism(config.getParallelism());
                logger.info("Set parallelism: " + config.getParallelism());
            }
            if (Asserts.isNotNull(config.getCheckpoint())) {
                streamExecutionEnvironment.enableCheckpointing(config.getCheckpoint());
                logger.info("Set checkpoint: " + config.getCheckpoint());
            }
            DataStreamSource<String> streamSource = cdcBuilder.build(streamExecutionEnvironment);
            logger.info("Build " + config.getType() + " successful...");
            if (cdcSource.getSinks() == null || cdcSource.getSinks().size() == 0) {
                sinkBuilder.build(cdcBuilder, streamExecutionEnvironment, executor.getCustomTableEnvironment(), streamSource);
            } else {
                for (Map<String, String> sink : cdcSource.getSinks()) {
                    config.setSink(sink);
                    sinkBuilder.build(cdcBuilder, streamExecutionEnvironment, executor.getCustomTableEnvironment(), streamSource);
                }
            }
            logger.info("Build CDCSOURCE Task successful!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    Driver checkAndCreateSinkSchema(FlinkCDCConfig config, String schemaName) throws Exception {
        Map<String, String> sink = config.getSink();
        String autoCreate = sink.get("auto.create");
        if (!Asserts.isEqualsIgnoreCase(autoCreate, "true") || Asserts.isNullString(schemaName)) {
            return null;
        }
        String url = sink.get("url");
        String schema = SqlUtil.replaceAllParam(sink.get("sink.db"), "schemaName", schemaName);
        Driver driver = Driver.build(sink.get("connector"), url, sink.get("username"), sink.get("password"));
        if (null != driver && !driver.existSchema(schema)) {
            driver.createSchema(schema);
        }
        sink.put("sink.db", schema);
        sink.put("url", url + "/" + schema);
        return driver;
    }

    void checkAndCreateSinkTable(Driver driver, Table table) throws Exception {
        if (null != driver && !driver.existTable(table)) {
            driver.generateCreateTable(table);
        }
    }
}
