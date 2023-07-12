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

import com.dlink.metadata.convert.DorisTypeConvert;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.query.DorisQuery;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.model.Table;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;
import com.dlink.utils.LogUtil;
import com.dlink.utils.SqlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.text.CharSequenceUtil;

public class DorisDriver extends AbstractJdbcDriver {

    @Override
    public IDBQuery getDBQuery() {
        return new DorisQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new DorisTypeConvert();
    }

    @Override
    String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getType() {
        return "Doris";
    }

    @Override
    public String getName() {
        return "Doris";
    }

    @Override
    public JdbcSelectResult executeSql(String sql, Integer limit) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Start parse sql...");
        String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(sql));
        process.info(CharSequenceUtil.format("A total of {} statement have been Parsed.", statements.length));
        List<Object> resList = new ArrayList<>();
        JdbcSelectResult result = JdbcSelectResult.buildResult();
        process.info("Start execute sql...");
        for (String item : statements) {
            String type = item.toUpperCase();
            if (type.startsWith("SELECT") || type.startsWith("SHOW") || type.startsWith("DESC")) {
                process.info("Execute query.");
                result = query(item, limit);
            } else if (type.startsWith("INSERT") || type.startsWith("UPDATE") || type.startsWith("DELETE")) {
                try {
                    process.info("Execute update.");
                    resList.add(executeUpdate(item));
                    result.setStatusList(resList);
                    result.success();
                } catch (Exception e) {
                    resList.add(0);
                    result.setStatusList(resList);
                    result.error(LogUtil.getError(e));
                    process.error(e.getMessage());
                    return result;
                }
            } else {
                try {
                    process.info("Execute DDL.");
                    execute(item);
                    resList.add(1);
                    result.setStatusList(resList);
                    result.success();
                } catch (Exception e) {
                    resList.add(0);
                    result.setStatusList(resList);
                    result.error(LogUtil.getError(e));
                    process.error(e.getMessage());
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
        map.put("TEXY", "STRING");
        map.put("DATETIME", "TIMESTAMP");
        return map;
    }

    @Override
    public String generateCreateTableSql(Table table) {
        String createTableSql = getCreateTableSql(table);
        logger.info("Auto generateCreateTableSql {}", createTableSql);
        return createTableSql;
    }

    @Override
    public String getCreateTableSql(Table table) {
        List<String> dorisTypes = Arrays.asList("BOOLEAN", "TINYINT", "SMALLINT", "SMALLINT", "INT", "BIGINT", "LARGEINT", "FLOAT", "DOUBLE", "DECIMAL", "DATE", "DATETIME", "CHAR", "VARCHAR", "TEXT", "TIMESTAMP","STRING");
        StringBuffer keyBuffer = new StringBuffer();
        StringBuffer ddlBuffer = new StringBuffer();
        ddlBuffer.append("CREATE TABLE IF NOT EXISTS ").append(table.getSchema()).append(".").append(table.getName())
            .append(" (").append(System.lineSeparator());
        for (int i = 0; i < table.getColumns().size(); i++) {
            Column columnInfo = table.getColumns().get(i);
            String cType = columnInfo.getType().split(" ")[0].toUpperCase();
            ddlBuffer.append(" `").append(columnInfo.getName()).append("` ");
            if (!dorisTypes.contains(cType)){
                 cType = columnInfo.getJavaType().getFlinkType().toUpperCase();
                if(!dorisTypes.contains(cType)){
                    logger.error("doris does not support {} type",columnInfo.getType());
                    return  "";
                }
            }
            if (cType.equalsIgnoreCase("TIMESTAMP")) {
                ddlBuffer.append("DATETIME");
            } else if (columnInfo.getType().equalsIgnoreCase("TEXT")) {
                ddlBuffer.append("STRING");
            }else{
                ddlBuffer.append(cType);
            }
            if(columnInfo.getLength()!=null &&columnInfo.getLength()>0 ) {
                ddlBuffer.append("(").append(cType.equalsIgnoreCase("VARCHAR") ?columnInfo.getLength() * 3 : columnInfo.getLength()).append(")");
            }
            if (columnInfo.getComment()!=null) {
                ddlBuffer.append(" COMMENT '").append(columnInfo.getComment()).append("'");
            }
            if (i < table.getColumns().size() - 1) {
                ddlBuffer.append(",");
            }
            ddlBuffer.append(System.lineSeparator());
            if(columnInfo.isKeyFlag()){
                keyBuffer.append(columnInfo.getName()).append(",");
            }

        }
        ddlBuffer.append(System.lineSeparator());
        String primaryKeys = keyBuffer.substring(0, keyBuffer.length() - 1);
        ddlBuffer.append(") UNIQUE KEY (").append(primaryKeys).append(")").append(System.lineSeparator());
        ddlBuffer.append("COMMENT '").append(table.getComment()).append("'");
        ddlBuffer.append(" DISTRIBUTED BY HASH (").append(primaryKeys).append(") BUCKETS AUTO").append(System.lineSeparator());
        //
        ddlBuffer.append(" PROPERTIES ( \"replication_allocation\" = \"tag.location.default: 3\")");
        return  ddlBuffer.toString();
    }
}
