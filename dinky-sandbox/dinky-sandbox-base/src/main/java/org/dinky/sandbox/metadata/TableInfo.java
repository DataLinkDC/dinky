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

package org.dinky.sandbox.metadata;

import java.util.ArrayList;
import java.util.List;

public class TableInfo {
    private final TableId tableId;
    private final TableType tableType;
    private final List<ColumnInfo> columns;

    public TableInfo(TableId tableId, TableType tableType, List<ColumnInfo> columns) {
        this.tableId = tableId;
        this.tableType = tableType;
        this.columns = columns;
    }

    public static TableInfo of(TableId tableId, TableType tableType, List<ColumnInfo> columns) {
        return new TableInfo(tableId, tableType, columns);
    }

    public static TableInfo of(TableId tableId, TableType tableType) {
        return new TableInfo(tableId, tableType, new ArrayList<>());
    }

    public static TableInfo of(TableId tableId, List<ColumnInfo> columns) {
        return new TableInfo(tableId, null, columns);
    }

    public TableId getTableId() {
        return tableId;
    }

    public TableType getTableType() {
        return tableType;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }
}
