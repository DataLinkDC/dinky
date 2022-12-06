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

package com.dlink.metadata.driver;

import com.dlink.assertion.Asserts;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.model.Column;
import com.dlink.model.Schema;
import com.dlink.model.Table;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AbstractDriver
 *
 * @author wenmo
 * @since 2021/7/19 23:32
 */
public abstract class AbstractDriver implements Driver {

    protected DriverConfig config;

    public abstract IDBQuery getDBQuery();

    public abstract ITypeConvert getTypeConvert();

    public boolean canHandle(String type) {
        return Asserts.isEqualsIgnoreCase(getType(), type);
    }

    public Driver setDriverConfig(DriverConfig config) {
        this.config = config;
        return this;
    }

    public boolean isHealth() {
        return false;
    }

    public List<Schema> getSchemasAndTables() {
        return listSchemas().stream().peek(schema -> schema.setTables(listTables(schema.getName()))).sorted().collect(Collectors.toList());
    }

    public List<Table> getTablesAndColumns(String schema) {
        return listTables(schema).stream().peek(table -> table.setColumns(listColumns(schema, table.getName()))).sorted().collect(Collectors.toList());
    }

    @Override
    public Table getTable(String schemaName, String tableName) {
        List<Table> tables = listTables(schemaName);
        Table table = null;
        for (Table item : tables) {
            if (Asserts.isEquals(item.getName(), tableName)) {
                table = item;
            }
        }
        if (Asserts.isNotNull(table)) {
            table.setColumns(listColumns(schemaName, table.getName()));
        }
        return table;
    }

    @Override
    public boolean existTable(Table table) {
        return listTables(table.getSchema()).stream().anyMatch(tableItem -> Asserts.isEquals(tableItem.getName(), table.getName()));
    }

    @Override
    public String getSqlSelect(Table table) {
        List<Column> columns = table.getColumns();
        StringBuilder sb = new StringBuilder("SELECT\n");
        for (int i = 0; i < columns.size(); i++) {
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            String columnComment = columns.get(i).getComment();
            if (Asserts.isNotNullString(columnComment)) {
                if (columnComment.contains("\'") | columnComment.contains("\"")) {
                    columnComment = columnComment.replaceAll("\"|'", "");
                }
                sb.append("`" + columns.get(i).getName() + "`  --  " + columnComment + " \n");
            } else {
                sb.append("`" + columns.get(i).getName() + "` \n");
            }
        }
        if (Asserts.isNotNullString(table.getComment())) {
            sb.append(" FROM " + table.getSchema() + "." + table.getName() + ";" + " -- " + table.getComment() + "\n");
        } else {
            sb.append(" FROM " + table.getSchema() + "." + table.getName() + ";\n");
        }
        return sb.toString();
    }

    @Override
    public List<Map<String, String>> getSplitSchemaList() {
        throw new RuntimeException("该数据源暂不支持分库分表");
    }
}
