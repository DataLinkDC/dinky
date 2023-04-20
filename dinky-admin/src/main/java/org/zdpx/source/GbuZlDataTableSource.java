package org.zdpx.source;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.SourceFunctionProvider;
import org.apache.flink.table.data.RowData;

/**
 *
 */
public class GbuZlDataTableSource implements ScanTableSource {
    @Override
    public ChangelogMode getChangelogMode() {
        return ChangelogMode.insertOnly();
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext runtimeProviderContext) {
        boolean bounded = false;
        final SourceFunction<RowData> source = new GbuZlDataSource();
        return SourceFunctionProvider.of(source, bounded);
    }

    @Override
    public DynamicTableSource copy() {
        return new GbuZlDataTableSource();
    }

    @Override
    public String asSummaryString() {
        return "Gbu Zl Table Source";
    }
}
