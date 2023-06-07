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
import org.dinky.cdc.sql.SQLSinkBuilder;
import org.dinky.cdc.sql.catalog.SQLCatalogSinkBuilder;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.exception.FlinkClientException;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

public class SinkBuilderFactory {

    private SinkBuilderFactory() {}

    private static final Map<String, Supplier<SinkBuilder>> SINK_BUILDER_MAP =
            ImmutableMap.of(
                    SQLSinkBuilder.KEY_WORD, SQLSinkBuilder::new,
                    SQLCatalogSinkBuilder.KEY_WORD, SQLCatalogSinkBuilder::new);

    public static SinkBuilder buildSinkBuilder(FlinkCDCConfig config) {
        if (Asserts.isNull(config) || Asserts.isNullString(config.getSink().get("connector"))) {
            throw new FlinkClientException("请指定 Sink connector。");
        }
        return SINK_BUILDER_MAP
                .getOrDefault(config.getSink().get("connector"), SQLSinkBuilder::new)
                .get()
                .create(config);
    }
}
