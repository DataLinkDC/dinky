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

import com.dlink.metadata.constant.PrestoConstant;

public class PrestoQuery extends AbstractDBQuery {

    @Override
    public String schemaAllSql() {
        return PrestoConstant.QUERY_ALL_DATABASE;
    }

    @Override
    public String tablesSql(String schemaName) {
        return PrestoConstant.QUERY_ALL_TABLES_BY_SCHEMA;
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return String.format(PrestoConstant.QUERY_TABLE_SCHEMA, schemaName, tableName);
    }

    @Override
    public String schemaName() {
        return "Catalog";
    }

    @Override
    public String createTableName() {
        return "Create Table";
    }

    @Override
    public String tableName() {
        return "Table";
    }

    @Override
    public String tableComment() {
        return "Comment";
    }

    @Override
    public String columnName() {
        return "Column";
    }

    @Override
    public String columnType() {
        return "Type";
    }

    @Override
    public String columnComment() {
        return "Comment";
    }

}
