package com.dlink.metadata.convert;

import com.dlink.assertion.Asserts;
import com.dlink.model.Column;
import com.dlink.model.ColumnType;

public class DorisTypeConvert implements ITypeConvert {
    @Override
    public ColumnType convert(Column column) {
        ColumnType columnType = ColumnType.STRING;
        if (Asserts.isNull(column)) {
            return columnType;
        }
        String t = column.getType().toLowerCase();
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        if (t.contains("char")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("boolean")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_BOOLEAN;
            } else {
                columnType = ColumnType.BOOLEAN;
            }
        } else if (t.contains("tinyint")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_BYTE;
            } else {
                columnType = ColumnType.BYTE;
            }
        } else if (t.contains("smallint")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_SHORT;
            } else {
                columnType = ColumnType.SHORT;
            }
        } else if (t.contains("bigint")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_LONG;
            } else {
                columnType = ColumnType.LONG;
            }
        } else if (t.contains("largeint")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("int")) {
            if (isNullable) {
                columnType = ColumnType.INTEGER;
            } else {
                columnType = ColumnType.INT;
            }
        } else if (t.contains("float")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_FLOAT;
            } else {
                columnType = ColumnType.FLOAT;
            }
        } else if (t.contains("double")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_DOUBLE;
            } else {
                columnType = ColumnType.DOUBLE;
            }
        } else if (t.contains("date")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("datetime")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("decimal")) {
            columnType = ColumnType.DECIMAL;
        } else if (t.contains("time")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_DOUBLE;
            } else {
                columnType = ColumnType.DOUBLE;
            }
        }
        columnType.setPrecisionAndScale(column.getPrecision(), column.getScale());
        return columnType;
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
