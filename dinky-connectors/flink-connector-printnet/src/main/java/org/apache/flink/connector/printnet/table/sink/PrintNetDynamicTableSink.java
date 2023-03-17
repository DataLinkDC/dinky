package org.apache.flink.connector.printnet.table.sink;

import org.apache.flink.api.common.serialization.SerializationSchema;
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
    private final int parallelism;
    private final List<String> partitionKeys;
    private String printIdentifier;
    private Map<String, String> staticPartitions = new LinkedHashMap<>();

    public PrintNetDynamicTableSink(
            DataType type, List<String> partitionKeys, EncodingFormat<SerializationSchema<RowData>> serializingFormat
            , String hostname, int port, int parallelism, String printIdentifier) {
        this.hostname = hostname;
        this.port = port;
        this.encodingFormat = serializingFormat;
        this.type = type;
        this.parallelism = parallelism;
        this.partitionKeys = partitionKeys;
        this.printIdentifier = printIdentifier;
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        return requestedMode;
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {

        final SerializationSchema<RowData> serializer = encodingFormat != null ?
                encodingFormat.createRuntimeEncoder(context, type) : null;

        DataStructureConverter converter = context.createDataStructureConverter(type);

        staticPartitions.forEach(
                (key, value) -> {
                    printIdentifier = null != printIdentifier ? printIdentifier + ":" : "";
                    printIdentifier += key + "=" + value;
                });

        return SinkFunctionProvider.of(
                new PrintNetSinkFunction(hostname, port, serializer, converter),
                parallelism);
    }

    @Override
    public DynamicTableSink copy() {
        return new PrintNetDynamicTableSink(
                type, partitionKeys, encodingFormat, hostname, port, parallelism, printIdentifier);
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
