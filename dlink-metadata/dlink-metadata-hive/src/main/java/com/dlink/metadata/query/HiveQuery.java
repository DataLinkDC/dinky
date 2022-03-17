package com.dlink.metadata.query;

import com.dlink.metadata.constant.HiveConstant;

public class HiveQuery extends AbstractDBQuery{
    @Override
    public String schemaAllSql() {
        return HiveConstant.QUERY_ALL_DATABASE;
    }


    @Override
    public String tablesSql(String schemaName) {
        return HiveConstant.QUERY_ALL_TABLES_BY_SCHEMA;
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return String.format(HiveConstant.QUERY_TABLE_SCHEMA, schemaName, tableName);
    }

    @Override
    public String schemaName() {
        return "database_name";
    }

    @Override
    public String createTableName() {
        return "createtab_stmt";
    }
    @Override
    public String tableName() {
        return "tab_name";
    }

    @Override
    public String tableComment() {
        return "comment";
    }


    @Override
    public String columnName() {
        return "col_name";
    }


    @Override
    public String columnType() {
        return "data_type";
    }


    @Override
    public String columnComment() {
        return "comment";
    }

}
