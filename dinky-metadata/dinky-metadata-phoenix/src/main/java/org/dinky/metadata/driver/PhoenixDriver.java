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

import org.dinky.data.model.Column;
import org.dinky.data.model.QueryData;
import org.dinky.data.model.Table;
import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.constant.PhoenixConstant;
import org.dinky.metadata.convert.ITypeConvert;
import org.dinky.metadata.convert.PhoenixTypeConvert;
import org.dinky.metadata.enums.DriverType;
import org.dinky.metadata.query.IDBQuery;
import org.dinky.metadata.query.PhoenixQuery;
import org.dinky.metadata.result.JdbcSelectResult;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class PhoenixDriver extends AbstractJdbcDriver {

    @Override
    public IDBQuery getDBQuery() {
        return new PhoenixQuery();
    }

    @Override
    public ITypeConvert<AbstractJdbcConfig> getTypeConvert() {
        return new PhoenixTypeConvert();
    }

    @Override
    String getDriverClass() {
        return PhoenixConstant.PHOENIX_DRIVER;
    }

    @Override
    public String getType() {
        return DriverType.PHOENIX.getValue();
    }

    /** sql拼接，目前还未实现limit方法 */
    @Override
    public StringBuilder genQueryOption(QueryData queryData) {

        String where = queryData.getOption().getWhere();
        String order = queryData.getOption().getOrder();

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

        return optionBuilder;
    }

    @Override
    public String getName() {
        return "Phoenix";
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder sql = new StringBuilder();
        List<Column> columns = table.getColumns();
        sql.append(" CREATE VIEW IF NOT EXISTS \"").append(table.getName()).append("\" ( ");
        sql.append("    rowkey varchar primary key ");
        PhoenixTypeConvert phoenixTypeConvert = new PhoenixTypeConvert();
        if (columns != null) {
            for (Column column : columns) {
                sql.append(", \"")
                        .append(column.getColumnFamily())
                        .append("\".\"")
                        .append(column.getName())
                        .append("\"  ")
                        .append(phoenixTypeConvert.convertToDB(column));
            }
        }
        sql.append(" ) ");
        return sql.toString();
    }

    @Override
    public Driver connect() {
        try {
            Class.forName(getDriverClass());
            // TODO：phoenix连接配置，后续可设置为参数传入，以适应不同配置的集群
            Properties properties = new Properties();
            properties.put("phoenix.schema.isNamespaceMappingEnabled", "true");
            properties.put("phoenix.schema.mapSystemTablesToNamespac", "true");
            Connection connection =
                    DriverManager.getConnection(config.getConnectConfig().getUrl(), properties);
            conn.set(connection);
            // 设置为自动提交，否则upsert语句不生效
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * 解决phoenix SQL多语句执行问题 phoenix SQL中不能执行带;语句
     *
     * @param sql
     * @return
     */
    public String parsePhoenixSql(String sql) {
        return StringUtils.remove(sql, ";");
    }

    @Override
    public JdbcSelectResult query(String sql, Integer limit) {
        return super.query(parsePhoenixSql(sql), limit);
    }

    @Override
    public int executeUpdate(String sql) throws Exception {
        return super.executeUpdate(parsePhoenixSql(sql));
    }

    @Override
    public boolean execute(String sql) throws Exception {
        return super.execute(parsePhoenixSql(sql));
    }
}
