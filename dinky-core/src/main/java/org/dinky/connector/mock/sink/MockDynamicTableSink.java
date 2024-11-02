package org.dinky.connector.mock.sink;


import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.types.logical.RowType;

public class MockDynamicTableSink implements DynamicTableSink {

    private final String tableName;
    private final RowType rowType;

    public MockDynamicTableSink(
            String tableName,
            RowType rowType) {
        this.tableName = tableName;
        this.rowType = rowType;
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        return requestedMode;
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
        return SinkFunctionProvider.of(new MockSinkFunction(tableName, rowType));
    }

    @Override
    public DynamicTableSink copy() {
        return new MockDynamicTableSink(tableName, rowType);
    }

    @Override
    public String asSummaryString() {
        return "Dinky Sink Mock";
    }
}
