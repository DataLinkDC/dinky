package org.dinky.connector.mock.source;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.SourceFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;

public class MockDynamicTableSource implements ScanTableSource {

    private final String hostName;

    private final String port;

    private final Integer taskId;

    private final String tableName;

    private final DecodingFormat<DeserializationSchema<RowData>> decodingFormat;

    private final DataType producedDataType;

    public MockDynamicTableSource(String hostName, String port, Integer taskId, String tableName, DecodingFormat<DeserializationSchema<RowData>> decodingFormat, DataType producedDataType) {
        this.hostName = hostName;
        this.port = port;
        this.taskId = taskId;
        this.tableName = tableName;
        this.decodingFormat = decodingFormat;
        this.producedDataType = producedDataType;
    }

    @Override
    public ChangelogMode getChangelogMode() {
        return decodingFormat.getChangelogMode();
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext scanContext) {
        // create runtime classes that are shipped to the cluster
        final DeserializationSchema<RowData> deserializer = decodingFormat.createRuntimeDecoder(
                scanContext,
                producedDataType
        );
        final SourceFunction<RowData> sourceFuntion = new MockSourceFunction(
                hostName,
                port,
                taskId,
                tableName,
                deserializer
        );
        return SourceFunctionProvider.of(sourceFuntion, false);
    }

    @Override
    public DynamicTableSource copy() {
        return null;
    }

    @Override
    public String asSummaryString() {
        return "";
    }
}
