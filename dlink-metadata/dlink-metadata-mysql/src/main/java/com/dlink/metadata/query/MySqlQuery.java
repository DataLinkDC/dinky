package com.dlink.metadata.query;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MySqlQuery
 *
 * @author wenmo
 * @since 2021/7/20 14:01
 **/
public class MySqlQuery extends AbstractDBQuery {

    @Override
    public String schemaAllSql() {
        return "show databases";
    }

    @Override
    public String tablesSql(String schemaName) {
        return "select TABLE_NAME AS `NAME`,TABLE_SCHEMA AS `Database`,TABLE_COMMENT AS COMMENT,TABLE_CATALOG AS `CATALOG`" +
                ",TABLE_TYPE AS `TYPE`,ENGINE AS `ENGINE`,CREATE_OPTIONS AS `OPTIONS`,TABLE_ROWS AS `ROWS`" +
                ",CREATE_TIME,UPDATE_TIME from information_schema.tables" +
                " where TABLE_SCHEMA = '" + schemaName + "'";
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return "select COLUMN_NAME,DATA_TYPE,COLUMN_COMMENT,COLUMN_KEY,EXTRA AS AUTO_INCREMENT" +
                ",COLUMN_DEFAULT,IS_NULLABLE,NUMERIC_PRECISION,NUMERIC_SCALE,CHARACTER_SET_NAME" +
                ",COLLATION_NAME,ORDINAL_POSITION from INFORMATION_SCHEMA.COLUMNS " +
                "where TABLE_SCHEMA = '" + schemaName + "' and TABLE_NAME = '" + tableName + "' " +
                "order by ORDINAL_POSITION";
    }

    @Override
    public String schemaName() {
        return "Database";
    }

}
