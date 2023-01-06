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
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;
import com.dlink.utils.LogUtil;
import com.dlink.utils.SqlUtil;

import java.util.ArrayList;
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
}
