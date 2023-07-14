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

import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.StarRocksTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.StarRocksQuery;
import com.dlink.metadata.result.JdbcSelectResult;
import com.dlink.model.Column;
import com.dlink.utils.LogUtil;
import com.dlink.utils.SqlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StarRocksDriver extends AbstractJdbcDriver {

    @Override
    public IDBQuery getDBQuery() {
        return new StarRocksQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new StarRocksTypeConvert();
    }

    @Override
    String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getType() {
        return "StarRocks";
    }

    @Override
    public String getName() {
        return "StarRocks";
    }

    @Override
    public JdbcSelectResult executeSql(String sql, Integer limit) {
        String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(sql));
        List<Object> resList = new ArrayList<>();
        JdbcSelectResult result = JdbcSelectResult.buildResult();
        for (String item : statements) {
            String type = item.toUpperCase();
            if (type.startsWith("SELECT") || type.startsWith("SHOW") || type.startsWith("DESC")) {
                result = query(item, limit);
            } else if (type.startsWith("INSERT") || type.startsWith("UPDATE") || type.startsWith("DELETE")) {
                try {
                    resList.add(executeUpdate(item));
                    result.setStatusList(resList);
                } catch (Exception e) {
                    resList.add(0);
                    result.setStatusList(resList);
                    result.error(LogUtil.getError(e));
                    return result;
                }
            } else {
                try {
                    execute(item);
                    resList.add(1);
                    result.setStatusList(resList);
                } catch (Exception e) {
                    resList.add(0);
                    result.setStatusList(resList);
                    result.error(LogUtil.getError(e));
                    return result;
                }
            }
        }
        result.success();
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
    public List<Column> listColumns(String schemaName, String tableName) {
        // StarRocks 中声明为 Key 的列（可能是多个）必须顺序声明在建表语句头部，因此按 Key 对列重新排序
        return listColumnsSortByPK(schemaName, tableName);
    }
}
