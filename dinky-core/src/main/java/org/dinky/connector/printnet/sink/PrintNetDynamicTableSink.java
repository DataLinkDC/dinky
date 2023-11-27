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

package org.dinky.connector.printnet.sink;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.connector.sink.abilities.SupportsPartitioning;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrintNetDynamicTableSink implements DynamicTableSink, SupportsPartitioning {

    private final String hostname;
    private final int port;
    private final EncodingFormat<SerializationSchema<RowData>> encodingFormat;
    private final DataType type;
    private final List<String> partitionKeys;
    private String printIdentifier;
    private ObjectIdentifier objectIdentifier;
    private Map<String, String> staticPartitions = new LinkedHashMap<>();

    public PrintNetDynamicTableSink(
            DataType type,
            List<String> partitionKeys,
            EncodingFormat<SerializationSchema<RowData>> serializingFormat,
            String hostname,
            int port,
            String printIdentifier,
            ObjectIdentifier objectIdentifier) {
        this.hostname = hostname;
        this.port = port;
        this.encodingFormat = serializingFormat;
        this.type = type;
        this.partitionKeys = partitionKeys;
        this.printIdentifier = printIdentifier;
        this.objectIdentifier = objectIdentifier;
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        return requestedMode;
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {

        final SerializationSchema<RowData> serializer =
                encodingFormat != null ? encodingFormat.createRuntimeEncoder(context, type) : null;

        DataStructureConverter converter = context.createDataStructureConverter(type);

        if (printIdentifier == null) {
            printIdentifier = objectIdentifier.asSerializableString();
        }

        staticPartitions.forEach((key, value) -> {
            printIdentifier = null != printIdentifier ? printIdentifier + ":" : "";
            printIdentifier += key + "=" + value;
        });

        return SinkFunctionProvider.of(
                new PrintNetSinkFunction(hostname, port, serializer, converter, printIdentifier));
    }

    @Override
    public DynamicTableSink copy() {
        return new PrintNetDynamicTableSink(
                type, partitionKeys, encodingFormat, hostname, port, printIdentifier, objectIdentifier);
    }

    @Override
    public String asSummaryString() {
        return "Print Net Sink";
    }

    @Override
    public void applyStaticPartition(Map<String, String> partition) {
        // make it a LinkedHashMap to maintain partition column order
        staticPartitions = new LinkedHashMap<>();
        for (String partitionCol : partitionKeys) {
            if (partition.containsKey(partitionCol)) {
                staticPartitions.put(partitionCol, partition.get(partitionCol));
            }
        }
    }
}
