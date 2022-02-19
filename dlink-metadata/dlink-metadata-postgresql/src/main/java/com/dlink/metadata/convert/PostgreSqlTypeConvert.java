package com.dlink.metadata.convert;

import com.dlink.assertion.Asserts;
import com.dlink.model.Column;
import com.dlink.model.ColumnType;

/**
 * PostgreSqlTypeConvert
 *
 * @author wenmo
 * @since 2021/7/22 9:33
 **/
public class PostgreSqlTypeConvert implements ITypeConvert {
    @Override
    public ColumnType convert(Column column) {
        if (Asserts.isNull(column)) {
            return ColumnType.STRING;
        }
        String t = column.getType().toLowerCase();
        if (t.contains("smallint") || t.contains("int2") || t.contains("smallserial") || t.contains("serial2")) {
            return ColumnType.SHORT;
        } else if (t.contains("integer") || t.contains("serial")) {
            return ColumnType.INTEGER;
        } else if (t.contains("bigint") || t.contains("bigserial")) {
            return ColumnType.LONG;
        } else if (t.contains("real") || t.contains("float4")) {
            return ColumnType.FLOAT;
        } else if (t.contains("float8") || t.contains("double precision")) {
            return ColumnType.DOUBLE;
        } else if (t.contains("numeric") || t.contains("decimal")) {
            return ColumnType.DECIMAL;
        } else if (t.contains("boolean")) {
            return ColumnType.BOOLEAN;
        } else if (t.contains("timestamp")) {
            return ColumnType.TIMESTAMP;
        } else if (t.contains("date")) {
            return ColumnType.DATE;
        } else if (t.contains("time")) {
            return ColumnType.TIME;
        } else if (t.contains("char") || t.contains("text")) {
            return ColumnType.STRING;
        } else if (t.contains("bytea")) {
            return ColumnType.BYTES;
        } else if (t.contains("array")) {
            return ColumnType.T;
        }
        return ColumnType.STRING;
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case SHORT:
                return "int2";
            case INTEGER:
                return "integer";
            case LONG:
                return "bigint";
            case FLOAT:
                return "float4";
            case DOUBLE:
                return "float8";
            case DECIMAL:
                return "decimal";
            case BOOLEAN:
                return "boolean";
            case TIMESTAMP:
                return "timestamp";
            case DATE:
                return "date";
            case TIME:
                return "time";
            case BYTES:
                return "bytea";
            case T:
                return "array";
            default:
                return "varchar";
        }
    }
}
