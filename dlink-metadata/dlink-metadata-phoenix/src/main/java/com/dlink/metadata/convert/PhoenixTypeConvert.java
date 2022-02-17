package com.dlink.metadata.convert;

import com.dlink.assertion.Asserts;
import com.dlink.model.Column;
import com.dlink.model.ColumnType;

public class PhoenixTypeConvert implements ITypeConvert {
    @Override
    public ColumnType convert(Column column) {
        if (Asserts.isNull(column)) {
            return ColumnType.STRING;
        }
        String t = column.getType().toLowerCase();
        if (t.contains("char") || t.contains("varchar") || t.contains("text") ||
                t.contains("nchar") || t.contains("nvarchar") || t.contains("ntext")
                || t.contains("uniqueidentifier") || t.contains("sql_variant")) {
            return ColumnType.STRING;
        } else if (t.contains("bigint")) {
            return ColumnType.LONG;
        } else if (t.contains("int") || t.contains("tinyint") || t.contains("smallint") || t.contains("integer")) {
            return ColumnType.INTEGER;
        } else if (t.contains("float")) {
            return ColumnType.FLOAT;
        } else if (t.contains("decimal") || t.contains("money") || t.contains("smallmoney")
                || t.contains("numeric")) {
            return ColumnType.DECIMAL;
        } else if (t.contains("double")) {
            return ColumnType.DOUBLE;
        } else if (t.contains("boolean")) {
            return ColumnType.BOOLEAN;
        } else if (t.contains("smalldatetime") || t.contains("datetime")) {
            return ColumnType.TIMESTAMP;
        } else if (t.contains("timestamp") || t.contains("binary") || t.contains("varbinary") || t.contains("image")) {
            return ColumnType.BYTES;
        } else if (t.contains("time")) {
            return ColumnType.TIME;
        } else if (t.contains("date")) {
            return ColumnType.DATE;
        }
        return ColumnType.STRING;
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case INTEGER:
                return "integer";
            case DOUBLE:
                return "double";
            case LONG:
                return "bigint";
            case FLOAT:
                return "float";
            case DECIMAL:
                return "decimal";
            case BOOLEAN:
                return "boolean";
            case TIME:
                return "time";
            case DATE:
                return "date";
            case TIMESTAMP:
                return "timestamp";
            case STRING:
                return "varchar";
            case BYTES:
                return "binary";
            default:
                return "varchar";
        }
    }
}
