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

    /**
     * 获取模式名称下的所有表，从元数据表中获取获取
     *
     * @param schemaName 模式名称
     * @return String
     */
    @Override
    public String tablesSql(String schemaName) {
        return "select name from system.tables where 1=1 and database='" + schemaName + "'";
    }

    /**
     * 从元数据表中获取表字段信息
     *
     * @param schemaName 模式名称
     * @param tableName  表名
     * @return String
     */
    @Override
    public String columnsSql(String schemaName, String tableName) {
        return "select  * from system.columns where 1=1 and database='" + schemaName + "' and table='" + tableName + "'";
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
    public String isNullable() {
        return "NULL";
    }

    @Override
    public String createTableName() {
        return "statement";
    }

    @Override
    public String isPK() {
        return "1";
    }
}
