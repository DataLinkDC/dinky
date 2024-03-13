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
import org.dinky.data.model.Table;
import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.convert.DorisTypeConvert;
import org.dinky.metadata.convert.ITypeConvert;
import org.dinky.metadata.convert.source.MysqlType;
import org.dinky.metadata.convert.source.OracleType;
import org.dinky.metadata.convert.source.PostgresType;
import org.dinky.metadata.convert.source.SqlServerType;
import org.dinky.metadata.enums.DriverType;
import org.dinky.metadata.query.DorisQuery;
import org.dinky.metadata.query.IDBQuery;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DorisDriver extends AbstractJdbcDriver {

    @Override
    public IDBQuery getDBQuery() {
        return new DorisQuery();
    }

    @Override
    public ITypeConvert<AbstractJdbcConfig> getTypeConvert() {
        return new DorisTypeConvert();
    }

    @Override
    String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getType() {
        return DriverType.DORIS.getValue();
    }

    @Override
    public String getName() {
        return "Doris";
    }

    @Override
    public JdbcSelectResult executeSql(String sql, Integer limit) {
        // TODO 改为ProcessStep注释
        log.info("Start parse sql...");
        String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(sql));
        log.info(CharSequenceUtil.format("A total of {} statement have been Parsed.", statements.length));
        List<Object> resList = new ArrayList<>();
        JdbcSelectResult result = JdbcSelectResult.buildResult();
        log.info("Start execute sql...");
        for (String item : statements) {
            String type = item.toUpperCase();
            if (type.startsWith("SELECT") || type.startsWith("SHOW") || type.startsWith("DESC")) {
                log.info("Execute query.");
                result = query(item, limit);
            } else if (type.startsWith("INSERT") || type.startsWith("UPDATE") || type.startsWith("DELETE")) {
                try {
                    log.info("Execute update.");
                    resList.add(executeUpdate(item));
                    result.setStatusList(resList);
                    result.success();
                } catch (Exception e) {
                    resList.add(0);
                    result.setStatusList(resList);
                    result.error(LogUtil.getError(e));
                    log.error(e.getMessage());
                    return result;
                }
            } else {
                try {
                    log.info("Execute DDL.");
                    execute(item);
                    resList.add(1);
                    result.setStatusList(resList);
                    result.success();
                } catch (Exception e) {
                    resList.add(0);
                    result.setStatusList(resList);
                    result.error(LogUtil.getError(e));
                    log.error(e.getMessage());
                    return result;
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        HashMap<String, String> map = new HashMap<>();
        map.put("BOOLEAN", "BOOLEAN");
        map.put("TINYINT", "TINYINT");
        map.put("SMALLINT", "SMALLINT");
        map.put("INT", "INT");
        map.put("VARCHAR", "STRING");
        map.put("TEXT", "STRING");
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
                .map(column -> generateColumnSql(column, table.getDriverType()))
                .collect(Collectors.joining(",\n"));

        List<String> columnKeys = table.getColumns().stream()
                .filter(Column::isKeyFlag)
                .map(Column::getName)
                .map(t -> String.format("`%s`", t))
                .collect(Collectors.toList());

        String primaryKeyStr = columnKeys.isEmpty()
                ? ""
                : columnKeys.stream().collect(Collectors.joining(",", "\n UNIQUE KEY (", ")"));

        // 默认开启 BUCKETS AUTO
        String distributeKeyStr = columnKeys.isEmpty()
                ? ""
                : columnKeys.stream().collect(Collectors.joining(",", "\n DISTRIBUTED BY HASH (", ") BUCKETS AUTO"));

        // 默认开启light_schema_change
        String propertiesStr = "\n PROPERTIES ( \"light_schema_change\" = \"true\" )";

        String commentStr =
                Asserts.isNullString(table.getComment()) ? "" : String.format("\n COMMENT \"%s\"", table.getComment());

        return MessageFormat.format(
                "CREATE TABLE IF NOT EXISTS `{0}`.`{1}` (\n{2}\n) ENGINE=OLAP{3}{4}{5}{6}",
                table.getSchema(),
                table.getName(),
                columnStrs,
                primaryKeyStr,
                commentStr,
                distributeKeyStr,
                propertiesStr);
    }

    private String generateColumnSql(Column column, String driverType) {
        String columnType = column.getType();
        int length = Asserts.isNull(column.getLength()) ? 0 : column.getLength();
        int scale = Asserts.isNull(column.getScale()) ? 0 : column.getScale();
        DriverType sourceConnector = DriverType.get(driverType);
        switch (sourceConnector) {
            case MYSQL:
                columnType = MysqlType.toDorisType(column.getType(), length, scale);
                break;
            case DORIS:
                columnType = new DorisTypeConvert().convertToDB(column);
                break;
            case ORACLE:
                columnType = OracleType.toDorisType(column.getType(), length, scale);
                break;
            case POSTGRESQL:
                columnType = PostgresType.toDorisType(column.getType(), length, scale);
                break;
            case SQLSERVER:
                columnType = SqlServerType.toDorisType(column.getType(), length, scale);
                break;
            default:
                String errMsg = "Not support " + driverType + " to Doris column type conversion.";
                throw new UnsupportedOperationException(errMsg);
        }

        String dv = column.getDefaultValue();
        String defaultValue = Asserts.isNotNull(dv)
                ? String.format(" DEFAULT %s", quoteDefaultValue(dv))
                : String.format("%s NULL ", !column.isNullable() ? " NOT " : "");

        return String.format(
                "  `%s`  %s%s%s",
                column.getName(),
                columnType,
                defaultValue,
                Asserts.isNotNullString(column.getComment())
                        ? String.format(" COMMENT '%s'", column.getComment())
                        : "");
    }

    private String quoteDefaultValue(String defaultValue) {
        // DEFAULT current_timestamp not need quote
        if (defaultValue.toLowerCase().contains("current_timestamp")) {
            return "CURRENT_TIMESTAMP";
        }
        if (defaultValue.isEmpty()) {
            return "''";
        }
        return "'" + defaultValue + "'";
    }
}
