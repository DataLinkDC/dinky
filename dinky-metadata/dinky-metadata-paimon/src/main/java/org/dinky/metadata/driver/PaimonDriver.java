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

package org.dinky.metadata.driver;

import org.dinky.data.constant.CommonConstant;
import org.dinky.data.enums.ColumnType;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.Column;
import org.dinky.data.model.QueryData;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.metadata.config.PaimonConfig;
import org.dinky.metadata.convert.ITypeConvert;
import org.dinky.metadata.convert.PaimonTypeConvert;
import org.dinky.metadata.enums.DriverType;
import org.dinky.metadata.query.IDBQuery;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.utils.JsonUtils;

import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.catalog.CatalogContext;
import org.apache.paimon.catalog.CatalogFactory;
import org.apache.paimon.catalog.Identifier;
import org.apache.paimon.data.InternalRow;
import org.apache.paimon.options.Options;
import org.apache.paimon.reader.RecordReader;
import org.apache.paimon.table.source.ReadBuilder;
import org.apache.paimon.table.source.Split;
import org.apache.paimon.table.source.TableRead;
import org.apache.paimon.types.DataField;
import org.apache.paimon.types.RowType;
import org.apache.paimon.utils.CloseableIterator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

/**
 * MysqlDriver
 *
 * @since 2021/7/20 14:06
 */
@Slf4j
public class PaimonDriver extends AbstractDriver<PaimonConfig> {

    public Catalog catalog;
    private Options catalogOptions;

    @Override
    public JdbcSelectResult query(QueryData queryData) {
        queryData.setTableName(queryData.getTableName());
        JdbcSelectResult result = JdbcSelectResult.buildResult();
        Identifier identifier = Identifier.create(queryData.getSchemaName(), queryData.getTableName());
        try {
            org.apache.paimon.table.Table table = catalog.getTable(identifier);
            List<DataField> fieldTypes = table.rowType().getFields();
            List<String> columNames = fieldTypes.stream().map(DataField::name).collect(Collectors.toList());
            result.setColumns(columNames);

            ReadBuilder readBuilder = table.newReadBuilder();
            QueryData.Option option = queryData.getOption();
            // Paimon无法做到分页，所以这里逻辑只取前N条
            int length = option.getLimitEnd() - option.getLimitStart();
            readBuilder.withLimit(length);
            List<Split> splits = readBuilder.newScan().plan().splits();

            // 4. Read a split in task
            TableRead read = readBuilder.newRead();
            List<LinkedHashMap<String, Object>> datas;
            try (RecordReader<InternalRow> reader = read.createReader(splits)) {
                datas = new ArrayList<>();

                try (CloseableIterator<InternalRow> iterator = reader.toCloseableIterator()) {
                    while (iterator.hasNext()) {
                        InternalRow row = iterator.next();
                        LinkedHashMap<String, Object> rowList = new LinkedHashMap<>();
                        for (int i = 0; i < row.getFieldCount(); i++) {
                            String name = fieldTypes.get(i).name();
                            Object data = PaimonTypeConvert.SafeGetRowData(fieldTypes.get(i), row, i);
                            rowList.put(name, data);
                        }
                        datas.add(rowList);
                        if (datas.size() >= length) {
                            log.warn("query data exceed limit: {}", length);
                            break;
                        }
                    }
                }
            }
            result.setRowData(datas);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error("query data error: ", e);
            throw new BusException(e.getMessage());
        }
        return result;
    }

    @Override
    public IDBQuery getDBQuery() {
        return null;
    }

    @Override
    public ITypeConvert<PaimonConfig> getTypeConvert() {
        return null;
    }

    @Override
    public <T> Driver buildDriverConfig(String name, String type, T config) {
        PaimonConfig paimonConfig = JsonUtils.convertValue(config, PaimonConfig.class);
        this.catalogOptions = paimonConfig.getOptions();
        return this;
    }

    @Override
    public String getType() {
        return DriverType.Paimon.getValue();
    }

    @Override
    public String getName() {
        return "Paimon数据库";
    }

    @Override
    public String test() {
        connect();
        List<String> databases = catalog.listDatabases();
        log.info("connect paimon success ,databases: {}", databases);
        return CommonConstant.HEALTHY;
    }

