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
        ColumnType columnType = ColumnType.STRING;
        if (Asserts.isNull(column)) {
            return columnType;
        }
        String t = column.getType().toLowerCase();
        if (t.contains("numeric") || t.contains("decimal")) {
            columnType = ColumnType.DECIMAL;
        } else if (t.contains("bigint")) {
            columnType = ColumnType.LONG;
        } else if (t.contains("float")) {
            columnType = ColumnType.FLOAT;
        } else if (t.contains("double")) {
            columnType = ColumnType.DOUBLE;
        } else if (t.contains("boolean") || t.contains("tinyint(1)")) {
            columnType = ColumnType.BOOLEAN;
        } else if (t.contains("datetime")) {
            columnType = ColumnType.TIMESTAMP;
        } else if (t.contains("date")) {
            columnType = ColumnType.DATE;
        } else if (t.contains("timestamp")) {
            columnType = ColumnType.TIMESTAMP;
        } else if (t.contains("time")) {
            columnType = ColumnType.TIME;
        } else if (t.contains("char") || t.contains("text")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("binary") || t.contains("blob")) {
            columnType = ColumnType.BYTES;
        } else if (t.contains("tinyint") || t.contains("mediumint") || t.contains("smallint") || t.contains("int") ) {
            columnType = ColumnType.INTEGER;
        }
        columnType.setPrecisionAndScale(column.getPrecision(), column.getScale());
        return columnType;
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
