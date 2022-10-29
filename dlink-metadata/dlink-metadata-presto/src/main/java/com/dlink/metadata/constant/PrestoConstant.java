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

package com.dlink.metadata.constant;

public interface PrestoConstant {

    /**
     * 查询所有database
     */
    String QUERY_ALL_DATABASE = "show catalogs";
    /**
     * 查询某个schema下的所有表
     */
    String QUERY_ALL_TABLES_BY_SCHEMA = "show tables from %s";
    /**
     * 查询指定schema.table的信息 列 列类型 列注释
     */
    String QUERY_TABLE_SCHEMA = " describe %s.%s";
    /**
     * 只查询指定schema.table的列名
     */
    String QUERY_TABLE_COLUMNS_ONLY = "show schemas from %s";
    /**
     * 查询schema列名
     */
    String SCHEMA = "SCHEMA";
    /**
     * 需要排除的catalog
     */
    String EXTRA_SCHEMA = "system";
    /**
     * 需要排除的schema
     */
    String EXTRA_DB = "information_schema";

}
