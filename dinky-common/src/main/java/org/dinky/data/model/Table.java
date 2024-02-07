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

package org.dinky.data.model;

import org.dinky.assertion.Asserts;
import org.dinky.data.enums.TableType;
import org.dinky.utils.SqlUtil;

import java.beans.Transient;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

/**
 * Table
 *
 * @since 2021/7/19 23:27
 */
@Getter
@Setter
public class Table implements Serializable, Comparable<Table>, Cloneable {

    private static final long serialVersionUID = 4209205512472367171L;

    private String name;
    private String schema;
    private String catalog;
    private String comment;
    private String type;
    private String engine;
    private String options;
    private Long rows;
    private Date createTime;
    private Date updateTime;
    /** 表类型 */
    private TableType tableType = TableType.SINGLE_DATABASE_AND_TABLE;
    /** 分库或分表对应的表名 */
    private List<String> schemaTableNameList;

    private List<Column> columns;

    /** 驱动类型, @see org.dinky.metadata.enums.DriverType */
    private String driverType;

    public Table() {}

    public Table(String name, String schema, List<Column> columns) {
        this.name = name;
        this.schema = schema;
        this.columns = columns;
    }

    @Transient
    public String getSchemaTableName() {
        return Asserts.isNullString(schema) ? name : schema + "." + name;
    }

    @Transient
    public String getSchemaTableNameWithUnderline() {
        return Asserts.isNullString(schema) ? name : schema + "_" + name;
    }

    @Override
    public int compareTo(Table o) {
        return this.name.compareTo(o.getName());
    }

    public static Table build(String name) {
        return new Table(name, null, null);
    }

    public static Table build(String name, String schema) {
        return new Table(name, schema, null);
    }

    public static Table build(String name, String schema, List<Column> columns) {
        return new Table(name, schema, columns);
    }

    @Transient
    public String getFlinkTableWith(String flinkConfig) {
        if (Asserts.isNotNullString(flinkConfig)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("schemaName", schema);
            replacements.put("tableName", name);

            return SqlUtil.replaceAllParam(flinkConfig, replacements);
        }
        return "";
    }

    @Transient
    public String getFlinkDDL(String flinkConfig, String tableName) {
        String columnStrs = columns.stream()
                .map(column -> {
                    String comment = "";
                    if (Asserts.isNotNullString(column.getComment())) {
                        comment = String.format(
                                " COMMENT '%s'", column.getComment().replaceAll("[\"']", ""));
                    }
                    return String.format("    `%s` %s%s", column.getName(), column.getFlinkType(), comment);
                })
                .collect(Collectors.joining(",\n"));

        List<String> columnKeys = columns.stream()
                .filter(Column::isKeyFlag)
                .map(Column::getName)
                .map(t -> String.format("`%s`", t))
                .collect(Collectors.toList());

        String primaryKeyStr = columnKeys.isEmpty()
                ? ""
                : columnKeys.stream().collect(Collectors.joining(",", ",\n    PRIMARY KEY ( ", " ) NOT ENFORCED\n"));

        return MessageFormat.format(
                "CREATE TABLE IF NOT EXISTS {0} (\n{1}{2}) WITH (\n{3})\n",
                tableName, columnStrs, primaryKeyStr, flinkConfig);
    }

    @Transient
    public String getFlinkTableSql(String catalogName, String flinkConfig) {
        String createSql = getFlinkDDL(getFlinkTableWith(flinkConfig), name);
        return String.format("DROP TABLE IF EXISTS %s;\n%s", name, createSql);
    }

    @Override
    public Object clone() {
        Table table = null;
        try {
            table = (Table) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return table;
    }
}
