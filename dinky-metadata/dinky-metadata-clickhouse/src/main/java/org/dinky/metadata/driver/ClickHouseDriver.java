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
import org.dinky.data.model.Column;
import org.dinky.data.model.Table;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.metadata.ast.Clickhouse20CreateTableStatement;
import org.dinky.metadata.config.AbstractJdbcConfig;
import org.dinky.metadata.convert.ClickHouseTypeConvert;
import org.dinky.metadata.convert.ITypeConvert;
import org.dinky.metadata.enums.DriverType;
import org.dinky.metadata.parser.Clickhouse20StatementParser;
import org.dinky.metadata.query.ClickHouseQuery;
import org.dinky.metadata.query.IDBQuery;
import org.dinky.utils.LogUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
 * @since 2021/7/21 17:14
 */
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
    public ITypeConvert<AbstractJdbcConfig> getTypeConvert() {
        return new ClickHouseTypeConvert();
    }

    @Override
    public String getType() {
        return DriverType.CLICKHOUSE.getValue();
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
                            sqlExplainResults.add(
                                    SqlExplainResult.fail(sql, "No comma can be added to the last field of Table! "));
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
                preparedStatement = conn.get().prepareStatement("explain " + current);
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
                return SqlExplainResult.fail(
                        sqlStatement.toString(), "Table " + schema + sqlStatement.getTableName() + " already exists.");
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
                return SqlExplainResult.fail(
                        sqlStatement.toString(),
                        "Table "
                                + sqlExprTableSource.getSchema()
                                + "."
                                + sqlExprTableSource.getTableName()
                                + " not exists.");
            }
        } else {
            return SqlExplainResult.success(sqlStatement.getClass().getSimpleName(), sqlStatement.toString(), null);
        }
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return new HashMap<>();
    }

    @Override
    public List<Column> listColumns(String schemaName, String tableName) {
        List<Column> columns = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        IDBQuery dbQuery = getDBQuery();
        String tableFieldsSql = dbQuery.columnsSql(schemaName, tableName);
        try {
            preparedStatement = conn.get().prepareStatement(tableFieldsSql);
            results = preparedStatement.executeQuery();
            ResultSetMetaData metaData = results.getMetaData();
            List<String> columnList = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnLabel(i));
            }
            while (results.next()) {
                Column field = new Column();
                String columnName = results.getString(dbQuery.columnName());
                if (columnList.contains(dbQuery.columnKey())) {
                    String key = results.getString(dbQuery.columnKey());
                    field.setKeyFlag(Asserts.isNotNullString(key) && Asserts.isEqualsIgnoreCase(dbQuery.isPK(), key));
                }
                field.setName(columnName);
                if (columnList.contains(dbQuery.columnType())) {
                    String columnType = results.getString(dbQuery.columnType());
                    if (columnType.indexOf("Nullable") >= 0) {
                        field.setNullable(true);
                        columnType = columnType.replaceAll("Nullable\\(", "").replaceAll("\\)", "");
                    }
                    if (columnType.contains("(")) {
                        String type = columnType.replaceAll("\\(.*\\)", "");
                        if (!columnType.contains(",")) {
                            Integer length = Integer.valueOf(columnType.replaceAll("\\D", ""));
                            field.setLength(length);
                        } else {
                            // some database does not have precision
                            if (dbQuery.precision() != null) {
                                // 例如浮点类型的长度和精度是一样的，decimal(10,2)
                                field.setLength(results.getInt(dbQuery.precision()));
                            }
                        }
                        field.setType(type);
                    } else {
                        field.setType(columnType);
                    }
                }
                if (columnList.contains(dbQuery.columnComment())
                        && Asserts.isNotNull(results.getString(dbQuery.columnComment()))) {
                    String columnComment =
                            results.getString(dbQuery.columnComment()).replaceAll("\"|'", "");
                    field.setComment(columnComment);
                }
                if (columnList.contains(dbQuery.columnLength())) {
                    int length = results.getInt(dbQuery.columnLength());
                    if (!results.wasNull()) {
                        field.setLength(length);
                    }
                }
                if (columnList.contains(dbQuery.characterSet())) {
                    field.setCharacterSet(results.getString(dbQuery.characterSet()));
                }
                if (columnList.contains(dbQuery.collation())) {
                    field.setCollation(results.getString(dbQuery.collation()));
                }
                if (columnList.contains(dbQuery.columnPosition())) {
                    field.setPosition(results.getInt(dbQuery.columnPosition()));
                }
                if (columnList.contains(dbQuery.precision())) {
                    field.setPrecision(results.getInt(dbQuery.precision()));
                }
                if (columnList.contains(dbQuery.scale())) {
                    field.setScale(results.getInt(dbQuery.scale()));
                }
                if (columnList.contains(dbQuery.defaultValue())) {
                    field.setDefaultValue(results.getString(dbQuery.defaultValue()));
                }
                if (columnList.contains(dbQuery.autoIncrement())) {
                    field.setAutoIncrement(
                            Asserts.isEqualsIgnoreCase(results.getString(dbQuery.autoIncrement()), "auto_increment"));
                }
                if (columnList.contains(dbQuery.defaultValue())) {
                    field.setDefaultValue(results.getString(dbQuery.defaultValue()));
                }
                field.setJavaType(getTypeConvert().convert(field, config));
                columns.add(field);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return columns;
    }
}
