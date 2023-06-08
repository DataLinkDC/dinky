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

package org.dinky.cdc;

import org.dinky.assertion.Asserts;
import org.dinky.cdc.mysql.MysqlCDCBuilder;
import org.dinky.cdc.oracle.OracleCDCBuilder;
import org.dinky.cdc.postgres.PostgresCDCBuilder;
import org.dinky.cdc.sqlserver.SqlServerCDCBuilder;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.exception.FlinkClientException;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

public class CDCBuilderFactory {
    private CDCBuilderFactory() {}

    private static final Map<String, Supplier<CDCBuilder>> CDC_BUILDER_MAP =
            ImmutableMap.<String, Supplier<CDCBuilder>>builder()
                    .put(MysqlCDCBuilder.KEY_WORD, MysqlCDCBuilder::new)
                    .put(OracleCDCBuilder.KEY_WORD, OracleCDCBuilder::new)
                    .put(PostgresCDCBuilder.KEY_WORD, PostgresCDCBuilder::new)
                    .put(SqlServerCDCBuilder.KEY_WORD, SqlServerCDCBuilder::new)
                    .build();

    public static CDCBuilder buildCDCBuilder(FlinkCDCConfig config) {
        if (Asserts.isNull(config) || Asserts.isNullString(config.getType())) {
            throw new FlinkClientException("set CDC Source type。");
        }

        Supplier<CDCBuilder> cdcBuilderSupplier = CDC_BUILDER_MAP.get(config.getType());
        if (cdcBuilderSupplier == null) {
            throw new FlinkClientException("mismatched CDC Source type[" + config.getType() + "].");
        }
        return cdcBuilderSupplier.get().create(config);
    }
}
