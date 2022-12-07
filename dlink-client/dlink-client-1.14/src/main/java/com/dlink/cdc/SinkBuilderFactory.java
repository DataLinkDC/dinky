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
import com.dlink.cdc.doris.DorisExtendSinkBuilder;
import com.dlink.cdc.doris.DorisSchemaEvolutionSinkBuilder;
import com.dlink.cdc.doris.DorisSinkBuilder;
import com.dlink.cdc.kafka.KafkaSinkBuilder;
import com.dlink.cdc.kafka.KafkaSinkJsonBuilder;
import com.dlink.cdc.sql.SQLSinkBuilder;
import com.dlink.cdc.starrocks.StarrocksSinkBuilder;
import com.dlink.exception.FlinkClientException;
import com.dlink.model.FlinkCDCConfig;

/**
 * SinkBuilderFactory
 *
 * @author wenmo
 * @since 2022/4/12 21:12
 **/
public class SinkBuilderFactory {

    private static SinkBuilder[] sinkBuilders = {
            new KafkaSinkBuilder(),
            new KafkaSinkJsonBuilder(),
            new DorisSinkBuilder(),
            new StarrocksSinkBuilder(),
            new SQLSinkBuilder(),
            new DorisExtendSinkBuilder(),
            new DorisSchemaEvolutionSinkBuilder()
    };

    public static SinkBuilder buildSinkBuilder(FlinkCDCConfig config) {
        if (Asserts.isNull(config) || Asserts.isNullString(config.getSink().get("connector"))) {
            throw new FlinkClientException("请指定 Sink connector。");
        }
        for (int i = 0; i < sinkBuilders.length; i++) {
            if (config.getSink().get("connector").equals(sinkBuilders[i].getHandle())) {
                return sinkBuilders[i].create(config);
            }
        }
        return new SQLSinkBuilder().create(config);
    }
}
