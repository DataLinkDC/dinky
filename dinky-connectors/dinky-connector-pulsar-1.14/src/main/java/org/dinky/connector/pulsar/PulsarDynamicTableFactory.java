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

package org.dinky.connector.pulsar;

import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.ADMIN_URL;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.DERIVE_SCHEMA;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.SERVICE_URL;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.SINK_PARALLELISM;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.SOURCE_PARALLELISM;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.SUBSCRIPTION_INITIAL_POSITION;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.SUBSCRIPTION_INITIAL_POSITION_TIMESTAMP;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.SUBSCRIPTION_NAME;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.SUBSCRIPTION_TYPE;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.ScanStartupMode;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.TOPIC;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.UPDATE_MODE;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptions.VERSION;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptionsUtil.PROPERTIES_CLIENT_PREFIX;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptionsUtil.PROPERTIES_PREFIX;
import static org.dinky.connector.pulsar.util.PulsarConnectorOptionsUtil.getPulsarProperties;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.catalog.CatalogTable;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.format.Format;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DeserializationFormatFactory;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.factories.FactoryUtil.TableFactoryHelper;
import org.apache.flink.table.factories.SerializationFormatFactory;
import org.apache.flink.table.types.DataType;
import org.apache.pulsar.client.api.SubscriptionType;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating configured instances of {@link PulsarDynamicSource} and {
 *
 * <p>* @version 1.0 * @Desc:
 *
 * @link PulsarDynamicSink}.
 */
@Internal
public class PulsarDynamicTableFactory
        implements DynamicTableSourceFactory, DynamicTableSinkFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PulsarDynamicTableFactory.class);

    public static final String IDENTIFIER = "pulsar";

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(SERVICE_URL);
        return options;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        final Set<ConfigOption<?>> options = new HashSet<>();
        options.add(ADMIN_URL);
        options.add(SUBSCRIPTION_NAME);
        options.add(SUBSCRIPTION_TYPE);
        options.add(SUBSCRIPTION_INITIAL_POSITION);
        options.add(SUBSCRIPTION_INITIAL_POSITION_TIMESTAMP);
        options.add(FactoryUtil.FORMAT);
        options.add(TOPIC);
        options.add(UPDATE_MODE);
        options.add(SOURCE_PARALLELISM);
        options.add(SINK_PARALLELISM);
        options.add(VERSION);
        options.add(DERIVE_SCHEMA);

        return options;
    }

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        // either implement your custom validation logic here ...
        // or use the provided helper utility
        final TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        // discover a suitable decoding format
        final DecodingFormat<DeserializationSchema<RowData>> decodingFormat =
                helper.discoverDecodingFormat(
                        DeserializationFormatFactory.class, FactoryUtil.FORMAT);

        // validate all options
        // helper.validate();
        helper.validateExcept(PROPERTIES_PREFIX, PROPERTIES_CLIENT_PREFIX);

        // get the validated options
        final ReadableConfig tableOptions = helper.getOptions();
        final String serviceUrl = tableOptions.get(SERVICE_URL);
        final String adminUrl = tableOptions.get(ADMIN_URL);
        final String subscriptionName = tableOptions.get(SUBSCRIPTION_NAME);
        final SubscriptionType subscriptionType = tableOptions.get(SUBSCRIPTION_TYPE);
        final ScanStartupMode startupMode = tableOptions.get(SUBSCRIPTION_INITIAL_POSITION);
        final Long timestamp = tableOptions.get(SUBSCRIPTION_INITIAL_POSITION_TIMESTAMP);
        final String topic = tableOptions.get(TOPIC);
        final Integer sourceParallelism = tableOptions.get(SOURCE_PARALLELISM);

        // derive the produced data type (excluding computed columns) from the catalog table
        final DataType producedDataType =
                context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType();

        // create and return dynamic table source
        return new PulsarDynamicSource(
                serviceUrl,
                adminUrl,
                subscriptionName,
                subscriptionType,
                startupMode,
                timestamp,
                topic,
                decodingFormat,
                producedDataType,
                context.getObjectIdentifier().asSummaryString(),
                getPulsarProperties(context.getCatalogTable().getOptions(), PROPERTIES_PREFIX),
                sourceParallelism);
    }

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        final TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        final ReadableConfig tableOptions = helper.getOptions();
        final String update_mode = tableOptions.get(UPDATE_MODE);
        final Integer sinkParallelism = tableOptions.get(SINK_PARALLELISM);

        helper.validateExcept(PROPERTIES_PREFIX, PROPERTIES_CLIENT_PREFIX);

        final EncodingFormat<SerializationSchema<RowData>> encodingFormat =
                helper.discoverEncodingFormat(SerializationFormatFactory.class, FactoryUtil.FORMAT);

        // 校验sql建表时是否指定主键约束
        // 我们一般使用flink自动推导出来的主键，不显式设置主键约束，所以这个校验方法暂时不使用
        // validatePKConstraints(update_mode, context.getObjectIdentifier(),
        // context.getCatalogTable(), encodingFormat);

        final DataType physicalDataType =
                context.getCatalogTable().getSchema().toPhysicalRowDataType();

        return createPulsarTableSink(
                physicalDataType,
                encodingFormat,
                tableOptions.get(TOPIC),
                tableOptions.get(SERVICE_URL),
                update_mode,
                getPulsarProperties(context.getCatalogTable().getOptions(), PROPERTIES_PREFIX),
                getPulsarProperties(
                        context.getCatalogTable().getOptions(), PROPERTIES_CLIENT_PREFIX),
                sinkParallelism);
    }

    // 校验sql建表时是否指定主键约束
    private static void validatePKConstraints(
            @Nullable String updateMode,
            ObjectIdentifier tableName,
            CatalogTable catalogTable,
            Format format) {

        if (!updateMode.equals("append") && !updateMode.equals("upsert")) {
            throw new ValidationException(
                    String.format(
                            "The Pulsar table '%s' with update-mode should be 'append' or 'upsert'",
                            tableName.asSummaryString()));
        } else if (catalogTable.getSchema().getPrimaryKey().isPresent()
                && updateMode.equals("append")) {
            throw new ValidationException(
                    String.format(
                            "The Pulsar table '%s' with append update-mode doesn't support defining PRIMARY KEY constraint"
                                    + " on the table, because it can't guarantee the semantic of primary key.",
                            tableName.asSummaryString()));
        } else if (!catalogTable.getSchema().getPrimaryKey().isPresent()
                && updateMode.equals("upsert")) {
            throw new ValidationException(
                    "'upsert' tables require to define a PRIMARY KEY constraint. "
                            + "The PRIMARY KEY specifies which columns should be read from or write to the Pulsar message key. "
                            + "The PRIMARY KEY also defines records in the 'upsert' table should update or delete on which keys.");
        }
    }

    protected PulsarDynamicSink createPulsarTableSink(
            DataType physicalDataType,
            @Nullable EncodingFormat<SerializationSchema<RowData>> encodingFormat,
            String topic,
            String serviceUrl,
            String updateMode,
            Properties pulsarProducerProperties,
            Properties pulsarClientProperties,
            Integer sinkParallelism) {
        return new PulsarDynamicSink(
                physicalDataType,
                encodingFormat,
                topic,
                serviceUrl,
                updateMode,
                pulsarProducerProperties,
                pulsarClientProperties,
                sinkParallelism);
    }
}
