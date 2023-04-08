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
import com.dlink.metadata.convert.PostgreSqlTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.PostgreSqlQuery;
import com.dlink.model.Column;
import com.dlink.model.QueryData;
import com.dlink.model.Table;
import com.dlink.utils.TextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PostgreSqlDriver
 *
 * @author wenmo
 * @since 2021/7/22 9:28
 **/
public class PostgreSqlDriver extends AbstractJdbcDriver {

    @Override
    String getDriverClass() {
        return "org.postgresql.Driver";
    }

    @Override
    public IDBQuery getDBQuery() {
        return new PostgreSqlQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new PostgreSqlTypeConvert();
    }

    @Override
    public String getType() {
        return "PostgreSql";
    }

    @Override
    public String getName() {
        return "PostgreSql 数据库";
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return new HashMap<>();
    }

    @Override
    public String generateCreateSchemaSql(String schemaName) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE SCHEMA ").append(schemaName);
        return sb.toString();
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
                sb.append("\"" + columns.get(i).getName() + "\"  --  " + columnComment + " \n");
            } else {
                sb.append("\"" + columns.get(i).getName() + "\" \n");
            }
        }
        if (Asserts.isNotNullString(table.getComment())) {
            sb.append(" FROM \"" + table.getSchema() + "\".\"" + table.getName() + "\";" + " -- " + table.getComment()
                    + "\n");
        } else {
            sb.append(" FROM \"" + table.getSchema() + "\".\"" + table.getName() + "\";\n");
        }
        return sb.toString();
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder key = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        StringBuilder comments = new StringBuilder();

        sb.append("CREATE TABLE \"").append(table.getSchema()).append("\".\"").append(table.getName())
                .append("\" (\n");

        for (int i = 0; i < table.getColumns().size(); i++) {
            Column column = table.getColumns().get(i);
            if (i > 0) {
                sb.append(",\n");
            }
            sb.append("  \"").append(column.getName()).append("\" ");
            sb.append(column.getType());
            if (column.getPrecision() > 0 && column.getScale() > 0) {
                sb.append("(")
                        .append(column.getPrecision())
                        .append(",").append(column.getScale())
                        .append(")");
            } else if (null != column.getLength()) {
                sb.append("(").append(column.getLength()).append(")");
            }
            if (!column.isNullable()) {
                sb.append(" NOT NULL");
            }
            if (Asserts.isNotNullString(column.getDefaultValue()) && !column.getDefaultValue().contains("nextval")) {
                sb.append(" DEFAULT ").append(column.getDefaultValue());
            }

            if (column.isKeyFlag()) {
                if (key.length() > 0) {
                    key.append(",");
                }
                key.append(column.getName());
            }

            if (Asserts.isNotNullString(column.getComment())) {
                comments.append("COMMENT ON COLUMN \"").append(table.getSchema()).append("\".\"")
                        .append(table.getName()).append("\".\"")
                        .append(column.getName()).append("\" IS '").append(column.getComment()).append("';\n");
            }
        }

        if (Asserts.isNotNullString(table.getComment())) {
            comments.append("COMMENT ON TABLE \"").append(table.getSchema()).append("\".\"")
                    .append(table.getName()).append("\" IS '").append(table.getComment()).append("';");
        }
        if (key.length() > 0) {
            sb.append(",\n");
            sb.append("CONSTRAINT ");
            sb.append(table.getName());
            sb.append("_pkey PRIMARY KEY (");
            sb.append(key);
            sb.append(")");
        }
        sb.append("\n);\n\n").append(comments);

        return sb.toString();
    }

    @Override
    public StringBuilder genQueryOption(QueryData queryData) {

        String where = queryData.getOption().getWhere();
        String order = queryData.getOption().getOrder();
        String limitStart = queryData.getOption().getLimitStart();
        String limitEnd = queryData.getOption().getLimitEnd();

        StringBuilder optionBuilder = new StringBuilder()
                .append("select * from ")
                .append(queryData.getSchemaName())
                .append(".")
                .append(queryData.getTableName());

        if (where != null && !where.equals("")) {
            optionBuilder.append(" where ").append(where);
        }
        if (order != null && !order.equals("")) {
            optionBuilder.append(" order by ").append(order);
        }

        if (TextUtil.isEmpty(limitStart)) {
            limitStart = "0";
        }
        if (TextUtil.isEmpty(limitEnd)) {
            limitEnd = "100";
        }
        optionBuilder.append(" limit ")
                .append(limitEnd);

        return optionBuilder;
    }
}
