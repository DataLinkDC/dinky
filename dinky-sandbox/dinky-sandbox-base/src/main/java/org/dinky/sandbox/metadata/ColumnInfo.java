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

import org.apache.flink.table.types.logical.RowType;

import java.io.Serializable;

public class ColumnInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    // TODO: Change it to a field type enumeration
    private final String dataType;
    private final String description;
    private final boolean primary;

    public ColumnInfo(String name, String dataType, String description, boolean primary) {
        this.name = name;
        this.dataType = dataType;
        this.description = description;
        this.primary = primary;
    }

    public static ColumnInfo withString(String name) {
        return new ColumnInfo(name, "STRING", "", false);
    }

    public static ColumnInfo of(String name, String dataType) {
        return new ColumnInfo(name, dataType, "", false);
    }

    public static ColumnInfo buildByFlinkColumn(org.apache.flink.table.catalog.Column column, boolean isPrimaryKey) {
        return new ColumnInfo(
                column.getName(),
                column.getDataType().getLogicalType().toString(),
                column.getComment().orElse(""),
                isPrimaryKey);
    }

    public static ColumnInfo buildByFlinkColumn(RowType.RowField column, boolean isPrimaryKey) {
        return new ColumnInfo(
                column.getName(),
                column.getType().toString(),
                column.getDescription().orElse(""),
                isPrimaryKey);
    }

    public String getName() {
        return name;
    }
}
