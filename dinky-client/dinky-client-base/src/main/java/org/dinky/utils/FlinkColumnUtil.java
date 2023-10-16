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
import org.dinky.data.model.Column;
import org.dinky.executor.CustomTableEnvironment;

import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.logical.VarCharType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlinkColumnUtil {
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
                                .comment(flinkColumn.getComment().orElse(""))
                                .keyFlag(isPrimaryKey.get())
                                .isNullable(logicalType.isNullable())
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

                        for (ColumnType columnType : ColumnType.values()) {
                            if (columnType
                                    .getJavaType()
                                    .equals(flinkColumn
                                            .getDataType()
                                            .getConversionClass()
                                            .getName())) {
                                column.setJavaType(columnType);
                                break;
                            }
                        }
                        //                            FlinkColumn flinkColumn = FlinkColumn.build(i,
                        // column.getName(), column.getDataType().getConversionClass().getName(),
                        // isPrimaryKey.get(), column.getDataType().getLogicalType().isNullable(),
                        // column.explainExtras().orElse(""), "", column.getComment().orElse(""));

                        columns.add(column);
                    }
                });
        return columns;
    }
}
