package org.dinky.metadata.query;

import org.dinky.metadata.constant.KyuubiConstant;

public class FlinkEngineQuery extends KyuubiEngineQuery{
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
}
