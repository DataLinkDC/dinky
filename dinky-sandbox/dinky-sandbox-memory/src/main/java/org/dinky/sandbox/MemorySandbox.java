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

import org.dinky.assertion.Asserts;
import org.dinky.data.exception.DinkyException;
import org.dinky.sandbox.metadata.ColumnInfo;
import org.dinky.sandbox.metadata.TableId;
import org.dinky.sandbox.metadata.TableInfo;
import org.dinky.sandbox.metadata.TableType;
import org.dinky.sandbox.metadata.Tuple;

import org.apache.flink.table.catalog.Column;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MemorySandbox extends AbstractSandbox implements Sandbox {

    private static final Map<String, TableInfo> TABLE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, List<Tuple>> DATA_CACHE = new ConcurrentHashMap<>();

    private static final DateTimeFormatter FORMATTER_CACHE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getType() {
        return "memory";
    }

    public TableInfo registerTable(
            TableId tableId, TableType tableType, List<Column> columns, int[] primaryKeyIndexes) {
        String identifier = tableId.identifier();
        if (!TABLE_CACHE.containsKey(identifier)) {
            TABLE_CACHE.put(identifier, TableInfo.of(tableId, tableType));
        }
        if (!DATA_CACHE.containsKey(identifier)) {
            DATA_CACHE.put(identifier, new CopyOnWriteArrayList<>());
        }
        TableInfo tableInfo = TABLE_CACHE.get(identifier);
        if (TableType.CHANGE_LOG.equals(tableType)) {
            tableInfo.getColumns().add(ColumnInfo.withString("__op__"));
        }
        for (int i = 0; i < columns.size(); i++) {
            final int finalI = i;
            boolean isPrimaryKey = Arrays.stream(primaryKeyIndexes).anyMatch(x -> x == finalI);
            tableInfo.getColumns().add(ColumnInfo.buildByFlinkColumn(columns.get(i), isPrimaryKey));
        }

        return tableInfo;
    }

    @Override
    public TableInfo registerTable(TableId tableId, TableType tableType, List<ColumnInfo> columns) {
        String identifier = tableId.identifier();
        if (!TABLE_CACHE.containsKey(identifier)) {
            if (TableType.CHANGE_LOG.equals(tableType)) {
                columns.add(0, ColumnInfo.withString("__op__"));
            }
            TABLE_CACHE.put(identifier, TableInfo.of(tableId, tableType, columns));
        }
        if (!DATA_CACHE.containsKey(identifier)) {
            DATA_CACHE.put(identifier, new CopyOnWriteArrayList<>());
        }
        return TABLE_CACHE.get(identifier);
    }

    public void dropTable(TableId tableId) {
        String identifier = tableId.identifier();
        TABLE_CACHE.remove(identifier);
        DATA_CACHE.remove(identifier);
    }

    public boolean existTable(TableId tableId) {
        return TABLE_CACHE.containsKey(tableId.identifier());
    }

    public TableInfo getTableInfo(TableId tableId) {
        String identifier = tableId.identifier();
        if (!TABLE_CACHE.containsKey(identifier)) {
            return null;
        }
        return TABLE_CACHE.get(identifier);
    }

    @Override
    public List<TableInfo> getAllTables() {
        return (new ArrayList<>(TABLE_CACHE.values()));
    }

    @Override
    public List<TableInfo> getAllTables(String boxName) {
        if (Asserts.isNotNullString(boxName)) {
            return TABLE_CACHE.values().stream()
                    .filter(tableInfo -> tableInfo.getTableId().getBoxName().equals(boxName))
                    .collect(Collectors.toList());
        }
        return (new ArrayList<>(TABLE_CACHE.values()));
    }

    public List<Tuple> getData(TableId tableId) {
        String identifier = tableId.identifier();
        if (DATA_CACHE.containsKey(identifier)) {
            return DATA_CACHE.get(identifier);
        }
        return new ArrayList<>();
    }

    @Override
    public void writeRowData(TableId tableId, Row row, String timeZone) {
        String identifier = tableId.identifier();
        if (!TABLE_CACHE.containsKey(identifier)) {
            throw new DinkyException("Table not found: " + identifier);
        }
        if (!DATA_CACHE.containsKey(identifier)) {
            DATA_CACHE.put(identifier, new CopyOnWriteArrayList<>());
        }
        TableInfo tableInfo = TABLE_CACHE.get(identifier);
        Tuple tuple = getStreamTuple(row, timeZone, tableInfo.getTableType().isAppendRowKind());
        if (tableInfo.getTableType().isAppendOnly()) {
            DATA_CACHE.get(identifier).add(tuple);
        } else {
            if (RowKind.UPDATE_BEFORE.equals(row.getKind()) || RowKind.DELETE.equals(row.getKind())) {
                DATA_CACHE.get(identifier).remove(tuple);
            } else {
                DATA_CACHE.get(identifier).add(tuple);
            }
        }
    }

    @Override
    public void appendData(TableId tableId, List<Tuple> tuples) {
        String identifier = tableId.identifier();
        if (!TABLE_CACHE.containsKey(identifier)) {
            throw new DinkyException("Table not found: " + identifier);
        }
        if (!DATA_CACHE.containsKey(identifier)) {
            DATA_CACHE.put(identifier, new CopyOnWriteArrayList<>());
        }
        DATA_CACHE.get(identifier).addAll(tuples);
    }

    public void handleFinished(String name, Consumer<String> consumer) {
        consumer.accept(name);
        dropTable(TableId.withPrivate(name));
    }

    @Override
    public void close() throws Exception {
        TABLE_CACHE.clear();
        DATA_CACHE.clear();
    }

    // TODO: Convert the data format according to the column type.
    private Tuple getStreamTuple(Row row, String timeZone, boolean isAppendRowKind) {
        List<Object> data = new ArrayList<>();
        if (isAppendRowKind) {
            data.add(row.getKind().shortString());
        }
        for (int i = 0; i < row.getArity(); ++i) {
            Object field = row.getField(i);
            if (field == null) {
                data.add(null);
            } else if (field instanceof Instant) {
                data.add(((Instant) field)
                        .atZone(ZoneId.of(timeZone))
                        .toLocalDateTime()
                        .format(FORMATTER_CACHE));
            } else if (field instanceof Boolean) {
                data.add(field.toString());
            } else if (field instanceof LocalDateTime) {
                data.add(((LocalDateTime) field).format(FORMATTER_CACHE));
            } else {
                data.add(field);
            }
        }
        return new Tuple(data.toArray());
    }
}
