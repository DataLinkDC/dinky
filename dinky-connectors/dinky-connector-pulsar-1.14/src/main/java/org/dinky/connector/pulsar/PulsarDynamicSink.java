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

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.RowKind;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

/** * @version 1.0 * @Desc: */

/** A version-agnostic Pulsar {@link DynamicTableSink}. */
@Internal
public class PulsarDynamicSink implements DynamicTableSink {

    // --------------------------------------------------------------------------------------------
    // Mutable attributes
    // --------------------------------------------------------------------------------------------

    /** Metadata that is appended at the end of a physical sink row. */
    protected List<String> metadataKeys;

    // --------------------------------------------------------------------------------------------
    // Format attributes
    // --------------------------------------------------------------------------------------------

    /** Data type of consumed data type. */
    protected DataType consumedDataType;

    /** Data type to configure the formats. */
    protected final DataType physicalDataType;

    /** Optional format for encoding to Pulsar. */
    protected final EncodingFormat<SerializationSchema<RowData>> encodingFormat;

    // --------------------------------------------------------------------------------------------
    // Pulsar-specific attributes
    // --------------------------------------------------------------------------------------------

    /** The Pulsar topic to write to. */
    protected final String topic;

    /** The Pulsar service url config. */
    protected final String serviceUrl;

    /** The Pulsar update mode to. */
    protected final String updateMode;

    /** Properties for the Pulsar producer. */
    protected final Properties pulsarProducerProperties;

    /** Properties for the Pulsar producer. */
    protected final Properties pulsarClientProperties;

    /** Properties for the Pulsar producer parallelism. */
    protected final Integer sinkParallelism;

    public PulsarDynamicSink(
            DataType physicalDataType,
            EncodingFormat<SerializationSchema<RowData>> encodingFormat,
            String topic,
            String serviceUrl,
            String updateMode,
            Properties pulsarProducerProperties,
            Properties pulsarClientProperties,
            Integer sinkParallelism) {
        // Format attributes
        this.physicalDataType = checkNotNull(physicalDataType, "Physical data type must not be null.");
        this.encodingFormat = encodingFormat;
        // Mutable attributes
        this.metadataKeys = Collections.emptyList();
        // Pulsar-specific attributes
        this.topic = checkNotNull(topic, "Topic must not be null.");
        this.serviceUrl = checkNotNull(serviceUrl, "Service url must not be null.");
        this.updateMode = checkNotNull(updateMode, "Update mode must not be null.");
        this.pulsarProducerProperties =
                checkNotNull(pulsarProducerProperties, "pulsarProducerProperties must not be null.");
        this.pulsarClientProperties = checkNotNull(pulsarClientProperties, "pulsarClientProperties must not be null.");
        this.sinkParallelism = sinkParallelism;
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        if (updateMode.equals("append")) {
            return ChangelogMode.newBuilder().addContainedKind(RowKind.INSERT).build();
        } else {
            return ChangelogMode.newBuilder()
                    .addContainedKind(RowKind.INSERT)
                    .addContainedKind(RowKind.UPDATE_AFTER)
                    .build();
        }
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
        SerializationSchema<RowData> runtimeEncoder = encodingFormat.createRuntimeEncoder(context, physicalDataType);

        PulsarSinkFunction<RowData> sinkFunction = new PulsarSinkFunction<>(
                topic, serviceUrl, pulsarProducerProperties, pulsarClientProperties, runtimeEncoder);
        // sink的并行度设置
        if (sinkParallelism != null) {
            return SinkFunctionProvider.of(sinkFunction, sinkParallelism);
        } else {
            return SinkFunctionProvider.of(sinkFunction);
        }
    }

    @Override
    public DynamicTableSink copy() {
        final PulsarDynamicSink copy = new PulsarDynamicSink(
                physicalDataType,
                encodingFormat,
                topic,
                serviceUrl,
                updateMode,
                pulsarProducerProperties,
                pulsarClientProperties,
                sinkParallelism);
        copy.metadataKeys = metadataKeys;
        return copy;
    }

    @Override
    public String asSummaryString() {
        return "Pulsar table sink";
    }
}
