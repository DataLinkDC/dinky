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

package org.dinky.data.types;

public class ColumnType {

    private final DataTypes dataType;
    private final LogicalType logicalType;

    public static final ColumnType DEFAULT = new ColumnType(DataTypes.STRING, VarCharType.STRING_TYPE);

    ColumnType(DataTypes dataType, LogicalType logicalType) {
        this.dataType = dataType;
        this.logicalType = logicalType;
    }

    public DataTypes getValue() {
        return dataType;
    }

    public LogicalType getLogicalType() {
        return logicalType;
    }

    public static ColumnType of(DataTypes dataType, LogicalType logicalType) {
        return new ColumnType(dataType, logicalType);
    }
}
