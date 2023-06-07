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

package org.dinky.data.enums;

/** 分库分表的类型 */
public enum TableType {

    /** 分库分表 */
    SPLIT_DATABASE_AND_TABLE,
    /** 分表单库 */
    SPLIT_DATABASE_AND_SINGLE_TABLE,
    /** 单库分表 */
    SINGLE_DATABASE_AND_SPLIT_TABLE
    /** 单库单表 */
    ,
    SINGLE_DATABASE_AND_TABLE;

    public static TableType type(boolean splitDatabase, boolean splitTable) {
        if (splitDatabase && splitTable) {
            return TableType.SPLIT_DATABASE_AND_TABLE;
        }

        if (splitTable) {
            return TableType.SINGLE_DATABASE_AND_SPLIT_TABLE;
        }

        if (splitDatabase) {
            return TableType.SPLIT_DATABASE_AND_SINGLE_TABLE;
        }

        return TableType.SINGLE_DATABASE_AND_TABLE;
    }
}
