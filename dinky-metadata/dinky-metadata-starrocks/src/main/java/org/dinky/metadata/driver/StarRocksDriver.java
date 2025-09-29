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
import org.dinky.data.model.QueryData;
import org.dinky.metadata.convert.AbstractJdbcTypeConvert;
import org.dinky.metadata.convert.StarRocksTypeConvert;
import org.dinky.metadata.enums.DriverType;
import org.dinky.metadata.query.IDBQuery;
import org.dinky.metadata.query.StarRocksQuery;
import org.dinky.metadata.result.JdbcSelectResult;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;

import java.util.ArrayList;
import java.util.List;

public class StarRocksDriver extends AbstractJdbcDriver {

    @Override
    public IDBQuery getDBQuery() {
        return new StarRocksQuery();
    }

    @Override
    public AbstractJdbcTypeConvert getTypeConvert() {
        return new StarRocksTypeConvert();
    }

    @Override
    String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getType() {
        return DriverType.STARROCKS.getValue();
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
    public StringBuilder genQueryOption(QueryData queryData) {
        StringBuilder optionBuilder = new StringBuilder()
                .append("select * from `")
                .append(queryData.getSchemaName())
                .append("`.`")
                .append(queryData.getTableName())
                .append("`");

        if (Asserts.isNotNull(queryData.getOption())) {
            String where = queryData.getOption().getWhere();
            if (Asserts.isNotNullString(where)) {
                optionBuilder.append(" where ").append(where);
            }
            String order = queryData.getOption().getOrder();
            if (Asserts.isNotNullString(order)) {
                optionBuilder.append(" order by ").append(order);
            }
            int limitStart = queryData.getOption().getLimitStart();
            int limitEnd = queryData.getOption().getLimitEnd();
            optionBuilder.append(" limit ").append(limitStart).append(",").append(limitEnd);
        }
        return optionBuilder;
    }
}
