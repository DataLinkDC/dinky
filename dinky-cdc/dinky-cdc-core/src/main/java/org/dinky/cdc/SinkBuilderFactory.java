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
import org.dinky.cdc.kafka.KafkaSinkBuilder;
import org.dinky.cdc.sql.SQLSinkBuilder;
import org.dinky.cdc.sql.catalog.SQLCatalogSinkBuilder;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.exception.FlinkClientException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinkBuilderFactory {
    private static final Logger logger = LoggerFactory.getLogger(SinkBuilderFactory.class);

    private SinkBuilderFactory() {}

    private static final Map<String, Supplier<SinkBuilder>> SINK_BUILDER_MAP = getPlusSinkBuilder();

    public static SinkBuilder buildSinkBuilder(FlinkCDCConfig config) {

        if (Asserts.isNull(config) || Asserts.isNullString(config.getSink().get("connector"))) {
            throw new FlinkClientException("set Sink connector.");
        }
        return SINK_BUILDER_MAP
                .getOrDefault(config.getSink().get("connector"), SQLSinkBuilder::new)
                .get()
                .create(config);
    }

    private static Map<String, Supplier<SinkBuilder>> getPlusSinkBuilder() {
        Map<String, Supplier<SinkBuilder>> map = new HashMap<>();
        map.put(SQLSinkBuilder.KEY_WORD, SQLSinkBuilder::new);
        map.put(SQLCatalogSinkBuilder.KEY_WORD, SQLCatalogSinkBuilder::new);
        map.put(KafkaSinkBuilder.KEY_WORD, KafkaSinkBuilder::new);

        final ServiceLoader<SinkBuilder> loader = ServiceLoader.load(SinkBuilder.class);

        final List<SinkBuilder> sinkBuilders = new ArrayList<>();
        for (SinkBuilder factory : loader) {
            sinkBuilders.add(factory);
        }

        Map<String, Supplier<SinkBuilder>> plusSinkBuilder =
                sinkBuilders.stream().collect(Collectors.toMap(SinkBuilderFactory::getKeyWord, x -> () -> x));
        map.putAll(plusSinkBuilder);
        return map;
    }

    public static String getKeyWord(SinkBuilder c) {
        String fieldName = "KEY_WORD";
        String result = null;
        try {
            Field f = c.getClass().getDeclaredField(fieldName);
            result = (String) f.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            logger.error("Could not find KEY_WORD in class : {}", e.getMessage());
        }

        return result;
    }
}
