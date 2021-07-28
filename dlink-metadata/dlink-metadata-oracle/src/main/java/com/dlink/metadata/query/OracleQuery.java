package com.dlink.metadata.query;

/**
 * OracleQuery
 *
 * @author wenmo
 * @since 2021/7/21 15:54
 **/
public class OracleQuery extends AbstractDBQuery {

    @Override
    public String schemaAllSql() {
        return "SELECT DISTINCT OWNER FROM ALL_TAB_COMMENTS";
    }

    @Override
    public String tablesSql(String schemaName) {
        return "SELECT * FROM ALL_TAB_COMMENTS WHERE OWNER='"+schemaName+"'";
    }

    @Override
    public String columnsSql(String schemaName,String tableName) {
        return "SELECT A.COLUMN_NAME, CASE WHEN A.DATA_TYPE='NUMBER' THEN "
                + "(CASE WHEN A.DATA_PRECISION IS NULL THEN A.DATA_TYPE "
                + "WHEN NVL(A.DATA_SCALE, 0) > 0 THEN A.DATA_TYPE||'('||A.DATA_PRECISION||','||A.DATA_SCALE||')' "
                + "ELSE A.DATA_TYPE||'('||A.DATA_PRECISION||')' END) "
                + "ELSE A.DATA_TYPE END DATA_TYPE, B.COMMENTS,A.NULLABLE,DECODE((select count(1) from all_constraints pc,all_cons_columns pcc"
                + "  where pcc.column_name = A.column_name"
                + "  and pcc.constraint_name = pc.constraint_name"
                + "  and pc.constraint_type ='P'"
                + "  and pcc.owner = upper(A.OWNER)"
                + "  and pcc.table_name = upper(A.TABLE_NAME)),0,'','PRI') KEY "
                + "FROM ALL_TAB_COLUMNS A "
                + " INNER JOIN ALL_COL_COMMENTS B ON A.TABLE_NAME = B.TABLE_NAME AND A.COLUMN_NAME = B.COLUMN_NAME AND B.OWNER = '"+schemaName+"'"
                + " LEFT JOIN ALL_CONSTRAINTS D ON D.TABLE_NAME = A.TABLE_NAME AND D.CONSTRAINT_TYPE = 'P' AND D.OWNER = '"+schemaName+"'"
                + " LEFT JOIN ALL_CONS_COLUMNS C ON C.CONSTRAINT_NAME = D.CONSTRAINT_NAME AND C.COLUMN_NAME=A.COLUMN_NAME AND C.OWNER = '"+schemaName+"'"
                + "WHERE A.OWNER = '"+schemaName+"' AND A.TABLE_NAME = '"+tableName+"' ORDER BY A.COLUMN_ID ";
    }

    @Override
    public String schemaName() {
        return "OWNER";
    }


    @Override
    public String tableName() {
        return "TABLE_NAME";
    }


    @Override
    public String tableComment() {
        return "COMMENTS";
    }


    @Override
    public String columnName() {
        return "COLUMN_NAME";
    }


    @Override
    public String columnType() {
        return "DATA_TYPE";
    }


    @Override
    public String columnComment() {
        return "COMMENTS";
    }


    @Override
    public String columnKey() {
        return "KEY";
    }

    @Override
    public String isNotNull() {
        return "NULLABLE";
    }
}
