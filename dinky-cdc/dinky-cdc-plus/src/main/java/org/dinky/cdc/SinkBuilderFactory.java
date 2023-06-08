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

import com.google.common.collect.ImmutableMap;
import org.dinky.assertion.Asserts;
import org.dinky.cdc.doris.DorisExtendSinkBuilder;
import org.dinky.cdc.doris.DorisSchemaEvolutionSinkBuilder;
import org.dinky.cdc.doris.DorisSinkBuilder;
import org.dinky.cdc.kafka.KafkaSinkBuilder;
import org.dinky.cdc.sql.SQLSinkBuilder;
import org.dinky.cdc.sql.catalog.SQLCatalogSinkBuilder;
import org.dinky.exception.FlinkClientException;
import org.dinky.data.model.FlinkCDCConfig;

import java.util.Map;
import java.util.function.Supplier;

public class SinkBuilderFactory {

    private SinkBuilderFactory() {
    }

    private static final Map<String, Supplier<SinkBuilder>> SINK_BUILDER_MAP =
            ImmutableMap.<String, Supplier<SinkBuilder>>builder()
                    .put(SQLSinkBuilder.KEY_WORD, SQLSinkBuilder::new)
                    .put(SQLCatalogSinkBuilder.KEY_WORD, SQLCatalogSinkBuilder::new)
                    .put(KafkaSinkBuilder.KEY_WORD, KafkaSinkBuilder::new)
                    .put(DorisSinkBuilder.KEY_WORD, DorisSinkBuilder::new)
                    .put(DorisExtendSinkBuilder.KEY_WORD, DorisExtendSinkBuilder::new)
                    .put(DorisSchemaEvolutionSinkBuilder.KEY_WORD, DorisSchemaEvolutionSinkBuilder::new)
                    .build();

    public static SinkBuilder buildSinkBuilder(FlinkCDCConfig config) {
        if (Asserts.isNull(config) || Asserts.isNullString(config.getSink().get("connector"))) {
            throw new FlinkClientException("Set Sink connector。");
        }
        return SINK_BUILDER_MAP
                .getOrDefault(config.getSink().get("connector"), SQLSinkBuilder::new)
                .get()
                .create(config);
    }
}
