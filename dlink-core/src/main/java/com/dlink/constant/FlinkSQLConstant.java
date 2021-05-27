package com.dlink.constant;

/**
 * FlinkSQLConstant
 *
 * @author wenmo
 * @since 2021/5/25 15:51
 **/
public interface FlinkSQLConstant {
    /**
     * 创建
     */
    String CREATE = "CREATE";
    /**
     * 删除
     */
    String DROP = "DROP";
    /**
     * 插入
     */
    String INSERT = "INSERT";
    /**
     * 修改
     */
    String ALTER = "ALTER";
    /**
     * 查询
     */
    String SELECT = "SELECT";
    /**
     * show操作
     */
    String SHOW = "SHOW";
    /**
     * 未知操作类型
     */
    String UNKNOWN_TYPE = "UNKNOWN TYPE";
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
}
