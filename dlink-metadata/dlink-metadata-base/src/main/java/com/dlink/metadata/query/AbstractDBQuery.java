package com.dlink.metadata.query;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AbstractDBQuery
 *
 * @author wenmo
 * @since 2021/7/20 13:50
 **/
public abstract class AbstractDBQuery implements IDBQuery {

    @Override
    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return false;
    }

    @Override
    public String[] columnCustom() {
        return null;
    }

    public String schemaName() {
        return "SCHEMA";
    }

    @Override
    public String catalogName() {
        return "CATALOG";
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
    public String tableType() {
        return "TYPE";
    }

    @Override
    public String engine() {
        return "ENGINE";
    }

    @Override
    public String options() {
        return "OPTIONS";
    }

    @Override
    public String columnName() {
        return "FIELD";
    }

    @Override
    public String columnType() {
        return "TYPE";
    }

    @Override
    public String columnComment() {
        return "COMMENT";
    }

    @Override
    public String columnKey() {
        return "KEY";
    }

    @Override
    public String isNotNull() {
        return "NULL";
    }
}
