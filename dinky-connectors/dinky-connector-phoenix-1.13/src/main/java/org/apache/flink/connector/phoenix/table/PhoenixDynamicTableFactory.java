/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.flink.connector.phoenix.table;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.connector.phoenix.JdbcExecutionOptions;
import org.apache.flink.connector.phoenix.dialect.JdbcDialect;
import org.apache.flink.connector.phoenix.dialect.JdbcDialects;
import org.apache.flink.connector.phoenix.internal.options.JdbcDmlOptions;
import org.apache.flink.connector.phoenix.internal.options.JdbcLookupOptions;
import org.apache.flink.connector.phoenix.internal.options.JdbcReadOptions;
import org.apache.flink.connector.phoenix.internal.options.PhoenixJdbcOptions;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.utils.TableSchemaUtils;
import org.apache.flink.util.Preconditions;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * PhoenixDynamicTableFactory
 *
 * @since 2022/3/17 9:44
 */
public class PhoenixDynamicTableFactory implements DynamicTableSourceFactory, DynamicTableSinkFactory {
    public static final String IDENTIFIER = "phoenix-jdbc";
    public static final ConfigOption<String> URL =
            ConfigOptions.key("url").stringType().noDefaultValue().withDescription("The JDBC database URL.");
    public static final ConfigOption<String> TABLE_NAME =
            ConfigOptions.key("table-name").stringType().noDefaultValue().withDescription("The JDBC table name.");
    public static final ConfigOption<String> USERNAME =
            ConfigOptions.key("username").stringType().noDefaultValue().withDescription("The JDBC user name.");
    public static final ConfigOption<String> PASSWORD =
            ConfigOptions.key("password").stringType().noDefaultValue().withDescription("The JDBC password.");
    private static final ConfigOption<String> DRIVER = ConfigOptions.key("driver")
            .stringType()
            .noDefaultValue()
            .withDescription("The class name of the JDBC driver to use to connect to this URL. If"
                    + " not set, it will automatically be derived from the URL.");
    public static final ConfigOption<Duration> MAX_RETRY_TIMEOUT = ConfigOptions.key("connection.max-retry-timeout")
            .durationType()
            .defaultValue(Duration.ofSeconds(60L))
            .withDescription("Maximum timeout between retries.");
    private static final ConfigOption<String> SCAN_PARTITION_COLUMN = ConfigOptions.key("scan.partition.column")
            .stringType()
            .noDefaultValue()
            .withDescription("The column name used for partitioning the input.");
    private static final ConfigOption<Integer> SCAN_PARTITION_NUM = ConfigOptions.key("scan.partition.num")
            .intType()
            .noDefaultValue()
            .withDescription("The number of partitions.");
    private static final ConfigOption<Long> SCAN_PARTITION_LOWER_BOUND = ConfigOptions.key("scan.partition.lower-bound")
            .longType()
            .noDefaultValue()
            .withDescription("The smallest value of the first partition.");
    private static final ConfigOption<Long> SCAN_PARTITION_UPPER_BOUND = ConfigOptions.key("scan.partition.upper-bound")
            .longType()
            .noDefaultValue()
            .withDescription("The largest value of the last partition.");
    private static final ConfigOption<Integer> SCAN_FETCH_SIZE = ConfigOptions.key("scan.fetch-size")
            .intType()
            .defaultValue(0)
            .withDescription("Gives the reader a hint as to the number of rows that should be"
                    + " fetched from the database per round-trip when reading. If the"
                    + " value is zero, this hint is ignored.");
    private static final ConfigOption<Boolean> SCAN_AUTO_COMMIT = ConfigOptions.key("scan.auto-commit")
            .booleanType()
            .defaultValue(true)
            .withDescription("Sets whether the driver is in auto-commit mode.");
    private static final ConfigOption<Long> LOOKUP_CACHE_MAX_ROWS = ConfigOptions.key("lookup.cache.max-rows")
            .longType()
            .defaultValue(-1L)
            .withDescription("The max number of rows of lookup cache, over this value, the oldest"
                    + " rows will be eliminated. \"cache.max-rows\" and \"cache.ttl\""
                    + " options must all be specified if any of them is specified.");
    private static final ConfigOption<Duration> LOOKUP_CACHE_TTL = ConfigOptions.key("lookup.cache.ttl")
            .durationType()
            .defaultValue(Duration.ofSeconds(10L))
            .withDescription("The cache time to live.");
    private static final ConfigOption<Integer> LOOKUP_MAX_RETRIES = ConfigOptions.key("lookup.max-retries")
            .intType()
            .defaultValue(3)
            .withDescription("The max retry times if lookup database failed.");
    private static final ConfigOption<Integer> SINK_BUFFER_FLUSH_MAX_ROWS = ConfigOptions.key(
                    "sink.buffer-flush.max-rows")
            .intType()
            .defaultValue(100)
            .withDescription("The flush max size (includes all append, upsert and delete records),"
                    + " over this number of records, will flush data.");
    private static final ConfigOption<Duration> SINK_BUFFER_FLUSH_INTERVAL = ConfigOptions.key(
                    "sink.buffer-flush.interval")
            .durationType()
            .defaultValue(Duration.ofSeconds(1L))
            .withDescription("The flush interval mills, over this time, asynchronous threads will" + " flush data.");
    private static final ConfigOption<Integer> SINK_MAX_RETRIES = ConfigOptions.key("sink.max-retries")
            .intType()
            .defaultValue(3)
            .withDescription("The max retry times if writing records to database failed.");

