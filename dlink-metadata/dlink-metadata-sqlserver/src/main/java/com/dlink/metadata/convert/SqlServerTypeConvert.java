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
        ColumnType columnType = ColumnType.STRING;
        if (Asserts.isNull(column)) {
            return columnType;
        }
        String t = column.getType().toLowerCase();
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        if (t.contains("char") || t.contains("varchar") || t.contains("text") ||
            t.contains("nchar") || t.contains("nvarchar") || t.contains("ntext")
            || t.contains("uniqueidentifier") || t.contains("sql_variant")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("bigint")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_LONG;
            } else {
                columnType = ColumnType.LONG;
            }
        } else if (t.contains("bit")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_BOOLEAN;
            } else {
                columnType = ColumnType.BOOLEAN;
            }
        } else if (t.contains("int") || t.contains("tinyint") || t.contains("smallint")) {
            if (isNullable) {
                columnType = ColumnType.INTEGER;
            } else {
                columnType = ColumnType.INT;
            }
        } else if (t.contains("float")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_DOUBLE;
            } else {
                columnType = ColumnType.DOUBLE;
            }
        } else if (t.contains("decimal") || t.contains("money") || t.contains("smallmoney")
            || t.contains("numeric")) {
            columnType = ColumnType.DECIMAL;
        } else if (t.contains("real")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_FLOAT;
            } else {
                columnType = ColumnType.FLOAT;
            }
        } else if (t.contains("smalldatetime") || t.contains("datetime")) {
            columnType = ColumnType.TIMESTAMP;
        } else if (t.contains("timestamp") || t.contains("binary") || t.contains("varbinary") || t.contains("image")) {
            columnType = ColumnType.BYTES;
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
                return "bit";
            case LONG:
            case JAVA_LANG_LONG:
                return "bigint";
            case INTEGER:
            case INT:
                return "int";
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return "double";
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return "float";
            case TIMESTAMP:
                return "datetime(0)";
            default:
                return "varchar";
        }
    }
}
