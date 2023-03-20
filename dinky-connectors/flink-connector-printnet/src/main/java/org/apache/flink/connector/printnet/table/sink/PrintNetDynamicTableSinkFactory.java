package org.apache.flink.connector.printnet.table.sink;

import org.apache.commons.compress.utils.Sets;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.factories.SerializationFormatFactory;

import java.util.Set;

import static org.apache.flink.configuration.ConfigOptions.key;

public class PrintNetDynamicTableSinkFactory implements DynamicTableSinkFactory {
    public static final String IDENTIFIER = "printnet";

    public static final ConfigOption<String> HOSTNAME =
            ConfigOptions.key("hostName").stringType().defaultValue("127.0.0.1");

    public static final ConfigOption<Integer> PORT =
            ConfigOptions.key("port").intType().noDefaultValue();

    public static final ConfigOption<String> PRINT_IDENTIFIER =
            key("print-identifier")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "Message that identify print and is prefixed to the output of the value.");

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        final FactoryUtil.TableFactoryHelper helper =
                FactoryUtil.createTableFactoryHelper(this, context);

        ObjectIdentifier objectIdentifier = context.getObjectIdentifier();

        helper.validate();

        final ReadableConfig options = helper.getOptions();

        EncodingFormat<SerializationSchema<RowData>> serializingFormat = null;

        try {
            // TODO: 2023/3/17 maybe not right
            serializingFormat = helper.discoverEncodingFormat(SerializationFormatFactory.class, FactoryUtil.FORMAT);
        } catch (Exception ignored) {}

        return new PrintNetDynamicTableSink(
                context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType(),
                context.getCatalogTable().getPartitionKeys(),
                serializingFormat,
                options.get(HOSTNAME),
                options.get(PORT),
                options.get(FactoryUtil.SINK_PARALLELISM),
                options.get(PRINT_IDENTIFIER),
                objectIdentifier
        );
    }

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return Sets.newHashSet(HOSTNAME, PORT);
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return Sets.newHashSet(PRINT_IDENTIFIER, FactoryUtil.SINK_PARALLELISM, FactoryUtil.FORMAT);
    }
}
