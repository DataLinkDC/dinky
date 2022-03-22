package com.dlink.metadata.query;

import com.dlink.metadata.constant.PhoenixConstant;


/**
 * @author lcg
 * @operate
 * @date 2022/2/16 14:39
 * @return
 */
public class PhoenixQuery extends AbstractDBQuery {

    @Override
    public String schemaAllSql() {
        return PhoenixConstant.QUERY_SCHEMA_SQL;
    }

    @Override
    public String tablesSql(String schemaName) {
        if (schemaName == null || schemaName.isEmpty()) {
            return PhoenixConstant.QUERY_TABLE_BY_SCHEMA_SQL_DEFAULT;
        }
        return String.format(PhoenixConstant.QUERY_TABLE_BY_SCHEMA_SQL, schemaName);
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        if (schemaName == null || schemaName.isEmpty()) {
            return String.format(PhoenixConstant.QUERY_COLUMNS_SQL_DEFAULT, tableName);
        }
        return String.format(PhoenixConstant.QUERY_COLUMNS_SQL, tableName, schemaName);
    }

    @Override
    public String schemaName() {
        return "TABLE_SCHEM";
    }

    @Override
    public String tableName() {
        return "TABLE_NAME";
    }

    @Override
    public String tableType() {
        return "TABLE_TYPE";
    }

    @Override
    public String tableComment() {
        return "TABLE_NAME";
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
        return "COLUMN_NAME";
    }


    @Override
    public String columnKey() {
        return "KEY_SEQ";
    }


    public String isNullable() {
        return "NULLABLE";
    }

    @Override
    public String rows() {
        return "ROWSNUM";
    }
}
