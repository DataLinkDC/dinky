package com.dlink.metadata.convert;

import com.dlink.metadata.rules.DbColumnType;
import com.dlink.metadata.rules.IColumnType;

/**
 * OracleTypeConvert
 *
 * @author wenmo
 * @since 2021/7/21 16:00
 **/
public class OracleTypeConvert implements ITypeConvert {
    @Override
    public IColumnType convert(String columnType) {
        String t = columnType.toLowerCase();
        if (t.contains("char")) {
            return DbColumnType.STRING;
        } else if (t.contains("date") || t.contains("timestamp")) {
            return DbColumnType.DATE;
        } else if (t.contains("number")) {
            if (t.matches("number\\(+\\d\\)")) {
                return DbColumnType.INTEGER;
            } else if (t.matches("number\\(+\\d{2}+\\)")) {
                return DbColumnType.LONG;
            }
            return DbColumnType.BIG_DECIMAL;
        } else if (t.contains("float")) {
            return DbColumnType.FLOAT;
        } else if (t.contains("clob")) {
            return DbColumnType.STRING;
        } else if (t.contains("blob")) {
            return DbColumnType.BLOB;
        } else if (t.contains("binary")) {
            return DbColumnType.BYTE_ARRAY;
        } else if (t.contains("raw")) {
            return DbColumnType.BYTE_ARRAY;
        }
        return DbColumnType.STRING;
    }

    @Override
    public String convertToDB(String columnType) {
        switch (columnType.toLowerCase()){
            case "string":
                return "varchar(255)";
            case "boolean":
            case "int":
            case "integer":
            case "double":
            case "float":
                return "number";
            case "date":
                return "date";
            default:
                return "varchar(255)";
        }
    }
}
