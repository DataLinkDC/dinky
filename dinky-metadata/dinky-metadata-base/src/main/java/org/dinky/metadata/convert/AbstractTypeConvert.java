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

package org.dinky.metadata.convert;

import org.dinky.assertion.Asserts;
import org.dinky.data.enums.ColumnType;
import org.dinky.data.model.Column;
import org.dinky.metadata.config.DriverConfig;
import org.dinky.metadata.config.IConnectConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class AbstractTypeConvert<T extends IConnectConfig> implements ITypeConvert<T> {
    protected Map<String, BiFunction<Column, DriverConfig<T>, Optional<ColumnType>>> convertMap = new LinkedHashMap<>();

    @Override
    public String convertToDB(ColumnType columnType) {
        return null;
    }

    @Override
    public ColumnType convert(Column column) {
        return convert(column, null);
    }

    @Override
    public ColumnType convert(Column column, DriverConfig<T> driverConfig) {
        if (Asserts.isNull(column)) {
            return ColumnType.STRING;
        }
        for (Map.Entry<String, BiFunction<Column, DriverConfig<T>, Optional<ColumnType>>> entry :
                convertMap.entrySet()) {
            if (column.getType().contains(entry.getKey())) {
                Optional<ColumnType> columnType = entry.getValue().apply(column, driverConfig);
                if (columnType.isPresent()) {
                    return columnType.get();
                }
            }
        }

        return ColumnType.STRING;
    }

    protected void register(String type, ColumnType columnType) {
        this.convertMap.put(type, (c, d) -> getColumnType(c, columnType, columnType));
    }

    protected void register(String type, ColumnType notNullType, ColumnType nullType) {
        this.convertMap.put(type, (c, d) -> getColumnType(c, notNullType, nullType));
    }

    protected void register(String type, BiFunction<Column, DriverConfig<T>, Optional<ColumnType>> func) {
        this.convertMap.put(type, func);
    }

    private static Optional<ColumnType> getColumnType(Column column, ColumnType notNullType, ColumnType nullType) {
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        if (isNullable) {
            return Optional.of(nullType);
        }
        return Optional.of(notNullType);
    }
}
