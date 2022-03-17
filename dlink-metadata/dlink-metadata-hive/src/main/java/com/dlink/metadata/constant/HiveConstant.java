package com.dlink.metadata.constant;

public interface HiveConstant {

    /**
     * 查询所有database
     */
    String QUERY_ALL_DATABASE = " show databases";
    /**
     * 查询所有schema下的所有表
     */
    String QUERY_ALL_TABLES_BY_SCHEMA = "show tables";
    /**
     * 扩展信息Key
     */
    String DETAILED_TABLE_INFO = "Detailed Table Information";
    /**
     * 查询指定schema.table的扩展信息
     */
    String QUERY_TABLE_SCHEMA_EXTENED_INFOS = " describe extended `%s`.`%s`";
    /**
     * 查询指定schema.table的信息 列 列类型 列注释
     */
    String QUERY_TABLE_SCHEMA = " describe `%s`.`%s`";
    /**
     * 使用 DB
     */
    String USE_DB = "use `%s`";
    /**
     * 只查询指定schema.table的列名
     */
    String QUERY_TABLE_COLUMNS_ONLY = "show columns in `%s`.`%s`";
}
