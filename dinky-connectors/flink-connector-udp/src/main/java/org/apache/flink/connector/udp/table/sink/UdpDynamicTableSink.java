package org.apache.flink.connector.udp.table.sink;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;

public class UdpDynamicTableSink implements DynamicTableSink {

    private final String hostname;
    private final int port;
    private final EncodingFormat<SerializationSchema<RowData>> encodingFormat;
    private final DataType producedDataType;
    private final int parallelism;

    public UdpDynamicTableSink(
            String hostname,
            int port,
            EncodingFormat<SerializationSchema<RowData>> serializingFormat,
            DataType producedDataType,
            int parallelism) {
        this.hostname = hostname;
        this.port = port;
        this.encodingFormat = serializingFormat;
        this.producedDataType = producedDataType;
        this.parallelism = parallelism;
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        return ChangelogMode.insertOnly();
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
        final SerializationSchema<RowData> serializer =
                encodingFormat.createRuntimeEncoder(context, producedDataType);

        final SinkFunction<RowData> sinkFunction = new UdpSinkFunction(hostname, port, serializer);
        return SinkFunctionProvider.of(sinkFunction, parallelism);
    }

    @Override
    public DynamicTableSink copy() {
        return new UdpDynamicTableSink(
                hostname, port, encodingFormat, producedDataType, parallelism);
    }

    @Override
    public String asSummaryString() {
        return "Udp Table Sink";
    }
}
