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

package com.dlink.model;

import com.dlink.assertion.Asserts;
import com.dlink.utils.SqlUtil;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;



/**
 * Table
 *
 * @author wenmo
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
    /**
     * 表类型
     */
    private TableType tableType = TableType.SINGLE_DATABASE_AND_TABLE;
    /**
     * 分库或分表对应的表名
     */
    private List<String> schemaTableNameList;


    private List<Column> columns;

    public Table() {
    }

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
        String tableWithSql = "";
        if (Asserts.isNotNullString(flinkConfig)) {
            tableWithSql = SqlUtil.replaceAllParam(flinkConfig, "schemaName", schema);
            tableWithSql = SqlUtil.replaceAllParam(tableWithSql, "tableName", name);
        }
        return tableWithSql;
    }

    @Transient
    public String getFlinkTableSql(String flinkConfig) {
        return getFlinkDDL(flinkConfig, name);
    }

    @Transient
    public String getFlinkDDL(String flinkConfig, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + tableName + " (\n");
        List<String> pks = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            String type = columns.get(i).getFlinkType();
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            sb.append("`" + columns.get(i).getName() + "` " + type);
            if (Asserts.isNotNullString(columns.get(i).getComment())) {
                if (columns.get(i).getComment().contains("\'") | columns.get(i).getComment().contains("\"")) {
                    sb.append(" COMMENT '" + columns.get(i).getComment().replaceAll("\"|'", "") + "'");
                } else {
                    sb.append(" COMMENT '" + columns.get(i).getComment() + "'");
                }
            }
            sb.append("\n");
            if (columns.get(i).isKeyFlag()) {
                pks.add(columns.get(i).getName());
            }
        }
        StringBuilder pksb = new StringBuilder("PRIMARY KEY ( ");
        for (int i = 0; i < pks.size(); i++) {
            if (i > 0) {
                pksb.append(",");
            }
            pksb.append("`" + pks.get(i) + "`");
        }
        pksb.append(" ) NOT ENFORCED\n");
        if (pks.size() > 0) {
            sb.append("    ,");
            sb.append(pksb);
        }
        sb.append(")");
        if (Asserts.isNotNullString(comment)) {
            if (comment.contains("\'") | comment.contains("\"")) {
                sb.append(" COMMENT '" + comment.replaceAll("\"|'", "") + "'\n");
            } else {
                sb.append(" COMMENT '" + comment + "'\n");
            }
        }
        sb.append(" WITH (\n");
        sb.append(flinkConfig);
        sb.append(")\n");
        return sb.toString();
    }

    @Transient
    public String getFlinkTableSql(String catalogName, String flinkConfig) {
        StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ");
        String fullSchemaName = catalogName + "." + schema + "." + name;
        sb.append(name + ";\n");
        sb.append("CREATE TABLE IF NOT EXISTS " + name + " (\n");
        List<String> pks = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            String type = columns.get(i).getFlinkType();
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            sb.append("`" + columns.get(i).getName() + "` " + type);
            if (Asserts.isNotNullString(columns.get(i).getComment())) {
                if (columns.get(i).getComment().contains("\'") | columns.get(i).getComment().contains("\"")) {
                    sb.append(" COMMENT '" + columns.get(i).getComment().replaceAll("\"|'", "") + "'");
                } else {
                    sb.append(" COMMENT '" + columns.get(i).getComment() + "'");
                }
            }
            sb.append("\n");
            if (columns.get(i).isKeyFlag()) {
                pks.add(columns.get(i).getName());
            }
        }
        StringBuilder pksb = new StringBuilder("PRIMARY KEY ( ");
        for (int i = 0; i < pks.size(); i++) {
            if (i > 0) {
                pksb.append(",");
            }
            pksb.append("`" + pks.get(i) + "`");
        }
        pksb.append(" ) NOT ENFORCED\n");
        if (pks.size() > 0) {
            sb.append("    ,");
            sb.append(pksb);
        }
        sb.append(")");
        if (Asserts.isNotNullString(comment)) {
            if (comment.contains("\'") | comment.contains("\"")) {
                sb.append(" COMMENT '" + comment.replaceAll("\"|'", "") + "'\n");
            } else {
                sb.append(" COMMENT '" + comment + "'\n");
            }
        }
        sb.append(" WITH (\n");
        sb.append(getFlinkTableWith(flinkConfig));
        sb.append("\n);\n");
        return sb.toString();
    }

    @Transient
    public String getSqlSelect(String catalogName) {
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
        if (Asserts.isNotNullString(comment)) {
            sb.append(" FROM " + schema + "." + name + ";" + " -- " + comment + "\n");
        } else {
            sb.append(" FROM " + schema + "." + name + ";\n");
        }
        return sb.toString();
    }

    @Transient
    public String getCDCSqlInsert(String targetName, String sourceName) {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(targetName);
        sb.append(" SELECT\n");
        for (int i = 0; i < columns.size(); i++) {
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            sb.append("`" + columns.get(i).getName() + "` \n");
        }
        sb.append(" FROM ");
        sb.append(sourceName);
        return sb.toString();
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
