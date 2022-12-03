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
import com.dlink.metadata.convert.OracleTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.OracleQuery;
import com.dlink.model.Column;
import com.dlink.model.QueryData;
import com.dlink.model.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * OracleDriver
 *
 * @author wenmo
 * @since 2021/7/21 15:52
 **/
public class OracleDriver extends AbstractJdbcDriver {

    @Override
    String getDriverClass() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public IDBQuery getDBQuery() {
        return new OracleQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new OracleTypeConvert();
    }

    @Override
    public String getType() {
        return "Oracle";
    }

    @Override
    public String getName() {
        return "Oracle数据库";
    }

    /**
     * oracel sql拼接，目前还未实现limit方法
     */
    @Override
    public StringBuilder genQueryOption(QueryData queryData) {

        String where = queryData.getOption().getWhere();
        String order = queryData.getOption().getOrder();

        StringBuilder optionBuilder = new StringBuilder()
                .append("select * from ")
                .append("\"")
                .append(queryData.getSchemaName())
                .append("\"")
                .append(".")
                .append("\"")
                .append(queryData.getTableName())
                .append("\"");

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
            sb.append(" FROM \"" + table.getSchema() + "\".\"" + table.getName() + "\";" + " -- " + table.getComment() + "\n");
        } else {
            sb.append(" FROM \"" + table.getSchema() + "\".\"" + table.getName() + "\";\n");
        }
        return sb.toString();
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(table.getName()).append(" (\n");
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            sb.append("    ");
            if (i > 0) {
                sb.append(",\n");
            }
            sb.append("\"" + columns.get(i).getName() + "\" " + getTypeConvert().convertToDB(columns.get(i)));
            if (columns.get(i).isNullable()) {
                sb.append(" NOT NULL");
            }
        }
        sb.append(");");
        sb.append("\n");
        List<Column> pks = columns.stream().filter(column -> column.isKeyFlag()).collect(Collectors.toList());
        if (Asserts.isNotNullCollection(pks)) {
            sb.append("ALTER TABLE \"" + table.getName() + "\" ADD CONSTRAINT " + table.getName() + "_PK PRIMARY KEY (");
            for (int i = 0; i < pks.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(pks.get(i).getName());
            }
            sb.append(");\n");
        }
        for (int i = 0; i < columns.size(); i++) {
            sb.append("COMMENT ON COLUMN \"" + table.getName() + "\".\"" + columns.get(i).getName() + "\" IS '" + columns.get(i).getComment() + "';\n");
        }
        return sb.toString();
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return new HashMap<>();
    }

    @Override
    protected void createDataSource(DruidDataSource ds, DriverConfig config) {
        ds.setName(config.getName().replaceAll(":", ""));
        ds.setUrl(config.getUrl());
        ds.setDriverClassName(getDriverClass());
        ds.setUsername(config.getUsername());
        ds.setPassword(config.getPassword());
        ds.setValidationQuery("select 1 from dual");
        ds.setTestWhileIdle(true);
        ds.setBreakAfterAcquireFailure(true);
        ds.setFailFast(true);
        ds.setInitialSize(1);
        ds.setMaxActive(8);
        ds.setMinIdle(5);
    }
}
