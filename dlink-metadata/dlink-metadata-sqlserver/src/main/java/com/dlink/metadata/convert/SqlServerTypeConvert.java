package com.dlink.metadata.convert;

import com.dlink.assertion.Asserts;
import com.dlink.model.Column;
import com.dlink.model.ColumnType;

/**
 * @operate
 * @date 2022/1/26 14:23
 * @return
 */
public class SqlServerTypeConvert implements ITypeConvert {
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
        } else if (t.contains("bit")) {
            return ColumnType.BOOLEAN;
        } else if (t.contains("int") || t.contains("tinyint") || t.contains("smallint")) {
            return ColumnType.INTEGER;
        } else if (t.contains("float")) {
            return ColumnType.DOUBLE;
        } else if (t.contains("decimal") || t.contains("money") || t.contains("smallmoney")
                || t.contains("numeric")) {
            return ColumnType.DECIMAL;
        } else if (t.contains("real")) {
            return ColumnType.FLOAT;
        } else if (t.contains("smalldatetime") || t.contains("datetime")) {
            return ColumnType.TIMESTAMP;
        } else if (t.contains("timestamp") || t.contains("binary") || t.contains("varbinary") || t.contains("image")) {
            return ColumnType.BYTES;
        }
        return ColumnType.STRING;
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case BOOLEAN:
                return "bit";
            case LONG:
                return "bigint";
            case INTEGER:
                return "int";
            case DOUBLE:
                return "double";
            case FLOAT:
                return "float";
            case TIMESTAMP:
                return "datetime(0)";
            default:
                return "varchar";
        }
    }
}
