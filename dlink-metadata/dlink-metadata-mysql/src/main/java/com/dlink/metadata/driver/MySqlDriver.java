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
import com.dlink.model.Table;
import com.dlink.model.TableType;
import com.dlink.utils.SplitUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    public Set<Table> getTables(List<String> tables, Map<String, String> splitConfig) {
        // add 分库分表的主要实现
        Set<Table> set = new HashSet<>();
        List<Map<String, String>> schemaList = getSchemaList();
        IDBQuery dbQuery = getDBQuery();

        for (String table : tables) {
            String[] split = table.split("\\\\.");
            String database = split[0];
            String tableName = split[1];
// 匹配对应的表
            List<Map<String, String>> mapList = schemaList.stream()
                    // 过滤不匹配的表
                    .filter(x -> SplitUtil.contains(database, x.get(dbQuery.schemaName())) && SplitUtil.contains(tableName, x.get(dbQuery.tableName()))).collect(Collectors.toList());
            List<Table> tableList = mapList.stream()
                    // 去重
                    .collect(Collectors.collectingAndThen(
                            Collectors.toCollection(
                                    () -> new TreeSet<>(Comparator.comparing(x -> SplitUtil.getReValue(x.get(dbQuery.schemaName()), splitConfig) + "." + SplitUtil.getReValue(x.get(dbQuery.tableName()), splitConfig)))
                            )
                            , ArrayList::new)
                    )
                    .stream().map(x -> {
                        Table tableInfo = new Table();
                        tableInfo.setName(x.get(dbQuery.tableName()));
                        tableInfo.setComment(x.get(dbQuery.tableComment()));
                        tableInfo.setSchema(x.get(dbQuery.schemaName()));
                        tableInfo.setType(x.get(dbQuery.tableType()));
                        tableInfo.setCatalog(x.get(dbQuery.catalogName()));
                        tableInfo.setEngine(x.get(dbQuery.engine()));
                        tableInfo.setOptions(x.get(dbQuery.options()));
                        tableInfo.setRows(Long.valueOf(x.get(dbQuery.rows())));
                        try {
                            tableInfo.setCreateTime(SimpleDateFormat.getDateInstance().parse(x.get(dbQuery.createTime())));
                            String updateTime = x.get(dbQuery.updateTime());
                            if (Asserts.isNotNullString(updateTime)) {
                                tableInfo.setUpdateTime(SimpleDateFormat.getDateInstance().parse(updateTime));
                            }
                        } catch (ParseException ignored) {

                        }
                        TableType tableType = TableType.type(SplitUtil.isSplit(x.get(dbQuery.schemaName()), splitConfig), SplitUtil.isSplit(x.get(dbQuery.tableName()), splitConfig));
                        tableInfo.setTableType(tableType);

                        if (tableType != TableType.SINGLE_DATABASE_AND_TABLE) {
                            String currentSchemaName = SplitUtil.getReValue(x.get(dbQuery.schemaName()), splitConfig) + "." + SplitUtil.getReValue(x.get(dbQuery.tableName()), splitConfig);
                            List<String> schemaTableNameList = mapList.stream()
                                    .filter(y -> (SplitUtil.getReValue(y.get(dbQuery.schemaName()), splitConfig) + "." + SplitUtil.getReValue(y.get(dbQuery.tableName()), splitConfig)).equals(currentSchemaName))
                                    .map(y -> y.get(dbQuery.schemaName()) + "." + y.get(dbQuery.tableName()))
                                    .collect(Collectors.toList());
                            tableInfo.setSchemaTableNameList(schemaTableNameList);
                        } else {
                            tableInfo.setSchemaTableNameList(Collections.singletonList(x.get(dbQuery.schemaName()) + "." + x.get(dbQuery.tableName())));
                        }
                        return tableInfo;
                    }).collect(Collectors.toList());
            set.addAll(tableList);

        }
        return set;
    }

    private List<Map<String, String>> getSchemaList() {
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String sql = "select DATA_LENGTH,TABLE_NAME AS `NAME`,TABLE_SCHEMA AS `Database`,TABLE_COMMENT AS COMMENT,TABLE_CATALOG AS `CATALOG`,TABLE_TYPE AS `TYPE`,ENGINE AS `ENGINE`,CREATE_OPTIONS AS `OPTIONS`,TABLE_ROWS AS `ROWS`,CREATE_TIME,UPDATE_TIME from information_schema.tables WHERE TABLE_TYPE='BASE TABLE'";
        List<Map<String, String>> schemas = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            schemas = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            while (results.next()) {
                Map<String, String> map = new HashMap<>();
                for (String column : columnList) {
                    map.put(column, results.getString(column));
                }
                schemas.add(map);

            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return schemas;
    }
}
