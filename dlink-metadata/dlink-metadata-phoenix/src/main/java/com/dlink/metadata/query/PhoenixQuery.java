package com.dlink.metadata.query;

/**
 * PhoenixQuery
 *
 * @author gy
 * @since 2022/3/2 13:07
 **/
public class PhoenixQuery extends AbstractDBQuery {


    @Override
    public String schemaName() {
        return "TABLE_SCHEM";
    }

    @Override
    public String schemaAllSql() {
        return "select  DISTINCT(TABLE_SCHEM) from SYSTEM.CATALOG where TABLE_TYPE='u'";
    }

    @Override
    public String tablesSql(String schemaName) {
        return "select * from SYSTEM.CATALOG where TABLE_TYPE='u' and TABLE_SCHEM='"+schemaName+"'";
    }
    @Override
    public String tableName() {
        return "TABLE_NAME";
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return "select * from SYSTEM.CATALOG where TABLE_SCHEM='"+schemaName+"' and TABLE_NAME='"+tableName+"'";
    }

    @Override
    public String tableComment() {
        return "comment";
    }

    @Override
    public String columnName() {
        return "COLUMN_NAME";
    }

    @Override
    public String columnType() {
        return "TABLE_TYPE";
    }

    @Override
    public String columnComment() {
        return "comment";
    }

    @Override
    public String columnKey() {
        return "PK_NAME";
    }

    @Override
    public String isNullable() {
        return "NULL";
    }

}
