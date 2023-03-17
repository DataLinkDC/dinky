package org.apache.flink.connector.udp.table.source;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.SourceFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;

public class UdpDynamicTableSource implements ScanTableSource {

    private final String hostname;
    private final int port;
    private final DecodingFormat<DeserializationSchema<RowData>> decodingFormat;
    private final DataType producedDataType;

    public UdpDynamicTableSource(
            String hostname,
            int port,
            DecodingFormat<DeserializationSchema<RowData>> decodingFormat,
            DataType producedDataType) {
        this.hostname = hostname;
        this.port = port;
        this.decodingFormat = decodingFormat;
        this.producedDataType = producedDataType;
    }

    @Override
    public ChangelogMode getChangelogMode() {
        return decodingFormat.getChangelogMode();
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext scanContext) {
        final DeserializationSchema<RowData> deserializer =
                decodingFormat.createRuntimeDecoder(scanContext, producedDataType);

        final SourceFunction<RowData> sourceFunction =
                new UdpSourceFunction(hostname, port, deserializer);
        return SourceFunctionProvider.of(sourceFunction, false);
    }

    @Override
    public DynamicTableSource copy() {
        return new UdpDynamicTableSource(hostname, port, decodingFormat, producedDataType);
    }

    @Override
    public String asSummaryString() {
        return "UDP Table Source";
    }
}
