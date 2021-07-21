package com.dlink.metadata.convert;

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
        return null;
    }

    @Override
    public String convertToDB(String columnType) {
        return null;
    }
}
