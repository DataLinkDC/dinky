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

package org.dinky.cdc.kafka;

import org.dinky.assertion.Asserts;
import org.dinky.cdc.AbstractCDCBuilder;
import org.dinky.cdc.CDCBuilder;
import org.dinky.data.model.FlinkCDCConfig;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

public class KafkaSourceBuilder extends AbstractCDCBuilder {

    public static final String KEY_WORD = "kafka";

    public KafkaSourceBuilder() {}

    public KafkaSourceBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public CDCBuilder create(FlinkCDCConfig config) {
        return new KafkaSourceBuilder(config);
    }

    @Override
    public DataStreamSource<String> build(StreamExecutionEnvironment env) {
        Map<String, String> source = config.getSource();
        String brokers = source.get("properties.bootstrap.servers");
        String topic = source.get("topic");
        String groupId = source.get("properties.group.id");
        String scanBoundedSpecificOffsets = source.get("scan.bounded.specific-offsets");
        String scanBoundedTimestampMillis = source.get("scan.bounded.timestamp-millis");

        final org.apache.flink.connector.kafka.source.KafkaSourceBuilder<String> sourceBuilder =
                KafkaSource.<String>builder()
                        .setBootstrapServers(brokers)
                        .setValueOnlyDeserializer(new SimpleStringSchema());

        if (Asserts.isNotNullString(topic)) {
            sourceBuilder.setTopics(topic);
        }

        if (Asserts.isNotNullString(groupId)) {
            sourceBuilder.setGroupId(groupId);
        }

        if (Asserts.isNotNullString(config.getStartupMode())) {
            switch (config.getStartupMode().toLowerCase()) {
                case "earliest-offset":
                    sourceBuilder.setStartingOffsets(OffsetsInitializer.earliest());
                    break;
                case "latest-offset":
                    sourceBuilder.setStartingOffsets(OffsetsInitializer.latest());
                    break;
                case "group-offsets":
                    sourceBuilder.setStartingOffsets(OffsetsInitializer.committedOffsets());
                    break;
                    /*If specific-offsets is specified, another config option scan.bounded.specific-offsets
                        is required to specify specific bounded offsets for each partition, e.g. an option value
                    partition:0,offset:42;partition:1,offset:300 indicates offset 42 for partition 0 and offset 300
                    for partition 1. If an offset for a partition is not provided it will not consume from that partition.*/
                    /*case "specific-offset":
                    if (Asserts.isNotNullString(scanBoundedSpecificOffsets)) {
                        sourceBuilder.setStartingOffsets(OffsetsInitializer.offsets(scanBoundedSpecificOffsets));
                    } else {
                        throw new RuntimeException("No specific offset parameter specified.");
                    }
                    break;*/
                case "timestamp":
                    if (Asserts.isNotNullString(scanBoundedTimestampMillis)) {
                        sourceBuilder.setStartingOffsets(
                                OffsetsInitializer.timestamp(Long.valueOf(scanBoundedTimestampMillis)));
                    } else {
                        throw new RuntimeException("No timestamp parameter specified.");
                    }
                    break;
                default:
            }
        } else {
            sourceBuilder.setStartingOffsets(OffsetsInitializer.latest());
        }

        return env.fromSource(sourceBuilder.build(), WatermarkStrategy.noWatermarks(), "Kafka Source");
    }

    @Override
    public String getSchemaFieldName() {
        return "db";
    }

    @Override
    public String getSchema() {
        return config.getDatabase();
    }

    @Override
    protected String getMetadataType() {
        return null;
    }

    @Override
    protected String generateUrl(String schema) {
        return null;
    }
}
