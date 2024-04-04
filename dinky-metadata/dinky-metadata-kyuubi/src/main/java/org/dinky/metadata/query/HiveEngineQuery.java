package org.dinky.metadata.query;

import org.dinky.metadata.constant.KyuubiConstant;

public class HiveEngineQuery extends KyuubiEngineQuery{

    @Override
    public String schemaAllSql() {
        return KyuubiConstant.QUERY_ALL_DATABASE;
    }

    @Override
    public String tablesSql(String schemaName) {
        return KyuubiConstant.QUERY_ALL_TABLES_BY_SCHEMA;
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return String.format(KyuubiConstant.QUERY_TABLE_SCHEMA, schemaName, tableName);
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
