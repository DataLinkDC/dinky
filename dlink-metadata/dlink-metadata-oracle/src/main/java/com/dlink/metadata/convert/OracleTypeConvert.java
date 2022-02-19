package com.dlink.metadata.convert;

import com.dlink.assertion.Asserts;
import com.dlink.model.Column;
import com.dlink.model.ColumnType;

/**
 * OracleTypeConvert
 *
 * @author wenmo
 * @since 2021/7/21 16:00
 **/
public class OracleTypeConvert implements ITypeConvert {
    @Override
    public ColumnType convert(Column column) {
        if (Asserts.isNull(column)) {
            return ColumnType.STRING;
        }
        String t = column.getType().toLowerCase();
        if (t.contains("char")) {
            return ColumnType.STRING;
        } else if (t.contains("date")) {
            return ColumnType.DATE;
        } else if (t.contains("timestamp")) {
            return ColumnType.TIMESTAMP;
        } else if (t.contains("number")) {
            if (t.matches("number\\(+\\d\\)")) {
                return ColumnType.INTEGER;
            } else if (t.matches("number\\(+\\d{2}+\\)")) {
                return ColumnType.LONG;
            }
            return ColumnType.DECIMAL;
        } else if (t.contains("float")) {
            return ColumnType.FLOAT;
        } else if (t.contains("clob")) {
            return ColumnType.STRING;
        } else if (t.contains("blob")) {
            return ColumnType.BYTES;
        }
        return ColumnType.STRING;
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case DATE:
                return "date";
            case TIMESTAMP:
                return "timestamp";
            case INTEGER:
            case LONG:
            case DECIMAL:
                return "number";
            case FLOAT:
                return "float";
            case BYTES:
                return "blob";
            default:
                return "varchar";
        }
    }
}
