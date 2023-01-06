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
 * AbstractDBQuery
 *
 * @author wenmo
 * @since 2021/7/20 13:50
 **/
public abstract class AbstractDBQuery implements IDBQuery {

    @Override
    public String createTableSql(String schemaName, String tableName) {
        return "show create table " + schemaName + "." + tableName;
    }

    @Override
    public String createTableName() {
        return "Create Table";
    }

    @Override
    public String createViewName() {
        return "Create View";
    }

    @Override
    public String[] columnCustom() {
        return null;
    }

    public String schemaName() {
        return "SCHEMA";
    }

    @Override
    public String catalogName() {
        return "CATALOG";
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
    public String tableType() {
        return "TYPE";
    }

    @Override
    public String engine() {
        return "ENGINE";
    }

    @Override
    public String options() {
        return "OPTIONS";
    }

    @Override
    public String rows() {
        return "ROWS";
    }

    @Override
    public String createTime() {
        return "CREATE_TIME";
    }

    @Override
    public String updateTime() {
        return "UPDATE_TIME";
    }

    @Override
    public String columnName() {
        return "COLUMN_NAME";
    }

    @Override
    public String columnPosition() {
        return "ORDINAL_POSITION";
    }

    @Override
    public String columnType() {
        return "DATA_TYPE";
    }

    @Override
    public String columnComment() {
        return "COLUMN_COMMENT";
    }

    @Override
    public String columnKey() {
        return "COLUMN_KEY";
    }

    @Override
    public String autoIncrement() {
        return "AUTO_INCREMENT";
    }

    @Override
    public String defaultValue() {
        return "COLUMN_DEFAULT";
    }

    @Override
    public String columnLength() {
        return "LENGTH";
    }

    @Override
    public String isNullable() {
        return "IS_NULLABLE";
    }

    @Override
    public String precision() {
        return "NUMERIC_PRECISION";
    }

    @Override
    public String scale() {
        return "NUMERIC_SCALE";
    }

    @Override
    public String characterSet() {
        return "CHARACTER_SET_NAME";
    }

    @Override
    public String collation() {
        return "COLLATION_NAME";
    }

    @Override
    public String isPK() {
        return "PRI";
    }

    @Override
    public String nullableValue() {
        return "YES";
    }
}
