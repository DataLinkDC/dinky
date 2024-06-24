package org.dinky.metadata.query;
import org.dinky.metadata.constant.TrinoEngineConstant;

public class TrinoEngineQuery extends KyuubiEngineQuery {

    @Override
    public String schemaAllSql() {
        return TrinoEngineConstant.QUERY_ALL_DATABASE;
    }

    @Override
    public String tablesSql(String schemaName) {
        return TrinoEngineConstant.QUERY_ALL_TABLES_BY_SCHEMA;
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return String.format(TrinoEngineConstant.QUERY_TABLE_SCHEMA, schemaName, tableName);
    }

    @Override
    public String schemaName() {
        return "Catalog";
    }

    @Override
    public String createTableName() {
        return "Create Table";
    }

    @Override
    public String tableName() {
        return "Table";
    }

    @Override
    public String tableComment() {
        return "Comment";
    }

    @Override
    public String columnName() {
        return "Column";
    }

    @Override
    public String columnType() {
        return "Type";
    }

    @Override
    public String columnComment() {
        return "Comment";
    }
}
