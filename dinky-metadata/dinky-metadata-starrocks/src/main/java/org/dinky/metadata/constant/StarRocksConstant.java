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

/**
 * StarRocksConstant
 **/
public interface StarRocksConstant {

    /**
     * 查询所有database
     */
    String QUERY_ALL_DATABASE = " show databases ";
    /**
     * 查询所有schema下的所有表
     */
    String QUERY_TABLE_BY_SCHEMA = " select TABLE_NAME AS `NAME`,TABLE_SCHEMA AS `SCHEMA`,TABLE_COMMENT AS COMMENT, '' as TYPE, '' as CATALOG, "
            + "'' as ENGINE , '' as OPTIONS , 0 as `ROWS`, null as CREATE_TIME, null as UPDATE_TIME from information_schema.tables where TABLE_SCHEMA = '%s'  ";
    /**
     * 查询指定schema.table下的所有列信息
     */
    String QUERY_COLUMNS_BY_TABLE_AND_SCHEMA = "  show full columns from `%s`.`%s` ";
}
