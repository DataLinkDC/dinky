package com.dlink.metadata.convert;

import com.dlink.metadata.rules.DbColumnType;
import com.dlink.metadata.rules.IColumnType;

/**
 * MySqlTypeConvert
 *
 * @author wenmo
 * @since 2021/7/20 15:21
 **/
public class MySqlTypeConvert implements ITypeConvert{
    @Override
    public IColumnType convert(String columnType) {
        String t = columnType.toLowerCase();
        if (t.contains("char")) {
            return DbColumnType.STRING;
        } else if (t.contains("bigint")) {
            return DbColumnType.LONG;
        } else if (t.contains("tinyint(1)")) {
            return DbColumnType.BOOLEAN;
        } else if (t.contains("int")) {
            return DbColumnType.INTEGER;
        } else if (t.contains("text")) {
            return DbColumnType.STRING;
        } else if (t.contains("bit")) {
            return DbColumnType.BOOLEAN;
        } else if (t.contains("decimal")) {
            return DbColumnType.BIG_DECIMAL;
        } else if (t.contains("clob")) {
            return DbColumnType.CLOB;
        } else if (t.contains("blob")) {
            return DbColumnType.BLOB;
        } else if (t.contains("binary")) {
            return DbColumnType.BYTE_ARRAY;
        } else if (t.contains("float")) {
            return DbColumnType.FLOAT;
        } else if (t.contains("double")) {
            return DbColumnType.DOUBLE;
        } else if (t.contains("json") || t.contains("enum")) {
            return DbColumnType.STRING;
        } else if (t.contains("date") || t.contains("time") || t.contains("year")) {
            return DbColumnType.DATE;
        }
        return DbColumnType.STRING;
    }

    @Override
    public String convertToDB(String columnType) {
        switch (columnType.toLowerCase()){
            case "string":
                return "varchar(255)";
            case "boolean":
                return "tinyint(1)";
            case "int":
            case "integer":
                return "int";
            case "double":
                return "double";
            case "float":
                return "float";
            case "date":
                return "datetime(0)";
            default:
                return "varchar(255)";
        }
    }
}
