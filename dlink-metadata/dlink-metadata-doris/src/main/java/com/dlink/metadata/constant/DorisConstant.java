package com.dlink.metadata.constant;

public interface DorisConstant {

    /**
     * 查询所有database
     */
    String QUERY_ALL_DATABASE = " show databases ";
    /**
     * 查询所有schema下的所有表
     */
    String QUERY_TABLE_BY_SCHEMA = " select TABLE_NAME AS `NAME`,TABLE_SCHEMA AS `SCHEMA`,TABLE_COMMENT AS COMMENT, '' as TYPE, '' as CATALOG, '' as ENGINE , '' as OPTIONS , 0 as `ROWS`, null as CREATE_TIME, null as UPDATE_TIME from information_schema.tables where TABLE_SCHEMA = '%s'  ";
    /**
     * 查询指定schema.table下的所有列信息
     */
    String QUERY_COLUMNS_BY_TABLE_AND_SCHEMA = "  show full columns from `%s`.`%s` ";
}
