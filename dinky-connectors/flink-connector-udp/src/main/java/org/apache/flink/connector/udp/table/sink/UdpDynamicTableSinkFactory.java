package org.apache.flink.connector.udp.table.sink;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.factories.SerializationFormatFactory;
import org.apache.flink.table.types.DataType;

import org.apache.commons.compress.utils.Sets;

import java.util.Set;

public class UdpDynamicTableSinkFactory implements DynamicTableSinkFactory {
    public static final String IDENTIFIER = "udp";

    public static final ConfigOption<String> HOSTNAME =
            ConfigOptions.key("hostName").stringType().defaultValue("127.0.0.1");

    public static final ConfigOption<Integer> PORT =
            ConfigOptions.key("port").intType().noDefaultValue();

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        final FactoryUtil.TableFactoryHelper helper =
                FactoryUtil.createTableFactoryHelper(this, context);

        final EncodingFormat<SerializationSchema<RowData>> encodingFormat =
                helper.discoverEncodingFormat(SerializationFormatFactory.class, FactoryUtil.FORMAT);

        helper.validate();

        final ReadableConfig options = helper.getOptions();
        final String hostname = options.get(HOSTNAME);
        final int port = options.get(PORT);
        final int parallelism = options.get(FactoryUtil.SINK_PARALLELISM);

        final DataType produceDataType =
                context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType();
        return new UdpDynamicTableSink(
                hostname, port, encodingFormat, produceDataType, parallelism);
    }

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return Sets.newHashSet(HOSTNAME, PORT, FactoryUtil.FORMAT);
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return Sets.newHashSet(FactoryUtil.SINK_PARALLELISM);
    }
}
