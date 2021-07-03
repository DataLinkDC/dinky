package com.dlink.constant;

/**
 * FlinkSQLConstant
 *
 * @author wenmo
 * @since 2021/5/25 15:51
 **/
public interface FlinkSQLConstant {
    /**
     * 查询
     */
    String SELECT = "SELECT";
    /**
     * 创建
     */
    String CREATE = "CREATE";
    /**
     * 删除
     */
    String DROP = "DROP";
    /**
     * 修改
     */
    String ALTER = "ALTER";
    /**
     * 插入
     */
    String INSERT = "INSERT";
    /**
     * DESCRIBE
     */
    String DESCRIBE = "DESCRIBE";
    /**
     * EXPLAIN
     */
    String EXPLAIN = "EXPLAIN";
    /**
     * USE
     */
    String USE = "USE";
    /**
     * SHOW
     */
    String SHOW = "SHOW";
    /**
     * LOAD
     */
    String LOAD = "LOAD";
    /**
     * UNLOAD
     */
    String UNLOAD = "UNLOAD";
    /**
     * SET
     */
    String SET = "SET";
    /**
     * RESET
     */
    String RESET = "RESET";
    /**
     * 未知操作类型
     */
    String UNKNOWN = "UNKNOWN";
    /**
     * 查询时null对应的值
     */
    String NULL_COLUMNS = "";
    /**
     * 创建聚合表 CREATEAGGTABLE
     */
    String CREATE_AGG_TABLE = "CREATEAGGTABLE";
    /**
     * 删除表语句的头部 DROP TABLE IF EXISTS
     */
    String DROP_TABLE_HEAD = " DROP TABLE IF EXISTS ";
    /**
     * 分隔符
     */
    String SEPARATOR = ";";
}
