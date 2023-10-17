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

package org.dinky.constant;

/**
 * FlinkSQLConstant
 *
 * @since 2021/5/25 15:51
 */
public interface FlinkSQLConstant {

    /** 分隔符 */
    String SEPARATOR = ";\n";
    /** DDL 类型 */
    String DDL = "DDL";
    /** DML 类型 */
    String DML = "DML";
    /** DATASTREAM 类型 */
    String DATASTREAM = "DATASTREAM";

    /** The define identifier of FlinkSQL Variable */
    String VARIABLES = ":=";

    /** The define identifier of FlinkSQL Date Variable */
    String INNER_DATE_KEY = "_CURRENT_DATE_";

    /** The define identifier of FlinkSQL Timestamp Variable */
    String INNER_TIMESTAMP_KEY = "_CURRENT_TIMESTAMP_";

    /** 内置日期变量格式 确定后不能修改 */
    String INNER_DATE_FORMAT = "yyyy-MM-dd";
}
