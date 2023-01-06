package com.dlink.model;

/**
 * 分库分表的类型
 */
public enum TableType {
    /**
     * 分库分表
     */
    SPLIT_DATABASE_AND_TABLE,
    /**
     * 分表单库
     */
    SPLIT_DATABASE_AND_SINGLE_TABLE,
    /**
     * 单库分表
     */
    SINGLE_DATABASE_AND_SPLIT_TABLE
    /**
     * 单库单表
     */
    , SINGLE_DATABASE_AND_TABLE;

    public static TableType type(boolean splitDatabase, boolean splitTable) {
        if (splitDatabase && splitTable) {
            return TableType.SPLIT_DATABASE_AND_TABLE;
        }
        if (splitTable) {
            return TableType.SINGLE_DATABASE_AND_SPLIT_TABLE;
        }
        if (!splitDatabase) {
            return TableType.SINGLE_DATABASE_AND_TABLE;
        }
        return TableType.SPLIT_DATABASE_AND_SINGLE_TABLE;
    }
}
