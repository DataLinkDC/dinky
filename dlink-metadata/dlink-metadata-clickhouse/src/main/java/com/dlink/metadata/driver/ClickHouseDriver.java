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

import com.dlink.metadata.ast.Clickhouse20CreateTableStatement;
import com.dlink.metadata.convert.ClickHouseTypeConvert;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.parser.Clickhouse20StatementParser;
import com.dlink.metadata.query.ClickHouseQuery;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.model.Table;
import com.dlink.result.SqlExplainResult;
import com.dlink.utils.LogUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.Token;

/**
 * ClickHouseDriver
 *
 * @author wenmo
 * @since 2021/7/21 17:14
 **/
public class ClickHouseDriver extends AbstractJdbcDriver {

    @Override
    String getDriverClass() {
        return "ru.yandex.clickhouse.ClickHouseDriver";
    }

    @Override
    public IDBQuery getDBQuery() {
        return new ClickHouseQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new ClickHouseTypeConvert();
    }

    @Override
    public String getType() {
        return "ClickHouse";
    }

    @Override
    public String getName() {
        return "ClickHouse OLAP 数据库";
    }

    @Override
    public List<SqlExplainResult> explain(String sql) {
        String initialSql = sql;
        List<SqlExplainResult> sqlExplainResults = new ArrayList<>();
        StringBuilder explain = new StringBuilder();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String current = null;
        try {
            sql = sql.replaceAll("(?i)if exists", "");
            Clickhouse20StatementParser parser = new Clickhouse20StatementParser(sql);
            List<SQLStatement> stmtList = new ArrayList<>();
            parser.parseStatementList(stmtList, -1, null);
            if (parser.getLexer().token() != Token.EOF) {
                throw new ParserException("syntax error : " + sql);
            }
            for (SQLStatement item : stmtList) {
                current = item.toString();
                String type = item.getClass().getSimpleName();
                if (!(item instanceof SQLSelectStatement)) {
                    if (item instanceof Clickhouse20CreateTableStatement) {
                        Matcher m = Pattern.compile(",\\s*\\)").matcher(sql);
                        if (m.find()) {
                            sqlExplainResults.add(SqlExplainResult.fail(sql, "No comma can be added to the last field of Table! "));
                            break;
                        }
                        sqlExplainResults.add(checkCreateTable((Clickhouse20CreateTableStatement) item));
                    } else if (item instanceof SQLDropTableStatement) {
                        sqlExplainResults.add(checkDropTable((SQLDropTableStatement) item, initialSql));
                    } else {
                        sqlExplainResults.add(SqlExplainResult.success(type, current, explain.toString()));
                    }
                    continue;
                }
                preparedStatement =  conn.get().prepareStatement("explain " + current);
                results = preparedStatement.executeQuery();
                while (results.next()) {
                    explain.append(getTypeConvert().convertValue(results, "explain", "string") + "\r\n");
                }
                sqlExplainResults.add(SqlExplainResult.success(type, current, explain.toString()));
            }
        } catch (Exception e) {
            sqlExplainResults.add(SqlExplainResult.fail(current, LogUtil.getError(e)));
        } finally {
            close(preparedStatement, results);
            return sqlExplainResults;
        }
    }

    private SqlExplainResult checkCreateTable(Clickhouse20CreateTableStatement sqlStatement) {
        if (existTable(Table.build(sqlStatement.getTableName()))) {
            if (sqlStatement.isIfNotExists()) {
                return SqlExplainResult.success(sqlStatement.getClass().getSimpleName(), sqlStatement.toString(), null);
            } else {
                String schema = null == sqlStatement.getSchema() ? "" : sqlStatement.getSchema() + ".";
                return SqlExplainResult.fail(sqlStatement.toString(), "Table " + schema + sqlStatement.getTableName() + " already exists.");
            }
        } else {
            return SqlExplainResult.success(sqlStatement.getClass().getSimpleName(), sqlStatement.toString(), null);
        }
    }

    private SqlExplainResult checkDropTable(SQLDropTableStatement sqlStatement, String sql) {
        SQLExprTableSource sqlExprTableSource = sqlStatement.getTableSources().get(0);
        if (!existTable(Table.build(sqlExprTableSource.getTableName()))) {
            if (Pattern.compile("(?i)if exists").matcher(sql).find()) {
                return SqlExplainResult.success(sqlStatement.getClass().getSimpleName(), sqlStatement.toString(), null);
            } else {
                return SqlExplainResult.fail(sqlStatement.toString(), "Table " + sqlExprTableSource.getSchema() + "." + sqlExprTableSource.getTableName() + " not exists.");
            }
        } else {
            return SqlExplainResult.success(sqlStatement.getClass().getSimpleName(), sqlStatement.toString(), null);
        }
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return new HashMap<>();
    }
}
