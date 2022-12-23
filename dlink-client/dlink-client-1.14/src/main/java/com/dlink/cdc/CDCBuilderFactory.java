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

package com.dlink.cdc;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.mysql.MysqlCDCBuilder;
import com.dlink.cdc.oracle.OracleCDCBuilder;
import com.dlink.cdc.postgres.PostgresCDCBuilder;
import com.dlink.cdc.sqlserver.SqlServerCDCBuilder;
import com.dlink.exception.FlinkClientException;
import com.dlink.model.FlinkCDCConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * CDCBuilderFactory
 *
 * @author wenmo
 * @since 2022/4/12 21:12
 **/
public class CDCBuilderFactory {

    private static final Map<String, Supplier<CDCBuilder>> CDC_BUILDER_MAP = new HashMap<String, Supplier<CDCBuilder>>() {
        {
            put(MysqlCDCBuilder.KEY_WORD, () -> new MysqlCDCBuilder());
            put(OracleCDCBuilder.KEY_WORD, () -> new OracleCDCBuilder());
            put(SqlServerCDCBuilder.KEY_WORD, () -> new SqlServerCDCBuilder());
            put(PostgresCDCBuilder.KEY_WORD, () -> new PostgresCDCBuilder());
        }
    };

    public static CDCBuilder buildCDCBuilder(FlinkCDCConfig config) {
        if (Asserts.isNull(config) || Asserts.isNullString(config.getType())) {
            throw new FlinkClientException("请指定 CDC Source 类型。");
        }
        return CDC_BUILDER_MAP.getOrDefault(config.getType(), () -> {
            throw new FlinkClientException("未匹配到对应 CDC Source 类型的【" + config.getType() + "】。");
        }).get().create(config);
    }
}
