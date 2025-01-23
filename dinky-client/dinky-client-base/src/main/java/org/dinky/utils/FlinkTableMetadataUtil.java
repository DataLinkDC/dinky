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

package org.dinky.utils;

import org.dinky.data.enums.ColumnType;
import org.dinky.data.model.Catalog;
import org.dinky.data.model.Column;
import org.dinky.data.model.Schema;
import org.dinky.data.model.Table;
import org.dinky.executor.CustomTableEnvironment;

import org.apache.flink.table.catalog.CatalogBaseTable;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.catalog.ObjectPath;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.logical.VarCharType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlinkTableMetadataUtil {

    public static List<Catalog> getCatalog(CustomTableEnvironment customTableEnvironment) {
        List<Catalog> catalogs = new ArrayList<>();
        for (String catalogName : customTableEnvironment.listCatalogs()) {
            Catalog catalog = Catalog.build(catalogName);
            List<Schema> schemas = new ArrayList<>();
            customTableEnvironment.useCatalog(catalogName);
            for (String database : customTableEnvironment.listDatabases()) {
                Schema schema = Schema.build(database);
                schemas.add(schema);
            }
            catalog.setSchemas(schemas);
            catalogs.add(catalog);
        }
        return catalogs;
    }

    public static void setSchemaInfo(
            CustomTableEnvironment customTableEnvironment,
            String catalogName,
            String database,
            Schema schema,
            List<Table> tables) {
        customTableEnvironment.useCatalog(catalogName);
        customTableEnvironment.useDatabase(database);
        for (String tableName : customTableEnvironment.getCatalogManager().listTables(catalogName, database)) {
            Table table = Table.build(tableName, catalogName);
            customTableEnvironment.getCatalogManager().getCatalog(catalogName).ifPresent(t -> {
                try {
                    CatalogBaseTable baseTable = t.getTable(new ObjectPath(database, tableName));
                    table.setComment(baseTable.getComment());
                    table.setOptions(baseTable.getOptions().toString());
                } catch (Exception e) {
                    // nothing to do
                }
            });
            tables.add(table);
        }
        schema.setTables(tables);

        // show views
        schema.setViews(Arrays.asList(customTableEnvironment.listViews()));
        // show functions
        schema.setFunctions(Arrays.asList(customTableEnvironment.listFunctions()));
        // show user functions
        schema.setUserFunctions(Arrays.asList(customTableEnvironment.listUserDefinedFunctions()));
        // show modules
        schema.setModules(Arrays.asList(customTableEnvironment.listModules()));
    }

    public static List<Column> getColumnList(
            CustomTableEnvironment customTableEnvironment, String catalogName, String database, String tableName) {
        List<Column> columns = new ArrayList<>();
        customTableEnvironment
                .getCatalogManager()
                .getTable(ObjectIdentifier.of(catalogName, database, tableName))
                .ifPresent(t -> {
                    for (int i = 0; i < t.getResolvedSchema().getColumns().size(); i++) {
                        org.apache.flink.table.catalog.Column flinkColumn =
                                t.getResolvedSchema().getColumns().get(i);
                        AtomicBoolean isPrimaryKey = new AtomicBoolean(false);
                        t.getResolvedSchema().getPrimaryKey().ifPresent(k -> {
                            isPrimaryKey.set(k.getColumns().contains(flinkColumn.getName()));
                        });
                        LogicalType logicalType = flinkColumn.getDataType().getLogicalType();
                        Column column = Column.builder()
                                .name(flinkColumn.getName())
                                .type(logicalType.getTypeRoot().name())
                                .keyFlag(isPrimaryKey.get())
                                .isNullable(logicalType.isNullable())
                                .comment(flinkColumn.getComment().orElse(""))
                                .position(i)
                                .build();
                        if (logicalType instanceof VarCharType) {
                            column.setLength(((VarCharType) logicalType).getLength());
                        } else if (logicalType instanceof TimestampType) {
                            column.setLength(((TimestampType) logicalType).getPrecision());
                        } else if (logicalType instanceof DecimalType) {
                            column.setLength(((DecimalType) logicalType).getPrecision());
                            column.setScale(((DecimalType) logicalType).getScale());
                        }

                        String dataTypeName =
                                flinkColumn.getDataType().getConversionClass().getName();
                        ColumnType columnType = ColumnType.getByJavaType(dataTypeName);
                        column.setJavaType(columnType);
                        columns.add(column);
                    }
                });
        return columns;
    }

    public static boolean dropTable(
            CustomTableEnvironment customTableEnvironment, String catalogName, String database, String tableName) {
        Optional<org.apache.flink.table.catalog.Catalog> catalogOptional =
                customTableEnvironment.getCatalogManager().getCatalog(catalogName);
        if (catalogOptional.isPresent()) {
            try {
                catalogOptional.get().dropTable(new ObjectPath(database, tableName), true);
                return true;
            } catch (TableNotExistException e) {
                return false;
            }
        }
        return false;
    }
}
