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

package org.dinky.metadata.driver;

import org.dinky.assertion.Asserts;
import org.dinky.data.model.Column;
import org.dinky.data.model.QueryData;
import org.dinky.data.model.Table;
import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.convert.ITypeConvert;
import org.dinky.metadata.convert.MySqlTypeConvert;
import org.dinky.metadata.query.IDBQuery;
import org.dinky.metadata.query.MySqlQuery;
import org.dinky.utils.TextUtil;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * MysqlDriver
 *
 * @since 2021/7/20 14:06
 */
@Slf4j
public class MySqlDriver extends AbstractJdbcDriver {

    @Override
    public IDBQuery getDBQuery() {
        return new MySqlQuery();
    }

    @Override
    public ITypeConvert<AbstractJdbcConfig> getTypeConvert() {
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
        log.info("Auto generateCreateTableSql {}", genTableSql);
        return genTableSql;
    }

    @Override
    public String getCreateTableSql(Table table) {
        return genTable(table);
    }

    private String genTable(Table table) {
        String columnStrs = table.getColumns().stream()
                .map(column -> {
                    String unit = "";
                    if (column.getPrecision() != null
                            && column.getScale() != null
                            && column.getPrecision() > 0
                            && column.getScale() > 0) {
                        unit = String.format("(%s,%s)", column.getPrecision(), column.getScale());
                    } else if (null != column.getLength()) {
                        unit = String.format("(%s)", column.getLength());
                    }

                    final String dv = column.getDefaultValue();
                    String defaultValue = Asserts.isNotNull(dv)
                            ? String.format(" DEFAULT '%s'", dv.isEmpty() ? "''" : dv)
                            : String.format("%s NULL ", !column.isNullable() ? " NOT " : "");

                    return String.format(
                            "  `%s`  %s%s%s%s%s",
                            column.getName(),
                            column.getType(),
                            unit,
                            defaultValue,
                            column.isAutoIncrement() ? " AUTO_INCREMENT " : "",
                            Asserts.isNotNullString(column.getComment())
                                    ? String.format(" COMMENT '%s'", column.getComment())
                                    : "");
                })
                .collect(Collectors.joining(",\n"));

        List<String> columnKeys = table.getColumns().stream()
                .filter(Column::isKeyFlag)
                .map(Column::getName)
                .map(t -> String.format("`%s`", t))
                .collect(Collectors.toList());

        String primaryKeyStr = columnKeys.isEmpty()
                ? ""
                : columnKeys.stream().collect(Collectors.joining(",", ",\n  PRIMARY KEY (", ")\n"));

        return MessageFormat.format(
                "CREATE TABLE IF NOT EXISTS `{0}`.`{1}` (\n{2}{3})\n ENGINE={4}{5}{6};",
                table.getSchema(),
                table.getName(),
                columnStrs,
                primaryKeyStr,
                table.getEngine(),
                Asserts.isNotNullString(table.getOptions()) ? String.format(" %s", table.getOptions()) : "",
                Asserts.isNotNullString(table.getComment()) ? String.format(" COMMENT='%s'", table.getComment()) : "");
    }

    @Override
    public StringBuilder genQueryOption(QueryData queryData) {

        String where = queryData.getOption().getWhere();
        String order = queryData.getOption().getOrder();
        String limitStart = queryData.getOption().getLimitStart();
        String limitEnd = queryData.getOption().getLimitEnd();

        StringBuilder optionBuilder = new StringBuilder()
                .append(String.format("select * from `%s`.`%s`", queryData.getSchemaName(), queryData.getTableName()));

        if (where != null && !where.isEmpty()) {
            optionBuilder.append(" where ").append(where);
        }
        if (order != null && !order.isEmpty()) {
            optionBuilder.append(" order by ").append(order);
        }

        if (TextUtil.isEmpty(limitStart)) {
            limitStart = "0";
        }
        if (TextUtil.isEmpty(limitEnd)) {
            limitEnd = "100";
        }
        optionBuilder.append(" limit ").append(limitStart).append(",").append(limitEnd);

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
                sb.append("`")
                        .append(columns.get(i).getName())
                        .append("`  --  ")
                        .append(columnComment)
                        .append(" \n");
            } else {
                sb.append("`").append(columns.get(i).getName()).append("` \n");
            }
        }
        if (Asserts.isNotNullString(table.getComment())) {
            sb.append(
                    String.format(" FROM `%s`.`%s`; -- %s\n", table.getSchema(), table.getName(), table.getComment()));
        } else {
            sb.append(String.format(" FROM `%s`.`%s`;\n", table.getSchema(), table.getName()));
        }
        return sb.toString();
    }
}
