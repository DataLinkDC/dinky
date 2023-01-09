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

package org.dinky.metadata.constant;

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
