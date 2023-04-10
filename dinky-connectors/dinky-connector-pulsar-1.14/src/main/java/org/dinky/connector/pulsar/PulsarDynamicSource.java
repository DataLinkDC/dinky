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

package org.dinky.connector.pulsar;

import static org.apache.flink.util.Preconditions.checkNotNull;

import org.dinky.connector.pulsar.util.PulsarConnectorOptions;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.connector.source.Boundedness;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.pulsar.source.PulsarSource;
import org.apache.flink.connector.pulsar.source.PulsarSourceBuilder;
import org.apache.flink.connector.pulsar.source.PulsarSourceOptions;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StartCursor;
import org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.source.DataStreamScanProvider;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.abilities.SupportsWatermarkPushDown;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;
import org.apache.pulsar.client.api.SubscriptionType;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** * @version 1.0 * @Desc: */

/** A version-agnostic Pulsar {@link ScanTableSource}. */
@Internal
public class PulsarDynamicSource implements ScanTableSource, SupportsWatermarkPushDown {

    private static final Logger LOG = LoggerFactory.getLogger(PulsarDynamicSource.class);
    private final String serviceUrl;
    private final String adminUrl;
    private final String subscriptionName;
    private final SubscriptionType subscriptionType;
    private final PulsarConnectorOptions.ScanStartupMode startupMode;
    private final String topic;
    private final DecodingFormat<DeserializationSchema<RowData>> decodingFormat;
    private final DataType producedDataType;
    private final String tableIdentifier;
    private final Properties properties;
    private final Long timestamp;
    private final Integer sourceParallelism;

    /** Watermark strategy that is used to generate per-partition watermark. */
    protected WatermarkStrategy<RowData> watermarkStrategy;

    public PulsarDynamicSource(
            String serviceUrl,
            String adminUrl,
            String subscriptionName,
            SubscriptionType subscriptionType,
            PulsarConnectorOptions.ScanStartupMode startupMode,
            Long timestamp,
            String topic,
            DecodingFormat<DeserializationSchema<RowData>> decodingFormat,
            DataType producedDataType,
            String tableIdentifier,
            Properties properties,
            Integer sourceParallelism) {
        this.serviceUrl = serviceUrl;
        this.adminUrl = adminUrl;
        this.subscriptionName = subscriptionName;
        this.subscriptionType = subscriptionType;
        this.startupMode = startupMode;
        this.timestamp = timestamp;
        this.topic = topic;
        this.decodingFormat = decodingFormat;
        this.producedDataType = producedDataType;
        this.tableIdentifier = tableIdentifier;
        this.properties = properties;
        this.sourceParallelism = sourceParallelism;
        this.watermarkStrategy = null;
    }

    @Override
    public ChangelogMode getChangelogMode() {
        // in our example the format decides about the changelog mode
        // but it could also be the source itself
        return decodingFormat.getChangelogMode();
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext runtimeProviderContext) {

        // create runtime classes that are shipped to the cluster

        final DeserializationSchema<RowData> deserializer =
                decodingFormat.createRuntimeDecoder(runtimeProviderContext, producedDataType);

        final PulsarSource<RowData> pulsarSource = createPulsarSource(deserializer);

        return new DataStreamScanProvider() {
            @Override
            public DataStream<RowData> produceDataStream(StreamExecutionEnvironment execEnv) {
                if (watermarkStrategy == null) {
                    LOG.info("WatermarkStrategy 为空");
                    watermarkStrategy = WatermarkStrategy.noWatermarks();
                } else {
                    LOG.info("WatermarkStrategy 不为空");
                }

                DataStreamSource<RowData> rowDataDataStreamSource =
                        execEnv.fromSource(
                                pulsarSource, watermarkStrategy, "PulsarSource-" + tableIdentifier);

                // 设置source并行度
                if (sourceParallelism != null) {
                    rowDataDataStreamSource.setParallelism(sourceParallelism);
                }
                return rowDataDataStreamSource;
            }

            @Override
            public boolean isBounded() {
                return pulsarSource.getBoundedness() == Boundedness.BOUNDED;
            }
        };
    }

    @Override
    public DynamicTableSource copy() {
        return new PulsarDynamicSource(
                serviceUrl,
                adminUrl,
                subscriptionName,
                subscriptionType,
                startupMode,
                timestamp,
                topic,
                decodingFormat,
                producedDataType,
                tableIdentifier,
                properties,
                sourceParallelism);
    }

    @Override
    public String asSummaryString() {
        return "Pulsar Table Source";
    }

    // ---------------------------------------------------------------------------------------------
    protected PulsarSource<RowData> createPulsarSource(
            DeserializationSchema<RowData> deserializer) {

        final PulsarSourceBuilder<RowData> pulsarSourceBuilder = PulsarSource.builder();

        pulsarSourceBuilder
                .setConfig(PulsarSourceOptions.PULSAR_ENABLE_AUTO_ACKNOWLEDGE_MESSAGE, true)
                .setServiceUrl(serviceUrl)
                .setAdminUrl(adminUrl)
                .setTopics(topic)
                .setDeserializationSchema(PulsarDeserializationSchema.flinkSchema(deserializer))
                .setConfig(Configuration.fromMap((Map) properties))
                .setSubscriptionName(subscriptionName);

        switch (subscriptionType) {
            case Shared:
                pulsarSourceBuilder.setSubscriptionType(SubscriptionType.Shared);
                break;
            case Exclusive:
                pulsarSourceBuilder.setSubscriptionType(SubscriptionType.Exclusive);
                break;
            case Key_Shared:
                pulsarSourceBuilder.setSubscriptionType(SubscriptionType.Key_Shared);
                break;
            case Failover:
                pulsarSourceBuilder.setSubscriptionType(SubscriptionType.Failover);
                break;
            default:
                throw new TableException(
                        "Unsupported subscriptionType. Validator should have checked that.");
        }

        switch (startupMode) {
            case EARLIEST:
                pulsarSourceBuilder.setStartCursor(StartCursor.earliest());
                break;
            case LATEST:
                pulsarSourceBuilder.setStartCursor(StartCursor.latest());
                break;
            case TIMESTAMP:
                checkNotNull(timestamp, "No timestamp supplied.");
                pulsarSourceBuilder.setStartCursor(StartCursor.fromMessageTime(timestamp));
                break;
            default:
                throw new TableException(
                        "Unsupported startup mode. Validator should have checked that.");
        }

        return pulsarSourceBuilder.build();
    }

    @Override
    public void applyWatermark(WatermarkStrategy<RowData> watermarkStrategy) {
        this.watermarkStrategy = watermarkStrategy;
    }
}
