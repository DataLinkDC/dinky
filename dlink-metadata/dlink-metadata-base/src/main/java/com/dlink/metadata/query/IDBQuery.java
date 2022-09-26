/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
     * 建视图语句列名
     */
    String createViewName();

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

    /**
     * @return 允许为空的值
     */
    String nullableValue();
}
