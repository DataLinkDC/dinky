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
import com.dlink.metadata.convert.MySqlTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.MySqlQuery;
import com.dlink.model.Column;
import com.dlink.model.QueryData;
import com.dlink.model.Table;
import com.dlink.utils.TextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MysqlDriver
 *
 * @author wenmo
 * @since 2021/7/20 14:06
 **/
public class MySqlDriver extends AbstractJdbcDriver {

    @Override
    public IDBQuery getDBQuery() {
        return new MySqlQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new MySqlTypeConvert();
    }

    @Override
    public String getType() {
        return "MySql";
    }

    @Override
    public String getName() {
        return "MySql数据库";
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        HashMap<String, String> map = new HashMap<>();
        map.put("VARCHAR", "STRING");
        map.put("TEXT", "STRING");
        map.put("INT", "INT");
        map.put("DATETIME", "TIMESTAMP");
        return map;
    }

    @Override
    public String generateCreateTableSql(Table table) {
        String genTableSql = genTable(table);
        logger.info("Auto generateCreateTableSql {}", genTableSql);
        return genTableSql;
    }

    @Override
    public String getCreateTableSql(Table table) {

        return genTable(table);
    }

    public String genTable(Table table) {
        StringBuilder key = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append("`")
                .append(table.getSchema())
                .append("`.`")
                .append(table.getName())
                .append("`")
                .append(" (\n");
        for (int i = 0; i < table.getColumns().size(); i++) {
            Column column = table.getColumns().get(i);
            sb.append("  `")
                    .append(column.getName()).append("`  ")
                    .append(column.getType());
            // 处理浮点类型
            if (column.getPrecision() > 0 && column.getScale() > 0) {
                sb.append("(")
                        .append(column.getLength())
                        .append(",").append(column.getScale())
                        .append(")");
            } else if (null != column.getLength()) { // 处理字符串类型和数值型
                sb.append("(").append(column.getLength()).append(")");
            }
            if (Asserts.isNotNull(column.getDefaultValue())) {
                if ("".equals(column.getDefaultValue())) {
                    sb.append(" DEFAULT ").append("\"\"");
                } else {
                    sb.append(" DEFAULT ").append(column.getDefaultValue());
                }
            } else {
                if (!column.isNullable()) {
                    sb.append(" NOT ");
                }
                sb.append(" NULL ");
            }
            if (column.isAutoIncrement()) {
                sb.append(" AUTO_INCREMENT ");
            }
            if (Asserts.isNotNullString(column.getComment())) {
                sb.append(" COMMENT '").append(column.getComment()).append("'");
            }
            if (column.isKeyFlag()) {
                key.append("`").append(column.getName()).append("`,");
            }
            if (i < table.getColumns().size() || key.length() > 0) {
                sb.append(",");
            }
            sb.append("\n");
        }

        if (key.length() > 0) {
            sb.append("  PRIMARY KEY (");
            sb.append(key.substring(0, key.length() - 1));
            sb.append(")\n");
        }

        sb.append(")\n ENGINE=").append(table.getEngine());
        if (Asserts.isNotNullString(table.getOptions())) {
            sb.append(" ").append(table.getOptions());
        }

        if (Asserts.isNotNullString(table.getComment())) {
            sb.append(" COMMENT='").append(table.getComment()).append("'");
        }
        sb.append(";");
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
                .append("`")
                .append(queryData.getSchemaName())
                .append("`")
                .append(".")
                .append("`")
                .append(queryData.getTableName())
                .append("`");

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
                .append(limitStart)
                .append(",")
                .append(limitEnd);

        return optionBuilder;
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
                sb.append("`").append(columns.get(i).getName()).append("`  --  ").append(columnComment).append(" \n");
            } else {
                sb.append("`").append(columns.get(i).getName()).append("` \n");
            }
        }
        if (Asserts.isNotNullString(table.getComment())) {
            sb.append(" FROM `").append(table.getSchema()).append("`.`").append(table.getName()).append("`;").append(" -- ").append(table.getComment()).append("\n");
        } else {
            sb.append(" FROM `").append(table.getSchema()).append("`.`").append(table.getName()).append("`;\n");
        }
        return sb.toString();
    }
}
