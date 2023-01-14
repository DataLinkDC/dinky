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

import org.dinky.metadata.constant.HiveConstant;

public class HiveQuery extends AbstractDBQuery {

    @Override
    public String schemaAllSql() {
        return HiveConstant.QUERY_ALL_DATABASE;
    }

    @Override
    public String tablesSql(String schemaName) {
        return HiveConstant.QUERY_ALL_TABLES_BY_SCHEMA;
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return String.format(HiveConstant.QUERY_TABLE_SCHEMA, schemaName, tableName);
    }

    @Override
    public String schemaName() {
        return "database_name";
    }

    @Override
    public String createTableName() {
        return "createtab_stmt";
    }

    @Override
    public String tableName() {
        return "tab_name";
    }

    @Override
    public String tableComment() {
        return "comment";
    }

    @Override
    public String columnName() {
        return "col_name";
    }

    @Override
    public String columnType() {
        return "data_type";
    }

    @Override
    public String columnComment() {
        return "comment";
    }
}
