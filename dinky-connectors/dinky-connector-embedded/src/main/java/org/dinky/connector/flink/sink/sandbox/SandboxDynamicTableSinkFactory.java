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

package org.dinky.connector.flink.sink.sandbox;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.types.logical.RowType;

import java.util.Set;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SandboxDynamicTableSinkFactory implements DynamicTableSinkFactory {

    public static final String CONNECTOR_KEY = "dinky-sandbox";

    public static final ConfigOption<String> HOST = ConfigOptions.key("host")
            .stringType()
            .noDefaultValue()
            .withDescription("Socket Server Host,e.g. localhost");

    public static final ConfigOption<Integer> PORT =
            ConfigOptions.key("port").intType().defaultValue(9999).withDescription("Socket Server Port,e.g. 9999");

    public static final ConfigOption<String> BOX_ID =
            ConfigOptions.key("box-id").stringType().defaultValue("public").withDescription("Sandbox ID");

    public static final ConfigOption<String> TABLE = ConfigOptions.key("table")
            .stringType()
            .defaultValue("default_database.sandbox_sink")
            .withDescription("Sink Table Name");

    public static final ConfigOption<Integer> MAX_ROW_NUM =
            ConfigOptions.key("max-row-num").intType().defaultValue(1000).withDescription("Max Row Num");

    public static final ConfigOption<Boolean> AUTO_CANCEL =
            ConfigOptions.key("auto-cancel").booleanType().defaultValue(true).withDescription("Auto Cancel");

    public static final ConfigOption<Boolean> USE_CHANGE_LOG = ConfigOptions.key("use-change-log")
            .booleanType()
            .defaultValue(false)
            .withDescription("Use Change Log");

    @Override
    public String factoryIdentifier() {
        return CONNECTOR_KEY;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return Sets.newHashSet(HOST, PORT);
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return Sets.newHashSet(BOX_ID, TABLE, MAX_ROW_NUM, AUTO_CANCEL, USE_CHANGE_LOG);
    }

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        helper.validate();

        final String host = helper.getOptions().get(HOST);
        final Integer port = helper.getOptions().get(PORT);
        final String boxId = helper.getOptions().get(BOX_ID);
        final String table = helper.getOptions().get(TABLE);
        final Integer maxRowNum = helper.getOptions().get(MAX_ROW_NUM);
        final boolean autoCancel = helper.getOptions().get(AUTO_CANCEL);
        final boolean useChangeLog = helper.getOptions().get(USE_CHANGE_LOG);

        final RowType rowType = (RowType) context.getCatalogTable()
                .getResolvedSchema()
                .toPhysicalRowDataType()
                .getLogicalType();

        return new SandboxDynamicTableSink(host, port, boxId, table, rowType, maxRowNum, autoCancel, useChangeLog);
    }
}
