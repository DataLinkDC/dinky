package com.dlink.metadata.query;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    String columnsSql(String schemaName,String tableName);
    /**
     * 数据库、模式、组织名称
     */
    String schemaName();
    /**
     * 表名称
     */
    String tableName();
    /**
     * 表注释
     */
    String tableComment();
    /**
     * 字段名称
     */
    String columnName();
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
     * 判断主键是否为identity，目前仅对mysql进行检查
     *
     * @param results ResultSet
     * @return 主键是否为identity
     * @throws SQLException ignore
     */
    boolean isKeyIdentity(ResultSet results) throws SQLException;
    /**
     * 判断字段是否不为null，目前仅对mysql进行检查
     *
     * @return 主键是否不为bull
     */
    String isNotNull();
    /**
     * 自定义字段名称
     */
    String[] columnCustom();
}
