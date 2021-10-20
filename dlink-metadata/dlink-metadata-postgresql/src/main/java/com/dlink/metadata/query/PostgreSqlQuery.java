package com.dlink.metadata.query;

/**
 * PostgreSqlQuery
 *
 * @author wenmo
 * @since 2021/7/22 9:29
 **/
public class PostgreSqlQuery extends AbstractDBQuery {
    @Override
    public String schemaAllSql() {
        return null;
    }

    @Override
    public String tablesSql(String schemaName) {
        return "SELECT A.tablename, obj_description(relfilenode, 'pg_class') AS comments FROM pg_tables A, pg_class B WHERE A.schemaname='"+schemaName+"' AND A.tablename = B.relname";
    }


    @Override
    public String columnsSql(String schemaName,String tableName) {
        return "SELECT A.attname AS name,format_type (A.atttypid,A.atttypmod) AS type,col_description (A.attrelid,A.attnum) AS comment,\n" +
                "(CASE WHEN (SELECT COUNT (*) FROM pg_constraint AS PC WHERE A.attnum = PC.conkey[1] AND PC.contype = 'p') > 0 THEN 'PRI' ELSE '' END) AS key \n" +
                "FROM pg_class AS C,pg_attribute AS A WHERE A.attrelid='"+schemaName+"."+tableName+"'::regclass AND A.attrelid= C.oid AND A.attnum> 0 AND NOT A.attisdropped ORDER  BY A.attnum";
    }

    @Override
    public String schemaName() {
        return null;
    }


    @Override
    public String tableName() {
        return "tablename";
    }


    @Override
    public String tableComment() {
        return "comments";
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
        return "key";
    }

    @Override
    public String isNotNull() {
        return null;
    }
}
