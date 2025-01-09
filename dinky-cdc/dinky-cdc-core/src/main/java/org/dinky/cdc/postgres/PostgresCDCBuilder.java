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

package org.dinky.cdc.postgres;

import com.ververica.cdc.connectors.postgres.PostgreSQLSource;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.dinky.assertion.Asserts;
import org.dinky.cdc.AbstractCDCBuilder;
import org.dinky.cdc.CDCBuilder;
import org.dinky.constant.FlinkParamConstant;
import org.dinky.data.model.FlinkCDCConfig;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PostgresCDCBuilder extends AbstractCDCBuilder implements CDCBuilder {
    public static final String KEY_WORD = "postgres-cdc";

    public PostgresCDCBuilder() {}

    public PostgresCDCBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    protected String getMetadataType() {
        return "PostgreSql";
    }

    public String getSourceName(){
        return "Postgres CDC Source";
    }

    public String getDriverPrefix(){
        return "jdbc:postgresql";
    }

    @Override
    public CDCBuilder create(FlinkCDCConfig config) {
        return new PostgresCDCBuilder(config);
    }

    public DebeziumSourceFunction<String> createSourceFunction(Properties debeziumProperties, String decodingPluginName, String slotName) {
        PostgreSQLSource.Builder<String> sourceBuilder = PostgreSQLSource.<String>builder()
                .hostname(config.getHostname())
                .port(config.getPort())
                .database(config.getDatabase())
                .username(config.getUsername())
                .password(config.getPassword());

        if (Asserts.isNotNullString(config.getSchema())) {
            String[] schemas = config.getSchema().split(FlinkParamConstant.SPLIT);
            sourceBuilder.schemaList(schemas);
        } else {
            sourceBuilder.schemaList();
        }

        List<String> schemaTableNameList = config.getSchemaTableNameList();
        if (Asserts.isNotNullCollection(schemaTableNameList)) {
            sourceBuilder.tableList(schemaTableNameList.toArray(new String[0]));
        } else {
            sourceBuilder.tableList();
        }

        sourceBuilder.deserializer(new JsonDebeziumDeserializationSchema());
        sourceBuilder.debeziumProperties(debeziumProperties);

        if (Asserts.isNotNullString(decodingPluginName)) {
            sourceBuilder.decodingPluginName(decodingPluginName);
        }
        if (Asserts.isNotNullString(slotName)) {
            sourceBuilder.slotName(slotName);
        }
        return sourceBuilder.build();
    }

    @Override
    public DataStreamSource<String> build(StreamExecutionEnvironment env) {
        Properties debeziumProperties = new Properties();
        // 为部分转换添加默认值
        debeziumProperties.setProperty("bigint.unsigned.handling.mode", "long");
        debeziumProperties.setProperty("decimal.handling.mode", "string");

        for (Map.Entry<String, String> entry : config.getDebezium().entrySet()) {
            if (Asserts.isNotNullString(entry.getKey()) && Asserts.isNotNullString(entry.getValue())) {
                debeziumProperties.setProperty(entry.getKey(), entry.getValue());
            }
        }

        String decodingPluginName = config.getSource().get("decoding.plugin.name");
        String slotName = config.getSource().get("slot.name");
        if(StringUtils.isBlank(slotName)){
            slotName = debeziumProperties.getProperty("slot.name", "flink");
        } else {
            debeziumProperties.setProperty("slot.name", slotName);
        }

        return env.addSource(createSourceFunction(debeziumProperties, decodingPluginName, slotName), getSourceName());
    }

    @Override
    public String getSchema() {
        return config.getSchema();
    }

    @Override
    public String generateUrl(String schema) {
        return String.format("%s://%s:%s/%s", getDriverPrefix(), config.getHostname(), config.getPort(), config.getDatabase());
    }
}
