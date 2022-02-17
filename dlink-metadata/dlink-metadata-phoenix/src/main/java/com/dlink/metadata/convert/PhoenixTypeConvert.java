package com.dlink.metadata.convert;

import com.dlink.metadata.constant.PhoenixEnum;
import com.dlink.metadata.rules.DbColumnType;
import com.dlink.metadata.rules.IColumnType;

public class PhoenixTypeConvert implements ITypeConvert {
    @Override
    public IColumnType convert(String columnType) {
        String t = columnType.toLowerCase();
        if (t.contains("char") || t.contains("varchar") || t.contains("text") ||
                t.contains("nchar") || t.contains("nvarchar") || t.contains("ntext")
                || t.contains("uniqueidentifier") || t.contains("sql_variant")) {
            return DbColumnType.STRING;
        } else if (t.contains("int") || t.contains("tinyint") || t.contains("smallint") || t.contains("integer")) {
            return DbColumnType.INTEGER;
        } else if (t.contains("bigint")) {
            return DbColumnType.LONG;
        } else if (t.contains("float")) {
            return DbColumnType.FLOAT;
        } else if (t.contains("decimal") || t.contains("money") || t.contains("smallmoney")
                || t.contains("numeric")) {
            return DbColumnType.BIG_DECIMAL;
        } else if (t.contains("double")) {
            return DbColumnType.DOUBLE;
        } else if (t.contains("boolean")) {
            return DbColumnType.BOOLEAN;
        } else if (t.contains("time")) {
            return DbColumnType.TIME;
        } else if (t.contains("date")) {
            return DbColumnType.DATE;
        } else if (t.contains("smalldatetime") || t.contains("datetime")) {
            return DbColumnType.TIMESTAMP;
        } else if (t.contains("timestamp") || t.contains("binary") || t.contains("varbinary") || t.contains("image")) {
            return DbColumnType.BYTE_ARRAY;
        }
        return DbColumnType.STRING;
    }

    @Override
    public String convertToDB(String columnType) {
        try {
            Integer typeNum = Integer.valueOf(columnType);
            return PhoenixEnum.getDataTypeEnum(typeNum).toString();
        } catch (Exception e) {
            switch (columnType.toUpperCase()) {
                case "UNSIGNED_INT":
                    return "UNSIGNED_INT";
                case "INT":
                case "INTEGER":
                    return "INTEGER";
                case "TINYINT":
                    return "TINYINT";
                case "double":
                    return "double";
                case "BIGINT":
                    return "BIGINT";
                case "UNSIGNED_TINYINT":
                    return "UNSIGNED_TINYINT";
                case "UNSIGNED_SMALLINT":
                    return "UNSIGNED_SMALLINT";
                case "FLOAT":
                    return "FLOAT";
                case "UNSIGNED_FLOAT":
                    return "UNSIGNED_FLOAT";
                case "DOUBLE":
                    return "DOUBLE";
                case "UNSIGNED_DOUBLE":
                    return "UNSIGNED_DOUBLE";
                case "DECIMAL":
                    return "DECIMAL";
                case "BOOLEAN":
                    return "BOOLEAN";
                case "TIME ":
                    return "TIME ";
                case "DATE":
                    return "DATE";
                case "TIMESTAMP":
                    return "TIMESTAMP";
                case "UNSIGNED_TIME":
                    return "UNSIGNED_TIME";
                case "UNSIGNED_DATE":
                    return "UNSIGNED_DATE";
                case "UNSIGNED_TIMESTAMP":
                    return "UNSIGNED_TIMESTAMP";
                case "VARCHAR":
                    return "VARCHAR";
                case "CHAR":
                    return "VARCHAR";
                case "BINARY":
                    return "BINARY";
                case "VARBINARY":
                    return "VARBINARY";
                default:
                    return "VARCHAR";
            }
        }
    }
}
