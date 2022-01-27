package com.dlink.metadata.convert;

import com.dlink.metadata.rules.DbColumnType;
import com.dlink.metadata.rules.IColumnType;

/**
 * ClickHouseTypeConvert
 *
 * @author wenmo
 * @since 2021/7/21 17:15
 **/
public class ClickHouseTypeConvert implements ITypeConvert {

  @Override
  public IColumnType convert(String columnType) {
    switch (columnType) {
      case "UInt8":
        return DbColumnType.BOOLEAN;
      case "Int8":
        return DbColumnType.BYTE;
      case "Int16":
      case "Int32":
        return DbColumnType.INTEGER;
      case "Int64":
        return DbColumnType.BIG_INTEGER;
      case "Float32":
        return DbColumnType.FLOAT;
      case "Float64":
      case "Decimal":
        return DbColumnType.DOUBLE;
      case "Date":
        return DbColumnType.DATE;
      case "DateTime":
        return DbColumnType.TIMESTAMP;
      case "String":
      default:
        return DbColumnType.STRING;
    }
  }

  @Override
  public String convertToDB(String columnType) {
    return null;
  }

}
