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
    public String rows() {
        return "ROWS";
    }

    @Override
    public String createTime() {
        return "CREATE_TIME";
    }

    @Override
    public String updateTime() {
        return "UPDATE_TIME";
    }

    @Override
    public String columnName() {
        return "COLUMN_NAME";
    }

    @Override
    public String columnPosition() {
        return "ORDINAL_POSITION";
    }

    @Override
    public String columnType() {
        return "DATA_TYPE";
    }

    @Override
    public String columnComment() {
        return "COLUMN_COMMENT";
    }

    @Override
    public String columnKey() {
        return "COLUMN_KEY";
    }

    @Override
    public String autoIncrement() {
        return "AUTO_INCREMENT";
    }

    @Override
    public String defaultValue() {
        return "COLUMN_DEFAULT";
    }

    @Override
    public String isNullable() {
        return "IS_NULLABLE";
    }

    @Override
    public String precision() {
        return "NUMERIC_PRECISION";
    }

    @Override
    public String scale() {
        return "NUMERIC_SCALE";
    }

    @Override
    public String characterSet() {
        return "CHARACTER_SET_NAME";
    }

    @Override
    public String collation() {
        return "COLLATION_NAME";
    }
}
