package com.dlink.metadata.query;

import com.dlink.metadata.constant.SqlServerConstant;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author lcg
 * @operate
 * @date 2022/1/26 15:42
 * @return
 */
public class SqlServerQuery extends AbstractDBQuery {

    @Override
    public String schemaAllSql() {
        return SqlServerConstant.QUERY_SCHEMA_SQL;
    }

    @Override
    public String tablesSql(String schemaName) {
        return String.format(SqlServerConstant.QUERY_TABLE_BY_SCHEMA_SQL, schemaName);
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return String.format(SqlServerConstant.QUERY_COLUMNS_SQL, tableName);
    }

    @Override
    public String schemaName() {
        return "TABLE_SCHEMA";
    }

    @Override
    public String tableName() {
        return "TABLE_NAME";
    }

    @Override
    public String tableType() {
        return "TYPE";
    }

    @Override
    public String tableComment() {
        return "COMMENTS";
    }


    @Override
    public String columnName() {
        return "COLUMN_NAME";
    }


    @Override
    public String columnType() {
        return "DATA_TYPE";
    }


    @Override
    public String columnComment() {
        return "COMMENTS";
    }


    @Override
    public String columnKey() {
        return "KEY";
    }


    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return 1 == results.getInt("isIdentity");
    }

    public String isNullable() {
        return "NULLVALUE";
    }
}
