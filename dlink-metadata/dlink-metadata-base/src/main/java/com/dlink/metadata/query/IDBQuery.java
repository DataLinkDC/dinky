package com.dlink.metadata.query;

/**
 * IDBQuery
 *
 * @author wenmo
 * @since 2021/7/20 13:44
 **/
public interface IDBQuery {
    /**
     * 所有数据库信息查询 SQL
     */
    String schemaAllSql();

    /**
     * 表信息查询 SQL
     */
    String tablesSql(String schemaName);

    /**
     * 表字段信息查询 SQL
     */
    String columnsSql(String schemaName, String tableName);

    /**
     * 建表 SQL
     */
    String createTableSql(String schemaName, String tableName);

    /**
     * 建表语句列名
     */
    String createTableName();

    /**
     * 数据库、模式、组织名称
     */
    String schemaName();

    /**
     * catalog 名称
     */
    String catalogName();

    /**
     * 表名称
     */
    String tableName();

    /**
     * 表注释
     */
    String tableComment();

    /**
     * 表类型
     */
    String tableType();

    /**
     * 表引擎
     */
    String engine();

    /**
     * 表配置
     */
    String options();

    /**
     * 表记录数
     */
    String rows();

    /**
     * 创建时间
     */
    String createTime();

    /**
     * 更新时间
     */
    String updateTime();

    /**
     * 字段名称
     */
    String columnName();

    /**
     * 字段序号
     */
    String columnPosition();

    /**
     * 字段类型
     */
    String columnType();

    /**
     * 字段注释
     */
    String columnComment();

    /**
     * 主键字段
     */
    String columnKey();

    /**
     * 主键自增
     */
    String autoIncrement();

    /**
     * 默认值
     */
    String defaultValue();

    /**
     * @return 是否允许为 NULL
     */
    String isNullable();

    /**
     * @return 精度
     */
    String precision();

    /**
     * @return 小数范围
     */
    String scale();

    /**
     * @return 字符集名称
     */
    String characterSet();

    /**
     * @return 排序规则
     */
    String collation();

    /**
     * 自定义字段名称
     */
    String[] columnCustom();

    /**
     * @return 主键值
     */
    String isPK();
}
