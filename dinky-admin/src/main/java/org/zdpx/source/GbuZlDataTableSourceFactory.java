package org.zdpx.source;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@Component
public class GbuZlDataTableSourceFactory implements DynamicTableSourceFactory {
    @Override
    public DynamicTableSource createDynamicTableSource(Context ctx) {
        final FactoryUtil.TableFactoryHelper factoryHelper = FactoryUtil.createTableFactoryHelper(this, ctx);
        factoryHelper.validate();

        return new GbuZlDataTableSource();
    }

    @Override
    public String factoryIdentifier() {
        return "gbuzl";
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return new HashSet<>();
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        ConfigOption<String> stringConfigOption = ConfigOptions.key("table-name").stringType().noDefaultValue();
        Set<ConfigOption<?>> objects = new HashSet<>();
        objects.add(stringConfigOption);
        return objects;
    }
}
