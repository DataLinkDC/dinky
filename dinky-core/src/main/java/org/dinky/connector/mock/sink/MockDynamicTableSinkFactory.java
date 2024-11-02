package org.dinky.connector.mock.sink;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.types.logical.RowType;

import java.util.Collections;
import java.util.Set;

import static org.apache.flink.configuration.ConfigOptions.key;

public class MockDynamicTableSinkFactory implements DynamicTableSinkFactory {
    public static final String IDENTIFIER = "dinky-mock";
    public static final ConfigOption<String> MOCK_IDENTIFIER = key("mock-identifier")
            .stringType()
            .noDefaultValue()
            .withDescription("Message that identify print and is prefixed to the output of the");

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        return new MockDynamicTableSink(context.getObjectIdentifier().asSummaryString(),
                (RowType) context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType().getLogicalType());
    }

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return Collections.emptySet();
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return Collections.emptySet();
    }
}
