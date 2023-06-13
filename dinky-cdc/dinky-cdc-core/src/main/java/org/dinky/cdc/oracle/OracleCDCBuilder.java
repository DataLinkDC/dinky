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

package org.dinky.cdc.oracle;

import org.dinky.assertion.Asserts;
import org.dinky.cdc.AbstractCDCBuilder;
import org.dinky.cdc.CDCBuilder;
import org.dinky.constant.FlinkParamConstant;
import org.dinky.data.model.FlinkCDCConfig;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;
import java.util.Properties;

import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;

/**
 * MysqlCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:29
 */
public class OracleCDCBuilder extends AbstractCDCBuilder implements CDCBuilder {

    public static final String KEY_WORD = "oracle-cdc";
    private static final String METADATA_TYPE = "Oracle";

    public OracleCDCBuilder() {}

    public OracleCDCBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public CDCBuilder create(FlinkCDCConfig config) {
        return new OracleCDCBuilder(config);
    }

    @Override
    public DataStreamSource<String> build(StreamExecutionEnvironment env) {
        Properties properties = new Properties();
        config.getDebezium()
                .forEach(
                        (key, value) -> {
                            if (Asserts.isNotNullString(key) && Asserts.isNotNullString(value)) {
                                properties.setProperty(key, value);
                            }
                        });

        OracleSource.Builder<String> sourceBuilder =
                OracleSource.<String>builder()
                        .hostname(config.getHostname())
                        .port(config.getPort())
                        .username(config.getUsername())
                        .password(config.getPassword())
                        .database(config.getDatabase());

        String schema = config.getSchema();
        if (Asserts.isNotNullString(schema)) {
            String[] schemas = schema.split(FlinkParamConstant.SPLIT);
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
        sourceBuilder.debeziumProperties(properties);

        if (Asserts.isNotNullString(config.getStartupMode())) {
            switch (config.getStartupMode().toLowerCase()) {
                case "initial":
                    sourceBuilder.startupOptions(StartupOptions.initial());
                    break;
                case "latest-offset":
                    sourceBuilder.startupOptions(StartupOptions.latest());
                    break;
                default:
            }
        } else {
            sourceBuilder.startupOptions(StartupOptions.latest());
        }

        return env.addSource(sourceBuilder.build(), "Oracle CDC Source");
    }

    @Override
    public String getSchema() {
        return config.getSchema();
    }

    @Override
    protected String getMetadataType() {
        return METADATA_TYPE;
    }

    @Override
    protected String generateUrl(String schema) {
        return String.format(
                "jdbc:oracle:thin:@%s:%s:%s",
                config.getHostname(), config.getPort(), config.getDatabase());
    }
}
