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

package org.dinky.metadata.query;

import org.dinky.metadata.constant.StarRocksConstant;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * StarRocksQuery
 **/
public class StarRocksQuery extends AbstractDBQuery {
    @Override
    public String schemaAllSql() {
        return StarRocksConstant.QUERY_ALL_DATABASE;
    }

    @Override
    public String tablesSql(String schemaName) {
        return String.format(StarRocksConstant.QUERY_TABLE_BY_SCHEMA, schemaName);
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return String.format(StarRocksConstant.QUERY_COLUMNS_BY_TABLE_AND_SCHEMA, schemaName, tableName);
    }

    @Override
    public String schemaName() {
        return "Database";
    }

    @Override
    public String tableName() {
        return "NAME";
    }

    @Override
    public String tableComment() {
        return "COMMENT";
    }

    @Override
    public String columnName() {
        return "Field";
    }

    @Override
    public String columnType() {
        return "Type";
    }

    @Override
    public String columnComment() {
        return "Comment";
    }

    @Override
    public String columnKey() {
        return "Key";
    }

    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return "auto_increment".equals(results.getString("Extra"));
    }

    @Override
    public String isNullable() {
        return "Null";
    }

    @Override
    public String characterSet() {
        return "Default";
    }

    @Override
    public String collation() {
        return "Default";
    }

    @Override
    public String columnPosition() {
        return "Default";
    }

    @Override
    public String precision() {
        return "Default";
    }

    @Override
    public String scale() {
        return "Default";
    }

    @Override
    public String autoIncrement() {
        return "Default";
    }

    @Override
    public String isPK() {
        return "YES";
    }
}
