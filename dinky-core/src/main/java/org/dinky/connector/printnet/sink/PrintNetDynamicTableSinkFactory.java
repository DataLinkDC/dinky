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

package org.dinky.connector.printnet.sink;

import static org.apache.flink.configuration.ConfigOptions.key;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintNetDynamicTableSinkFactory implements DynamicTableSinkFactory {
    public static final String IDENTIFIER = "printnet";

    public static final ConfigOption<String> HOSTNAME =
            ConfigOptions.key("hostName").stringType().defaultValue("127.0.0.1");

    public static final ConfigOption<Integer> PORT =
            ConfigOptions.key("port").intType().noDefaultValue();

    public static final ConfigOption<String> PRINT_IDENTIFIER = key("print-identifier")
            .stringType()
            .noDefaultValue()
            .withDescription("Message that identify print and is prefixed to the output of the" + " value.");

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);
        final ReadableConfig options = helper.getOptions();

        ObjectIdentifier objectIdentifier = context.getObjectIdentifier();
        FactoryUtil.validateFactoryOptions(this, options);
        EncodingFormat<SerializationSchema<RowData>> serializingFormat = null;

        try {
            // keep no serialization schema for changelog mode now, you can implement it by yourself
            serializingFormat = helper.discoverEncodingFormat(SerializationFormatFactory.class, FactoryUtil.FORMAT);
        } catch (Exception ignored) {
            log.debug("Could not create serialization format for '{}'.", objectIdentifier, ignored);
        }

        return new PrintNetDynamicTableSink(
                context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType(),
                context.getCatalogTable().getPartitionKeys(),
                serializingFormat,
                options.get(HOSTNAME),
                options.get(PORT),
                options.get(PRINT_IDENTIFIER),
                objectIdentifier);
    }

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return new HashSet<>(Arrays.asList(HOSTNAME, PORT));
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return new HashSet<>(Arrays.asList(PRINT_IDENTIFIER, FactoryUtil.FORMAT));
    }
}
