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

package com.dlink.metadata.query;

import com.dlink.metadata.constant.SqlServerConstant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlServerQuery extends AbstractDBQuery {

    @Override
    public String schemaAllSql() {
        return SqlServerConstant.QUERY_SCHEMA_SQL;
    }

    @Override
    public String tablesSql(String schemaName) {
        return String.format(SqlServerConstant.QUERY_TABLE_BY_SCHEMA_SQL, schemaName);
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return String.format(SqlServerConstant.QUERY_COLUMNS_SQL, tableName);
    }

    @Override
    public String schemaName() {
        return "TABLE_SCHEMA";
    }

    @Override
    public String tableName() {
        return "TABLE_NAME";
    }

    @Override
    public String tableType() {
        return "TYPE";
    }

    @Override
    public String tableComment() {
        return "COMMENTS";
    }

    @Override
    public String columnName() {
        return "COLUMN_NAME";
    }

    @Override
    public String columnType() {
        return "DATA_TYPE";
    }

    @Override
    public String columnComment() {
        return "COMMENTS";
    }

    @Override
    public String columnKey() {
        return "KEY";
    }

    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return 1 == results.getInt("isIdentity");
    }

    public String isNullable() {
        return "NULLVALUE";
    }

}
