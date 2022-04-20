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
        return "SELECT currentDatabase()";
    }

    @Override
    public String tablesSql(String schemaName) {
        return "show tables";
    }


    @Override
    public String columnsSql(String schemaName, String tableName) {
        return "desc `" + tableName + "`";
    }

    @Override
    public String schemaName() {
        return "database";
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
    public String isNullable() {
        return "NULL";
    }
    
    @Override
    public String createTableName() {
        return "statement";
    }
}
