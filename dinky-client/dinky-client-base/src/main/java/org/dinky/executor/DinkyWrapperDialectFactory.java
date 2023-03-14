package org.dinky.executor;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.table.delegation.ExtendedOperationExecutor;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.planner.delegation.DialectFactory;

import java.util.Set;

public class DinkyWrapperDialectFactory implements DialectFactory {

    private DialectFactory dialectFactory;

    public DinkyWrapperDialectFactory(DialectFactory dialectFactory) {
        this.dialectFactory = dialectFactory;
    }

    @Override
    public Parser create(Context context) {
        return new ParserWrapper(dialectFactory.create(context));
    }

    @Override
    public String factoryIdentifier() {
        return "dinky_" + dialectFactory.factoryIdentifier();
    }

    @Override
    public ExtendedOperationExecutor createExtendedOperationExecutor(Context context) {
        return new ExtendedOperationExecutorWrapper(dialectFactory.createExtendedOperationExecutor(context));
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return dialectFactory.requiredOptions();
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return dialectFactory.requiredOptions();
    }
}
