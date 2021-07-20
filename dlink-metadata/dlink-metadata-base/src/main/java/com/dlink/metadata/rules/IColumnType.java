package com.dlink.metadata.rules;

/**
 * IColumnType
 *
 * @author wenmo
 * @since 2021/7/20 14:43
 **/
public interface IColumnType {
    /**
     * 获取字段类型
     */
    String getType();
    /**
     * 获取字段类型完整名
     */
    String getPkg();
}
