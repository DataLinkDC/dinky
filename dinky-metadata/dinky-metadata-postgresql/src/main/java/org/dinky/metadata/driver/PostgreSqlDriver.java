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
import org.dinky.metadata.convert.PostgreSqlTypeConvert;
import org.dinky.metadata.enums.DriverType;
import org.dinky.metadata.query.IDBQuery;
import org.dinky.metadata.query.PostgreSqlQuery;
import org.dinky.utils.TextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PostgreSqlDriver
 *
 * @since 2021/7/22 9:28
 */
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
    public ITypeConvert<AbstractJdbcConfig> getTypeConvert() {
        return new PostgreSqlTypeConvert();
    }

    @Override
    public String getType() {
        return DriverType.POSTGRESQL.getValue();
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
            sb.append(" FROM \""
                    + table.getSchema()
                    + "\".\""
                    + table.getName()
                    + "\";"
                    + " -- "
                    + table.getComment()
                    + "\n");
        } else {
            sb.append(" FROM \"" + table.getSchema() + "\".\"" + table.getName() + "\";\n");
        }
        return sb.toString();
    }

    @Override
    public String getCreateTableSql(Table table) {
        String tableName = table.getName();
        String schema = table.getSchema();
        List<Column> columns = table.getColumns();

        String columnDefinitions =
                columns.stream().map(this::getColumnDefinition).collect(Collectors.joining(",\n"));

        // comment table:COMMENT ON TABLE "schemaName"."tableName" IS 'comment';
        String comment =
                String.format("COMMENT ON TABLE \"%s\".\"%s\" IS '%s';\n", schema, tableName, table.getComment());

        // get primaryKeys
        List<String> columnKeys = table.getColumns().stream()
                .filter(Column::isKeyFlag)
                .map(Column::getName)
                .map(t -> String.format("\"%s\"", t))
                .collect(Collectors.toList());

        // add primaryKey
        String primaryKeyStr = columnKeys.isEmpty()
                ? ""
                : columnKeys.stream().collect(Collectors.joining(",", ", \n\tPRIMARY KEY (", ")\n"));

        // CREATE TABLE "schemaName"."tableName" ( columnDefinitions ); comment
        String ddl = String.format(
                "CREATE TABLE \"%s\".\"%s\" (\n%s%s);\n%s",
                schema, tableName, columnDefinitions, primaryKeyStr, comment);

        ddl += columns.stream()
                // COMMENT ON COLUMN "schemaName"."tableName"."columnName" IS 'comment'
                .map(c -> String.format(
                        "COMMENT ON COLUMN \"%s\".\"%s\".\"%s\" IS '%s';\n",
                        schema, tableName, c.getName(), c.getComment()))
                .collect(Collectors.joining());

        return ddl;
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
        optionBuilder.append(" offset ").append(limitStart).append(" limit ").append(limitEnd);

        return optionBuilder;
    }

    private String getColumnDefinition(Column column) {

        String length = "";
        if (null != column.getPrecision()
                && column.getPrecision() > 0
                && null != column.getScale()
                && column.getScale() > 0) {
            length = String.format("(%s,%s)", column.getPrecision(), column.getScale());
        } else if (null != column.getLength()) {
            length = String.format("(%s)", column.getLength());
        }
        // "columnName" type(length) null_able default_value
        return String.format(
                "\t\"%s\" %s%s%s%s",
                column.getName(),
                column.getType(),
                length,
                column.isNullable() ? "" : " NOT NULL",
                Asserts.isNotNullString(column.getDefaultValue())
                                && !column.getDefaultValue().contains("nextval")
                        ? " DEFAULT " + column.getDefaultValue()
                        : "");
    }
}
