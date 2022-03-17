package com.dlink.metadata.convert;

import com.dlink.assertion.Asserts;
import com.dlink.model.Column;
import com.dlink.model.ColumnType;

public class HiveTypeConvert implements ITypeConvert {
    @Override
    public ColumnType convert(Column column) {
        if (Asserts.isNull(column)) {
            return ColumnType.STRING;
        }
        String t = column.getType().toLowerCase().trim();
        if (t.contains("char")) {
            return ColumnType.STRING;
        } else if (t.contains("boolean")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_BOOLEAN;
            }
            return ColumnType.BOOLEAN;
        } else if (t.contains("tinyint")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_BYTE;
            }
            return ColumnType.BYTE;
        } else if (t.contains("smallint")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_SHORT;
            }
            return ColumnType.SHORT;
        } else if (t.contains("bigint")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_LONG;
            }
            return ColumnType.LONG;
        } else if (t.contains("largeint")) {
            return ColumnType.STRING;
        } else if (t.contains("int")) {
            if (column.isNullable()) {
                return ColumnType.INTEGER;
            }
            return ColumnType.INT;
        } else if (t.contains("float")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_FLOAT;
            }
            return ColumnType.FLOAT;
        } else if (t.contains("double")) {
            if (column.isNullable()) {
                return ColumnType.JAVA_LANG_DOUBLE;
            }
            return ColumnType.DOUBLE;
        } else if (t.contains("date")) {
            return ColumnType.STRING;
        } else if (t.contains("datetime")) {
            return ColumnType.STRING;
        } else if (t.contains("decimal")) {
            return ColumnType.DECIMAL;
        } else if (t.contains("time")) {
            return ColumnType.DOUBLE;
        }
        return ColumnType.STRING;
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return "boolean";
            case BYTE:
            case JAVA_LANG_BYTE:
                return "tinyint";
            case SHORT:
            case JAVA_LANG_SHORT:
                return "smallint";
            case LONG:
            case JAVA_LANG_LONG:
                return "bigint";
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return "float";
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return "double";
            case DECIMAL:
                return "decimal";
            case INT:
            case INTEGER:
                return "int";
            default:
                return "varchar";
        }
    }
}
