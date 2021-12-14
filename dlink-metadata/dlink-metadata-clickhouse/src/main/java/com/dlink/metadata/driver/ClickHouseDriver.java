package com.dlink.metadata.driver;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.clickhouse.parser.ClickhouseStatementParser;
import com.dlink.metadata.convert.ClickHouseTypeConvert;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.query.ClickHouseQuery;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.model.Table;
import com.dlink.result.SqlExplainResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public String getCreateTableSql(Table table) {
        return null;
    }

    @Override
    public SqlExplainResult explain(String sql){
        boolean correct = true;
        String error = null;
        String type = "ClickHouseSql";
        StringBuilder explain = new StringBuilder();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        try {
            ClickhouseStatementParser parser = new ClickhouseStatementParser(sql);
            SQLStatement sqlStatement = parser.parseStatement();
            type = sqlStatement.getClass().getSimpleName();
            if(!(sqlStatement instanceof SQLSelectStatement)){
                return SqlExplainResult.success(type, sql, explain.toString());
            }
            preparedStatement = conn.prepareStatement("explain "+sql);
            results = preparedStatement.executeQuery();
            while(results.next()){
                explain.append(getTypeConvert().convertValue(results,"explain", "string")+"\r\n");
            }
        } catch (SQLException e) {
            correct = false;
            error = e.getMessage();
        } finally {
            close(preparedStatement, results);
        }
        if(correct) {
            return SqlExplainResult.success(type, sql, explain.toString());
        }else {
            return SqlExplainResult.fail(sql,error);
        }
    }
}