    public static final ConfigOption<Boolean> SCHEMA_NAMESPACE_MAPPING_ENABLE = ConfigOptions.key(
                    "phoenix.schema.isnamespacemappingenabled")
            .booleanType()
            .defaultValue(false)
            .withDescription("The JDBC phoenix Schema isNamespaceMappingEnabled.");
    public static final ConfigOption<Boolean> SCHEMA_MAP_SYSTEMTABLE_ENABLE = ConfigOptions.key(
                    "phoenix.schema.mapsystemtablestonamespace")
            .booleanType()
            .defaultValue(false)
            .withDescription("The JDBC phoenix mapSystemTablesToNamespace.");

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);
        ReadableConfig config = helper.getOptions();
        helper.validate();
        this.validateConfigOptions(config);
        PhoenixJdbcOptions jdbcOptions = this.getJdbcOptions(config);
        TableSchema physicalSchema =
                TableSchemaUtils.getPhysicalSchema(context.getCatalogTable().getSchema());
        return new PhoenixDynamicTableSink(
                jdbcOptions,
                this.getJdbcExecutionOptions(config),
                this.getJdbcDmlOptions(jdbcOptions, physicalSchema),
                physicalSchema);
    }

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);
        ReadableConfig config = helper.getOptions();
        helper.validate();
        this.validateConfigOptions(config);
        TableSchema physicalSchema =
                TableSchemaUtils.getPhysicalSchema(context.getCatalogTable().getSchema());
        return new PhoenixDynamicTableSource(
                this.getJdbcOptions(helper.getOptions()),
                this.getJdbcReadOptions(helper.getOptions()),
                this.getJdbcLookupOptions(helper.getOptions()),
                physicalSchema);
    }

    private PhoenixJdbcOptions getJdbcOptions(ReadableConfig readableConfig) {
        String url = (String) readableConfig.get(URL);
        PhoenixJdbcOptions.Builder builder = PhoenixJdbcOptions.builder()
                .setDBUrl(url)
                .setTableName((String) readableConfig.get(TABLE_NAME))
                .setDialect((JdbcDialect) JdbcDialects.get(url).get())
                .setParallelism((Integer)
                        readableConfig.getOptional(FactoryUtil.SINK_PARALLELISM).orElse((Integer) null))
                .setConnectionCheckTimeoutSeconds(
                        (int) ((Duration) readableConfig.get(MAX_RETRY_TIMEOUT)).getSeconds());
        readableConfig.getOptional(DRIVER).ifPresent(builder::setDriverName);
        readableConfig.getOptional(USERNAME).ifPresent(builder::setUsername);
        readableConfig.getOptional(PASSWORD).ifPresent(builder::setPassword);
        readableConfig.getOptional(SCHEMA_NAMESPACE_MAPPING_ENABLE).ifPresent(builder::setNamespaceMappingEnabled);
        readableConfig.getOptional(SCHEMA_MAP_SYSTEMTABLE_ENABLE).ifPresent(builder::setMapSystemTablesToNamespace);

        return builder.build();
    }

    private JdbcReadOptions getJdbcReadOptions(ReadableConfig readableConfig) {
        Optional<String> partitionColumnName = readableConfig.getOptional(SCAN_PARTITION_COLUMN);
        JdbcReadOptions.Builder builder = JdbcReadOptions.builder();
        if (partitionColumnName.isPresent()) {
            builder.setPartitionColumnName((String) partitionColumnName.get());
            builder.setPartitionLowerBound((Long) readableConfig.get(SCAN_PARTITION_LOWER_BOUND));
            builder.setPartitionUpperBound((Long) readableConfig.get(SCAN_PARTITION_UPPER_BOUND));
            builder.setNumPartitions((Integer) readableConfig.get(SCAN_PARTITION_NUM));
        }

        readableConfig.getOptional(SCAN_FETCH_SIZE).ifPresent(builder::setFetchSize);
        builder.setAutoCommit((Boolean) readableConfig.get(SCAN_AUTO_COMMIT));
        return builder.build();
    }

    private JdbcLookupOptions getJdbcLookupOptions(ReadableConfig readableConfig) {
        return new JdbcLookupOptions(
                (Long) readableConfig.get(LOOKUP_CACHE_MAX_ROWS),
                ((Duration) readableConfig.get(LOOKUP_CACHE_TTL)).toMillis(),
                (Integer) readableConfig.get(LOOKUP_MAX_RETRIES));
    }

    private JdbcExecutionOptions getJdbcExecutionOptions(ReadableConfig config) {
        JdbcExecutionOptions.Builder builder = new JdbcExecutionOptions.Builder();
        builder.withBatchSize((Integer) config.get(SINK_BUFFER_FLUSH_MAX_ROWS));
        builder.withBatchIntervalMs(((Duration) config.get(SINK_BUFFER_FLUSH_INTERVAL)).toMillis());
        builder.withMaxRetries((Integer) config.get(SINK_MAX_RETRIES));
        return builder.build();
    }

    private JdbcDmlOptions getJdbcDmlOptions(PhoenixJdbcOptions jdbcOptions, TableSchema schema) {
        String[] keyFields = (String[]) schema.getPrimaryKey()
                .map((pk) -> {
                    return (String[]) pk.getColumns().toArray(new String[0]);
                })
                .orElse((String[]) null);
        return JdbcDmlOptions.builder()
                .withTableName(jdbcOptions.getTableName())
                .withDialect(jdbcOptions.getDialect())
                .withFieldNames(schema.getFieldNames())
                .withKeyFields(keyFields)
                .build();
    }

    public String factoryIdentifier() {
        return this.IDENTIFIER;
    }

    public Set<ConfigOption<?>> requiredOptions() {
        Set<ConfigOption<?>> requiredOptions = new HashSet();
        requiredOptions.add(URL);
        requiredOptions.add(TABLE_NAME);
        requiredOptions.add(SCHEMA_NAMESPACE_MAPPING_ENABLE);
        requiredOptions.add(SCHEMA_MAP_SYSTEMTABLE_ENABLE);
        return requiredOptions;
    }

    public Set<ConfigOption<?>> optionalOptions() {
        Set<ConfigOption<?>> optionalOptions = new HashSet();
        optionalOptions.add(DRIVER);
        optionalOptions.add(USERNAME);
        optionalOptions.add(PASSWORD);
        optionalOptions.add(SCAN_PARTITION_COLUMN);
        optionalOptions.add(SCAN_PARTITION_LOWER_BOUND);
        optionalOptions.add(SCAN_PARTITION_UPPER_BOUND);
        optionalOptions.add(SCAN_PARTITION_NUM);
        optionalOptions.add(SCAN_FETCH_SIZE);
        optionalOptions.add(SCAN_AUTO_COMMIT);
        optionalOptions.add(LOOKUP_CACHE_MAX_ROWS);
        optionalOptions.add(LOOKUP_CACHE_TTL);
        optionalOptions.add(LOOKUP_MAX_RETRIES);
        optionalOptions.add(SINK_BUFFER_FLUSH_MAX_ROWS);
        optionalOptions.add(SINK_BUFFER_FLUSH_INTERVAL);
        optionalOptions.add(SINK_MAX_RETRIES);
        optionalOptions.add(FactoryUtil.SINK_PARALLELISM);
        optionalOptions.add(MAX_RETRY_TIMEOUT);
        // optionalOptions.add(SCHEMA_NAMESPACE_MAPPING_ENABLE);
        // optionalOptions.add(SCHEMA_MAP_SYSTEMTABLE_ENABLE);
        return optionalOptions;
    }

    private void validateConfigOptions(ReadableConfig config) {
        String jdbcUrl = (String) config.get(URL);
        Optional<JdbcDialect> dialect = JdbcDialects.get(jdbcUrl);
        Preconditions.checkState(dialect.isPresent(), "Cannot handle such jdbc url: " + jdbcUrl);
        this.checkAllOrNone(
                config, new ConfigOption[] {SCHEMA_NAMESPACE_MAPPING_ENABLE, SCHEMA_MAP_SYSTEMTABLE_ENABLE});
        this.checkAllOrNone(config, new ConfigOption[] {USERNAME, PASSWORD});
        this.checkAllOrNone(config, new ConfigOption[] {
            SCAN_PARTITION_COLUMN, SCAN_PARTITION_NUM, SCAN_PARTITION_LOWER_BOUND, SCAN_PARTITION_UPPER_BOUND
        });
        if (config.getOptional(SCAN_PARTITION_LOWER_BOUND).isPresent()
                && config.getOptional(SCAN_PARTITION_UPPER_BOUND).isPresent()) {
            long lowerBound = (Long) config.get(SCAN_PARTITION_LOWER_BOUND);
            long upperBound = (Long) config.get(SCAN_PARTITION_UPPER_BOUND);
            if (lowerBound > upperBound) {
                throw new IllegalArgumentException(String.format(
                        "'%s'='%s' must not be larger than '%s'='%s'.",
                        SCAN_PARTITION_LOWER_BOUND.key(), lowerBound, SCAN_PARTITION_UPPER_BOUND.key(), upperBound));
            }
        }

        this.checkAllOrNone(config, new ConfigOption[] {LOOKUP_CACHE_MAX_ROWS, LOOKUP_CACHE_TTL});
        if ((Integer) config.get(LOOKUP_MAX_RETRIES) < 0) {
            throw new IllegalArgumentException(String.format(
                    "The value of '%s' option shouldn't be negative, but is %s.",
                    LOOKUP_MAX_RETRIES.key(), config.get(LOOKUP_MAX_RETRIES)));
        } else if ((Integer) config.get(SINK_MAX_RETRIES) < 0) {
            throw new IllegalArgumentException(String.format(
                    "The value of '%s' option shouldn't be negative, but is %s.",
                    SINK_MAX_RETRIES.key(), config.get(SINK_MAX_RETRIES)));
        } else if (((Duration) config.get(MAX_RETRY_TIMEOUT)).getSeconds() <= 0L) {
            throw new IllegalArgumentException(String.format(
                    "The value of '%s' option must be in second granularity and shouldn't"
                            + " be smaller than 1 second, but is %s.",
                    MAX_RETRY_TIMEOUT.key(),
                    config.get(ConfigOptions.key(MAX_RETRY_TIMEOUT.key())
                            .stringType()
                            .noDefaultValue())));
        }
    }

    private void checkAllOrNone(ReadableConfig config, ConfigOption<?>[] configOptions) {
        int presentCount = 0;
        ConfigOption[] var4 = configOptions;
        int var5 = configOptions.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            ConfigOption configOption = var4[var6];
            if (config.getOptional(configOption).isPresent()) {
                ++presentCount;
            }
        }

        String[] propertyNames =
                (String[]) Arrays.stream(configOptions).map(ConfigOption::key).toArray((x$0) -> {
                    return new String[x$0];
                });
        Preconditions.checkArgument(
                configOptions.length == presentCount || presentCount == 0,
                "Either all or none of the following options should be provided:\n" + String.join("\n", propertyNames));
    }
}
