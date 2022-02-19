package com.dlink.metadata.convert;

import com.dlink.assertion.Asserts;
import com.dlink.model.Column;
import com.dlink.model.ColumnType;

/**
 * MySqlTypeConvert
 *
 * @author wenmo
 * @since 2021/7/20 15:21
 **/
public class MySqlTypeConvert implements ITypeConvert {
    @Override
    public ColumnType convert(Column column) {
        if (Asserts.isNull(column)) {
            return ColumnType.STRING;
        }
        String t = column.getType().toLowerCase();
        if (t.contains("tinyint")) {
            return ColumnType.BYTE;
        } else if (t.contains("smallint") || t.contains("tinyint unsigned")) {
            return ColumnType.SHORT;
        } else if (t.contains("bigint unsigned") || t.contains("numeric") || t.contains("decimal")) {
            return ColumnType.DECIMAL;
        } else if (t.contains("bigint") || t.contains("int unsigned")) {
            return ColumnType.LONG;
        } else if (t.contains("float")) {
            return ColumnType.FLOAT;
        } else if (t.contains("double")) {
            return ColumnType.DOUBLE;
        } else if (t.contains("boolean") || t.contains("tinyint(1)")) {
            return ColumnType.BOOLEAN;
        } else if (t.contains("datetime")) {
            return ColumnType.TIMESTAMP;
        } else if (t.contains("date")) {
            return ColumnType.DATE;
        } else if (t.contains("time")) {
            return ColumnType.TIME;
        } else if (t.contains("char") || t.contains("text")) {
            return ColumnType.STRING;
        } else if (t.contains("binary") || t.contains("blob")) {
            return ColumnType.BYTES;
        } else if (t.contains("int") || t.contains("mediumint") || t.contains("smallint unsigned")) {
            return ColumnType.INTEGER;
        }
        return ColumnType.STRING;
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case BYTE:
                return "tinyint";
            case SHORT:
                return "smallint";
            case DECIMAL:
                return "decimal";
            case LONG:
                return "bigint";
            case FLOAT:
                return "float";
            case DOUBLE:
                return "double";
            case BOOLEAN:
                return "boolean";
            case TIMESTAMP:
                return "datetime";
            case DATE:
                return "date";
            case TIME:
                return "time";
            case BYTES:
                return "binary";
            case INTEGER:
                return "int";
            default:
                return "varchar";
        }
    }
}
