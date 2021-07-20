package com.dlink.metadata.convert;

import com.dlink.metadata.rules.IColumnType;
import com.dlink.model.Column;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ITypeConvert
 *
 * @author wenmo
 * @since 2021/7/20 14:39
 **/
public interface ITypeConvert {

    default IColumnType convert(Column column) {
        return convert(column.getType());
    }

    default String convertToDB(Column column) {
        return convertToDB(column.getJavaType());
    }

    IColumnType convert(String columnType);

    String convertToDB(String columnType);

    default Object convertValue(ResultSet results, String columnName, String javaType) throws SQLException {
        switch (javaType.toLowerCase()) {
            case "string":
                return results.getString(columnName);
            case "double":
                return results.getDouble(columnName);
            case "int":
                return results.getInt(columnName);
            case "float":
                return results.getFloat(columnName);
            case "decimal":
                return results.getBigDecimal(columnName);
            case "date":
            case "localdate":
                return results.getDate(columnName);
            case "time":
            case "localtime":
                return results.getTime(columnName);
            case "timestamp":
                return results.getTimestamp(columnName);
            case "blob":
                return results.getBlob(columnName);
            case "boolean":
                return results.getBoolean(columnName);
            case "byte":
                return results.getByte(columnName);
            case "bytes":
                return results.getBytes(columnName);
            default:
                return results.getString(columnName);
        }
    }
}
