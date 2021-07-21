package com.dlink.metadata.query;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ClickHouseQuery
 *
 * @author wenmo
 * @since 2021/7/21 17:15
 **/
public class ClickHouseQuery extends AbstractDBQuery {
    @Override
    public String schemaAllSql() {
        return "show databases";
    }

    @Override
    public String tablesSql(String schemaName) {
        return "show table status WHERE 1=1 ";
    }


    @Override
    public String columnsSql(String schemaName,String tableName) {
        return "desc `"+tableName+"`";
    }

    @Override
    public String schemaName() {
        return "db";
    }


    @Override
    public String tableName() {
        return "name";
    }


    @Override
    public String tableComment() {
        return "comment";
    }


    @Override
    public String columnName() {
        return "name";
    }


    @Override
    public String columnType() {
        return "type";
    }


    @Override
    public String columnComment() {
        return "comment";
    }


    @Override
    public String columnKey() {
        return "KEY";
    }


    @Override
    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return "auto_increment".equals(results.getString("Extra"));
    }

    @Override
    public String isNotNull() {
        return "NULL";
    }
}
