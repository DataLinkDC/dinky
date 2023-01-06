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
import com.dlink.metadata.constant.SqlServerConstant;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.SqlServerTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.SqlServerQuery;
import com.dlink.model.Column;
import com.dlink.model.QueryData;
import com.dlink.model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlServerDriver extends AbstractJdbcDriver {
    @Override
    public IDBQuery getDBQuery() {
        return new SqlServerQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new SqlServerTypeConvert();
    }

    @Override
    String getDriverClass() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public String getType() {
        return "SqlServer";
    }

    @Override
    public String getName() {
        return "SqlServer数据库";
    }

    /**
     *  sql拼接，目前还未实现limit方法
     * */
    @Override
    public StringBuilder genQueryOption(QueryData queryData) {

        String where = queryData.getOption().getWhere();
        String order = queryData.getOption().getOrder();

        StringBuilder optionBuilder = new StringBuilder()
                .append("select * from ")
                .append(queryData.getSchemaName())
                .append(".")
                .append(queryData.getTableName());

        if (where != null && !"".equals(where)) {
            optionBuilder.append(" where ").append(where);
        }
        if (order != null && !"".equals(order)) {
            optionBuilder.append(" order by ").append(order);
        }

        return optionBuilder;
    }

    @Override
    public String getSqlSelect(Table table) {
        List<Column> columns = table.getColumns();
        StringBuilder sb = new StringBuilder("SELECT \n");
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
                sb.append("[" + columns.get(i).getName() + "]  --  " + columnComment + " \n");
            } else {
                sb.append("[" + columns.get(i).getName() + "] \n");
            }
        }
        if (Asserts.isNotNullString(table.getComment())) {
            sb.append(" FROM [" + table.getSchema() + "].[" + table.getName() + "];" + " -- " + table.getComment() + "\n");
        } else {
            sb.append(" FROM [" + table.getSchema() + "].[" + table.getName() + "];\n");
        }
        return sb.toString();
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE [" + table.getName() + "] (\n");
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            sb.append("    ");
            if (i > 0) {
                sb.append(",\n");
            }
            sb.append("[" + columns.get(i).getName() + "]" + getTypeConvert().convertToDB(columns.get(i)));
            if (columns.get(i).isNullable()) {
                sb.append(" NULL");
            } else {
                sb.append(" NOT NULL");
            }
        }
        List<String> pks = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isKeyFlag()) {
                pks.add(columns.get(i).getName());
            }
        }
        if (pks.size() > 0) {
            sb.append(", PRIMARY KEY ( ");
            for (int i = 0; i < pks.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("[" + pks.get(i) + "]");
            }
            sb.append(" ) ");
        }
        sb.append(")\n GO ");
        for (Column column : columns) {
            String comment = column.getComment();
            if (comment != null && !comment.isEmpty()) {
                sb.append(String.format(SqlServerConstant.COMMENT_SQL, comment, table.getSchema() == null || table.getSchema().isEmpty() ? "dbo" : table.getSchema(),
                        table.getName(), column.getName()) + " \nGO ");
            }
        }
        return sb.toString();
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return new HashMap<>();
    }
}
