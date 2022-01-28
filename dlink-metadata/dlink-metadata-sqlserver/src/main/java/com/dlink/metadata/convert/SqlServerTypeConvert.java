package com.dlink.metadata.convert;

import com.dlink.metadata.rules.DbColumnType;
import com.dlink.metadata.rules.IColumnType;

/**
 *
 * @operate 
 * @date 2022/1/26 14:23
 * @return 
 */
public class SqlServerTypeConvert implements ITypeConvert {
    @Override
    public IColumnType convert(String columnType) {
        String t = columnType.toLowerCase();
        if (t.contains("char") || t.contains("varchar") || t.contains("text") ||
        t.contains("nchar") || t.contains("nvarchar") || t.contains("ntext")
        || t.contains("uniqueidentifier") || t.contains("sql_variant")) {
            return DbColumnType.STRING;
        } else if(t.contains("bit")){
            return DbColumnType.BOOLEAN;
        }else if(t.contains("int") || t.contains("tinyint") || t.contains("smallint")){
            return DbColumnType.INTEGER;
        }else if(t.contains("bigint")){
            return DbColumnType.LONG;
        }else if(t.contains("float") ){
            return DbColumnType.DOUBLE;
        }else if(t.contains("decimal") || t.contains("money") || t.contains("smallmoney")
        || t.contains("numeric")){
            return DbColumnType.BIG_DECIMAL;
        }else if(t.contains("real")){
            return DbColumnType.FLOAT;
        }else if(t.contains("smalldatetime") || t.contains("datetime")){
            return DbColumnType.TIMESTAMP;
        }else if(t.contains("timestamp") || t.contains("binary") || t.contains("varbinary") || t.contains("image")){
            return DbColumnType.BYTE_ARRAY;
        }
        return DbColumnType.STRING;
    }

    @Override
    public String convertToDB(String columnType) {
        switch (columnType.toLowerCase()) {
            case "string":
                return "varchar(255)";
            case "boolean":
                return "tinyint(1)";
            case "int":
            case "integer":
                return "int";
            case "double":
                return "double";
            case "float":
                return "float";
            case "date":
                return "datetime(0)";
            default:
                return "varchar(255)";
        }
    }
}
