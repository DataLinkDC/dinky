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

/** phoenix constant */
public interface PhoenixConstant {

    /** 不指定schema列信息模板SQL */
    String QUERY_COLUMNS_SQL_DEFAULT = " select COLUMN_NAME,COLUMN_FAMILY,DATA_TYPE,KEY_SEQ,NULLABLE, '' as"
            + " CHARACTER_SET_NAME, '' as COLLATION_NAME ,'' as ORDINAL_POSITION , 0 as"
            + " NUMERIC_PRECISION, 0 as NUMERIC_SCALE, '' as AUTO_INCREMENT from"
            + " SYSTEM.CATALOG where TABLE_NAME='%s' and COLUMN_NAME is not null ";
    /** 查询默认指定列信息模板SQL */
    String QUERY_COLUMNS_SQL = QUERY_COLUMNS_SQL_DEFAULT + "  AND TABLE_SCHEM = '%s' ";

    /** 查询schema模板SQL */
    String QUERY_SCHEMA_SQL = " select distinct(TABLE_SCHEM) as TABLE_SCHEM from SYSTEM.CATALOG where TABLE_SCHEM is"
            + " not null and TABLE_SCHEM <> 'SYSTEM' ";

    /** 不指定schema查询table信息模板SQL */
    String QUERY_TABLE_BY_SCHEMA_SQL_DEFAULT =
            " select TABLE_NAME,TABLE_SCHEM,TABLE_TYPE,SCOPE_CATALOG as CATALOG,'' as ENGINE,'' as"
                    + " OPTIONS, 0 as ROWSNUM, null as CREATE_TIME, null as UPDATE_TIME from"
                    + " SYSTEM.CATALOG where TABLE_TYPE in ('u','v')  ";
    /** 根据schema查询table信息模板SQL */
    String QUERY_TABLE_BY_SCHEMA_SQL = QUERY_TABLE_BY_SCHEMA_SQL_DEFAULT + "  AND TABLE_SCHEM = '%s' ";

    /** Phoenix的driver */
    String PHOENIX_DRIVER = "org.apache.phoenix.jdbc.PhoenixDriver";
}
