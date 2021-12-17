package com.dlink.metadata.driver;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.Token;
import com.dlink.assertion.Asserts;
import com.dlink.metadata.ast.Clickhouse20CreateTableStatement;
import com.dlink.metadata.convert.ClickHouseTypeConvert;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.parser.Clickhouse20StatementParser;
import com.dlink.metadata.query.ClickHouseQuery;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.model.Table;
import com.dlink.result.SqlExplainResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public List<Table> listTables(String schemaName) {
        List<Table> tableList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String sql = getDBQuery().tablesSql(schemaName);
        try {
            preparedStatement = conn.prepareStatement(sql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                String tableName = results.getString(getDBQuery().tableName());
                if (Asserts.isNotNullString(tableName)) {
                    Table tableInfo = new Table();
                    tableInfo.setName(tableName);
                    tableInfo.setSchema(schemaName);
                    tableList.add(tableInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return tableList;
    }

    @Override
    public String getCreateTableSql(Table table) {
        return null;
    }

    @Override
    public List<SqlExplainResult> explain(String sql){
        List<SqlExplainResult> sqlExplainResults = new ArrayList<>();
        StringBuilder explain = new StringBuilder();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String current = null;
        try {
            Clickhouse20StatementParser parser = new Clickhouse20StatementParser(sql);
            List<SQLStatement> stmtList = new ArrayList<>();
            parser.parseStatementList(stmtList, -1, null);
            if (parser.getLexer().token() != Token.EOF) {
                throw new ParserException("syntax error : " + sql);
            }
            for(SQLStatement item : stmtList){
                current = item.toString();
                String type = item.getClass().getSimpleName();
                if(!(item instanceof SQLSelectStatement)){
                    if(item instanceof Clickhouse20CreateTableStatement){
                        sqlExplainResults.add(checkCreateTable((Clickhouse20CreateTableStatement)item));
                    } else if(item instanceof SQLDropTableStatement){
                        sqlExplainResults.add(checkDropTable((SQLDropTableStatement)item));
                    } else {
                        sqlExplainResults.add(SqlExplainResult.success(type, current, explain.toString()));
                    }
                    continue;
                }
                preparedStatement = conn.prepareStatement("explain "+current);
                results = preparedStatement.executeQuery();
                while(results.next()){
                    explain.append(getTypeConvert().convertValue(results,"explain", "string")+"\r\n");
                }
                sqlExplainResults.add(SqlExplainResult.success(type, current, explain.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sqlExplainResults.add(SqlExplainResult.fail(current, e.getMessage()));
        } finally {
            close(preparedStatement, results);
            return sqlExplainResults;
        }
    }

    private SqlExplainResult checkCreateTable(Clickhouse20CreateTableStatement sqlStatement){
        if(existTable(Table.build(sqlStatement.getTableName()))){
            return SqlExplainResult.fail(sqlStatement.toString(), "Table "+sqlStatement.getSchema()+"."+sqlStatement.getTableName()+" already exists.");
        }else{
            return SqlExplainResult.success(sqlStatement.getClass().getSimpleName(), sqlStatement.toString(), null);
        }
    }

    private SqlExplainResult checkDropTable(SQLDropTableStatement sqlStatement){
        SQLExprTableSource sqlExprTableSource = sqlStatement.getTableSources().get(0);
        if(!existTable(Table.build(sqlExprTableSource.getTableName()))){
            return SqlExplainResult.fail(sqlStatement.toString(), "Table "+sqlExprTableSource.getSchema()+"."+sqlExprTableSource.getTableName()+" not exists.");
        }else{
            return SqlExplainResult.success(sqlStatement.getClass().getSimpleName(), sqlStatement.toString(), null);
        }
    }
}
