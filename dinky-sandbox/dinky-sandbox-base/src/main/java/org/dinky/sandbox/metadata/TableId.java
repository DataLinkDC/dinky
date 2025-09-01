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

import org.dinky.assertion.Asserts;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Nullable;

public class TableId implements Serializable {
    private final String boxName;
    private final String catalogName;
    private final String databaseName;
    private final String tableName;

    private final boolean isPrivate;

    private transient int cachedHashCode;

    public TableId(
            @Nullable String boxName,
            @Nullable String catalogName,
            @Nullable String databaseName,
            String tableName,
            boolean isPrivate) {
        this.boxName = boxName;
        this.catalogName = catalogName;
        this.databaseName = databaseName;
        this.tableName = Objects.requireNonNull(tableName);
        this.isPrivate = isPrivate;
    }

    public String getBoxName() {
        return boxName;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public static TableId of(String boxName, String catalogName, String databaseName, String tableName) {
        return new TableId(boxName, catalogName, databaseName, tableName, false);
    }

    public static TableId of(String catalogName, String databaseName, String tableName) {
        return new TableId("public", catalogName, databaseName, tableName, false);
    }

    public static TableId of(String databaseName, String tableName) {
        return new TableId("public", "default_catalog", databaseName, tableName, false);
    }

    public static TableId of(String tableName) {
        return new TableId("public", "default_catalog", "default_database", tableName, false);
    }

    public static TableId withPrivate(String boxName) {
        return new TableId(boxName, "default_catalog", "default_database", "result", true);
    }

    public static TableId withPrivate(String boxName, String name) {
        TableId parsedTableId = parse(name);
        return new TableId(
                boxName,
                parsedTableId.getCatalogName(),
                parsedTableId.getDatabaseName(),
                parsedTableId.getTableName(),
                true);
    }

    public static TableId parse(String name) {
        String[] parts = name.split("\\.");
        if (parts.length == 2) {
            return TableId.of(parts[0], parts[1]);
        } else if (parts.length == 3) {
            return TableId.of(parts[0], parts[1], parts[2]);
        } else if (parts.length >= 4) {
            return TableId.of(parts[0], parts[1], parts[2], parts[3]);
        }
        return TableId.of(parts[0]);
    }

    public String getPrivateName() {
        return boxName;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableId that = (TableId) o;
        return Objects.equals(boxName, that.boxName)
                && Objects.equals(catalogName, that.catalogName)
                && Objects.equals(databaseName, that.databaseName)
                && Objects.equals(tableName, that.tableName);
    }

    @Override
    public int hashCode() {
        if (cachedHashCode == 0) {
            cachedHashCode = Objects.hash(boxName, catalogName, databaseName, tableName);
        }
        return cachedHashCode;
    }

    public String identifier() {
        if (Asserts.isNullString(boxName)) {
            if (Asserts.isNullString(catalogName)) {
                if (Asserts.isNullString(databaseName)) {
                    return tableName;
                }
                return databaseName + "." + tableName;
            }
            return catalogName + "." + databaseName + "." + tableName;
        }
        return boxName + "." + catalogName + "." + databaseName + "." + tableName;
    }

    public String tableName() {
        if (Asserts.isNullString(catalogName)) {
            if (Asserts.isNullString(databaseName)) {
                return tableName;
            }
            return databaseName + "." + tableName;
        }
        return catalogName + "." + databaseName + "." + tableName;
    }

    public String toString() {
        return identifier();
    }
}
