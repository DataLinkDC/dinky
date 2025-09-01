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
import org.dinky.sandbox.metadata.ColumnInfo;
import org.dinky.sandbox.metadata.TableId;
import org.dinky.sandbox.metadata.TableInfo;
import org.dinky.sandbox.metadata.TableType;
import org.dinky.sandbox.metadata.Tuple;

import org.apache.flink.table.catalog.Column;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.function.Consumer;

public interface Sandbox extends AutoCloseable {

    default boolean canHandle(String type) {
        return Asserts.isEqualsIgnoreCase(getType(), type);
    }

    String getType();

    TableInfo registerTable(TableId tableId, TableType tableType, List<Column> columns, int[] primaryKeyIndexes);

    TableInfo registerTable(TableId tableId, TableType tableType, List<ColumnInfo> columns);

    boolean existTable(TableId tableId);

    TableInfo getTableInfo(TableId tableId);

    List<TableInfo> getAllTables();

    List<TableInfo> getAllTables(String boxName);

    List<Tuple> getData(TableId tableId);

    void writeRowData(TableId tableId, Row row, String timeZone);

    void appendData(TableId tableId, List<Tuple> tuples);

    void handleFinished(String name, Consumer<String> consumer);
}
