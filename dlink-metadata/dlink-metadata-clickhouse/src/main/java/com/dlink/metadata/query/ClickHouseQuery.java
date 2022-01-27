package com.dlink.metadata.query;

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
        return "show tables";
    }


    @Override
    public String columnsSql(String schemaName,String tableName) {
        return String.format("select * from system.columns where database='%s' and table='%s'",
            schemaName, tableName);
    }

    @Override
    public String schemaName() {
        return "name";
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
        return "is_in_primary_key";
    }

    @Override
    public String columnPosition() {
        return "position";
    }

    @Override
    public String isNullable() {
        return "NULL";
    }
}
