package org.dinky.connector.mock.source;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.formats.json.JsonFormatFactory;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.types.DataType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MockDynamicTableSourceFactory implements DynamicTableSourceFactory {

    public static final String IDENTIFIER = "dinky-mock-source";

    public static final ConfigOption<String> HOST_NAME = ConfigOptions.key("hostName").stringType().noDefaultValue();
    public static final ConfigOption<String> PORT = ConfigOptions.key("port").stringType().noDefaultValue();
    public static final ConfigOption<Integer> TASK_ID = ConfigOptions.key("taskId").intType().noDefaultValue();
    public static final ConfigOption<String> TABLE_NAME_BEFORE_MOCK = ConfigOptions.key("port").stringType().noDefaultValue();

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);
        JsonFormatFactory jsonFormatFactory = new JsonFormatFactory();
        DecodingFormat<DeserializationSchema<RowData>> decodingFormat = jsonFormatFactory.createDecodingFormat(context, new Configuration());
        final ReadableConfig options = helper.getOptions();
        final DataType producedDataType =
                context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType();
        return new MockDynamicTableSource(options.get(HOST_NAME),
                options.get(PORT),
                options.get(TASK_ID),
                options.get(TABLE_NAME_BEFORE_MOCK),
                decodingFormat,
                producedDataType);
    }

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return new HashSet<>(Arrays.asList(HOST_NAME, TASK_ID, TABLE_NAME_BEFORE_MOCK));
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return Collections.emptySet();
    }
}