    @Override
    public Driver connect() {
        this.catalog = CatalogFactory.createCatalog(CatalogContext.create(catalogOptions));
        return this;
    }

    @Override
    public void close() {
        try {
            catalog.close();
        } catch (Exception e) {
            log.error("close paimon error: ", e);
        }
    }

    @Override
    public List<Schema> listSchemas() {
        return catalog.listDatabases().stream().map(Schema::new).collect(Collectors.toList());
    }

    @Override
    public boolean existSchema(String schemaName) {
        return false;
    }

    @Override
    public boolean createSchema(String schemaName) throws Exception {
        return false;
    }

    @Override
    public String generateCreateSchemaSql(String schemaName) {
        return null;
    }

    @Override
    public List<Table> listTables(String schemaName) {
        try {
            return catalog.listTables(schemaName).stream().map(Table::new).collect(Collectors.toList());
        } catch (Catalog.DatabaseNotExistException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Table> listTables(String schemaName, String tableName) {
        try {
            return catalog.listTables(schemaName).stream()
                    .filter(t -> t.equalsIgnoreCase(tableName))
                    .map(Table::new)
                    .collect(Collectors.toList());
        } catch (Catalog.DatabaseNotExistException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Table getTable(String schemaName, String tableName) {
        Identifier identifier = Identifier.create(schemaName, tableName);
        Table.TableBuilder tableBuilder = Table.builder();
        try {
            org.apache.paimon.table.Table catalogTable = catalog.getTable(identifier);
            RowType rowType = catalogTable.rowType();
            List<String> primaryKeys = catalogTable.primaryKeys();
            List<String> partitionKeys = catalogTable.partitionKeys();
            List<Column> columns = new ArrayList<>();
            for (DataField field : rowType.getFields()) {
                Column column = new Column();
                column.setName(field.name());
                column.setType(field.type().toString());
                column.setComment(field.description());
                // TODO: 使用CovertType
                column.setJavaType(ColumnType.STRING);
                column.setKeyFlag(primaryKeys.contains(field.name()));
                //                column.setPartaionKey(partitionKeys.contains(field.name()));
                column.setNullable(field.type().isNullable());
                columns.add(column);
            }
            tableBuilder
                    .columns(columns)
                    .name(tableName)
                    .schema(schemaName)
                    .comment(catalogTable.comment().toString())
                    .options(JsonUtils.toJsonString(catalogTable.options()))
                    .driverType(getType());
        } catch (Catalog.TableNotExistException e) {
            throw new RuntimeException(e);
        }
        return tableBuilder.build();
    }

    @Override
    public List<Column> listColumns(String schemaName, String tableName) {

        return null;
    }

    @Override
    public List<Column> listColumnsSortByPK(String schemaName, String tableName) {
        return null;
    }

    @Override
    public boolean createTable(Table table) throws Exception {
        return false;
    }

    @Override
    public boolean generateCreateTable(Table table) throws Exception {
        return false;
    }

    @Override
    public boolean dropTable(Table table) throws Exception {
        return false;
    }

    @Override
    public boolean truncateTable(Table table) throws Exception {
        return false;
    }

    @Override
    public String getCreateTableSql(Table table) {
        return null;
    }

    @Override
    public String getDropTableSql(Table table) {
        return null;
    }

    @Override
    public String getTruncateTableSql(Table table) {
        return null;
    }

    @Override
    public String generateCreateTableSql(Table table) {
        return null;
    }

    @Override
    public boolean execute(String sql) throws Exception {
        return false;
    }

    @Override
    public int executeUpdate(String sql) throws Exception {
        return 0;
    }

    @Override
    public JdbcSelectResult query(String sql, Integer limit) {
        return null;
    }

    @Override
    public StringBuilder genQueryOption(QueryData queryData) {
        return null;
    }

    @Override
    public JdbcSelectResult executeSql(String sql, Integer limit) {
        return null;
    }

    @Override
    public List<SqlExplainResult> explain(String sql) {
        return null;
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return null;
    }

    @Override
    public Stream<JdbcSelectResult> StreamExecuteSql(String statement, Integer maxRowNum) {
        return null;
    }
}
