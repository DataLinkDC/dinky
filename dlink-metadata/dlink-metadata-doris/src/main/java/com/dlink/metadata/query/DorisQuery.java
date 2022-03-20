package com.dlink.metadata.query;

import com.dlink.metadata.constant.DorisConstant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DorisQuery extends AbstractDBQuery {
    @Override
    public String schemaAllSql() {
        return DorisConstant.QUERY_ALL_DATABASE;
    }

    @Override
    public String tablesSql(String schemaName) {
        return String.format(DorisConstant.QUERY_TABLE_BY_SCHEMA, schemaName);
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return String.format(DorisConstant.QUERY_COLUMNS_BY_TABLE_AND_SCHEMA, schemaName, tableName);
    }

    @Override
    public String schemaName() {
        return "Database";
    }


    @Override
    public String tableName() {
        return "NAME";
    }


    @Override
    public String tableComment() {
        return "COMMENT";
    }


    @Override
    public String columnName() {
        return "Field";
    }


    @Override
    public String columnType() {
        return "Type";
    }


    @Override
    public String columnComment() {
        return "Comment";
    }


    @Override
    public String columnKey() {
        return "Key";
    }


    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return "auto_increment".equals(results.getString("Extra"));
    }

    @Override
    public String isNullable() {
        return "Null";
    }

    @Override
    public String characterSet() {
        return "Default";
    }

    @Override
    public String collation() {
        return "Default";
    }

    @Override
    public String columnPosition() {
        return "Default";
    }

    @Override
    public String precision() {
        return "Default";
    }

    @Override
    public String scale() {
        return "Default";
    }

    @Override
    public String autoIncrement() {
        return "Default";
    }
}
