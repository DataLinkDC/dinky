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

import org.dinky.sql.FlinkQuery;

/**
 * FlinkSQLConstant
 *
 * @since 2021/5/25 15:51
 */
public class FlinkSQLConstant {
    private FlinkSQLConstant() {}

    /** 分隔符 */
    public static final String SEPARATOR = FlinkQuery.separator();
    /** DDL 类型 */
    public static final String DDL = "DDL";
    /** DML 类型 */
    public static final String DML = "DML";
    /** DATASTREAM 类型 */
    public static final String DATASTREAM = "DATASTREAM";

    /** The define identifier of FlinkSQL Variable */
    public static final String VARIABLES = ":=";
}
