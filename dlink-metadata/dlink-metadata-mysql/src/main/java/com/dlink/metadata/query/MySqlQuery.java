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

/**
 * MySqlQuery
 *
 * @author wenmo
 * @since 2021/7/20 14:01
 **/
public class MySqlQuery extends AbstractDBQuery {

    @Override
    public String schemaAllSql() {
        return "show databases";
    }

    @Override
    public String tablesSql(String schemaName) {
        return "select TABLE_NAME AS `NAME`,TABLE_SCHEMA AS `Database`,TABLE_COMMENT AS COMMENT,TABLE_CATALOG AS `CATALOG`"
                + ",TABLE_TYPE AS `TYPE`,ENGINE AS `ENGINE`,CREATE_OPTIONS AS `OPTIONS`,TABLE_ROWS AS `ROWS`"
                + ",CREATE_TIME,UPDATE_TIME from information_schema.tables"
                + " where TABLE_SCHEMA = '" + schemaName + "'";
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return "select COLUMN_NAME,COLUMN_TYPE,COLUMN_COMMENT,COLUMN_KEY,EXTRA AS AUTO_INCREMENT"
                + ",COLUMN_DEFAULT,IS_NULLABLE,NUMERIC_PRECISION,NUMERIC_SCALE,CHARACTER_SET_NAME"
                + ",COLLATION_NAME,ORDINAL_POSITION from INFORMATION_SCHEMA.COLUMNS "
                + "where TABLE_SCHEMA = '" + schemaName + "' and TABLE_NAME = '" + tableName + "' "
                + "order by ORDINAL_POSITION";
    }

    @Override
    public String schemaName() {
        return "Database";
    }

    @Override
    public String columnType() {
        return "COLUMN_TYPE";
    }
}
