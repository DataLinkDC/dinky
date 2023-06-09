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

package org.dinky.cdc.mysql;

import org.dinky.assertion.Asserts;
import org.dinky.cdc.AbstractCDCBuilder;
import org.dinky.cdc.CDCBuilder;
import org.dinky.constant.FlinkParamConstant;
import org.dinky.data.model.FlinkCDCConfig;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.MySqlSourceBuilder;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;

public class MysqlCDCBuilder extends AbstractCDCBuilder {

    public static final String KEY_WORD = "mysql-cdc";
    private static final String METADATA_TYPE = "MySql";

    public MysqlCDCBuilder() {}

    public MysqlCDCBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public CDCBuilder create(FlinkCDCConfig config) {
        return new MysqlCDCBuilder(config);
    }

    @Override
    public DataStreamSource<String> build(StreamExecutionEnvironment env) {
        Map<String, String> source = config.getSource();
        String serverId = source.get("server-id");
        String serverTimeZone = source.get("server-time-zone");
        String fetchSize = source.get("scan.snapshot.fetch.size");
        String connectTimeout = source.get("connect.timeout");
        String connectMaxRetries = source.get("connect.max-retries");
        String connectionPoolSize = source.get("connection.pool.size");
        String heartbeatInterval = source.get("heartbeat.interval");
        String chunkSize = source.get("scan.incremental.snapshot.chunk.size");
        String distributionFactorLower =
                source.get("chunk-key.even-distribution.factor.upper-bound");
        String distributionFactorUpper =
                source.get("chunk-key.even-distribution.factor.lower-bound");
        String scanNewlyAddedTableEnabled = source.get("scan.newly-added-table.enabled");
        String schemaChanges = source.get("schema.changes");

        // 为部分转换添加默认值
        Properties debeziumProperties = new Properties();
        debeziumProperties.setProperty("bigint.unsigned.handling.mode", "long");
        debeziumProperties.setProperty("decimal.handling.mode", "string");

        config.getDebezium()
                .forEach(
                        (key, value) -> {
                            if (Asserts.isNotNullString(key) && Asserts.isNotNullString(value)) {
                                debeziumProperties.setProperty(key, value);
                            }
                        });

        // 添加jdbc参数注入
        Properties jdbcProperties = new Properties();
        config.getJdbc()
                .forEach(
                        (key, value) -> {
                            if (Asserts.isNotNullString(key) && Asserts.isNotNullString(value)) {
                                jdbcProperties.setProperty(key, value);
                            }
                        });

        MySqlSourceBuilder<String> sourceBuilder =
                MySqlSource.<String>builder()
                        .hostname(config.getHostname())
                        .port(config.getPort())
                        .username(config.getUsername())
                        .password(config.getPassword());

        String database = config.getDatabase();
        if (Asserts.isNotNullString(database)) {
            String[] databases = database.split(FlinkParamConstant.SPLIT);
            sourceBuilder.databaseList(databases);
        } else {
            sourceBuilder.databaseList();
        }

        List<String> schemaTableNameList = config.getSchemaTableNameList();
        if (Asserts.isNotNullCollection(schemaTableNameList)) {
            sourceBuilder.tableList(schemaTableNameList.toArray(new String[0]));
        } else {
            sourceBuilder.tableList();
        }

        sourceBuilder.deserializer(new JsonDebeziumDeserializationSchema());
        sourceBuilder.debeziumProperties(debeziumProperties);
        sourceBuilder.jdbcProperties(jdbcProperties);

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

        if (Asserts.isNotNullString(serverId)) {
            sourceBuilder.serverId(serverId);
        }

        if (Asserts.isNotNullString(serverTimeZone)) {
            sourceBuilder.serverTimeZone(serverTimeZone);
        }

        if (Asserts.isNotNullString(fetchSize)) {
            sourceBuilder.fetchSize(Integer.parseInt(fetchSize));
        }

        if (Asserts.isNotNullString(connectTimeout)) {
            sourceBuilder.connectTimeout(Duration.ofMillis(Long.parseLong(connectTimeout)));
        }

        if (Asserts.isNotNullString(connectMaxRetries)) {
            sourceBuilder.connectMaxRetries(Integer.parseInt(connectMaxRetries));
        }

        if (Asserts.isNotNullString(connectionPoolSize)) {
            sourceBuilder.connectionPoolSize(Integer.parseInt(connectionPoolSize));
        }

        if (Asserts.isNotNullString(heartbeatInterval)) {
            sourceBuilder.heartbeatInterval(Duration.ofMillis(Long.parseLong(heartbeatInterval)));
        }

        if (Asserts.isAllNotNullString(chunkSize)) {
            sourceBuilder.splitSize(Integer.parseInt(chunkSize));
        }

        if (Asserts.isNotNullString(distributionFactorLower)) {
            sourceBuilder.distributionFactorLower(Double.parseDouble(distributionFactorLower));
        }

        if (Asserts.isNotNullString(distributionFactorUpper)) {
            sourceBuilder.distributionFactorUpper(Double.parseDouble(distributionFactorUpper));
        }

        if (Asserts.isEqualsIgnoreCase(scanNewlyAddedTableEnabled, "true")) {
            sourceBuilder.scanNewlyAddedTableEnabled(true);
        }

        if (Asserts.isEqualsIgnoreCase(schemaChanges, "true")) {
            sourceBuilder.includeSchemaChanges(true);
        }

        return env.fromSource(
                sourceBuilder.build(), WatermarkStrategy.noWatermarks(), "MySQL CDC Source");
    }

    @Override
    public Map<String, String> parseMetaDataConfig() {
        String url = String.format("jdbc:mysql://%s:%d/", config.getHostname(), config.getPort());
        return parseMetaDataSingleConfig(url);
    }

    @Override
    public String getSchemaFieldName() {
        return "db";
    }

    @Override
    public String getSchema() {
        return config.getDatabase();
    }

    @Override
    protected String getMetadataType() {
        return METADATA_TYPE;
    }

    @Override
    protected String generateUrl(String schema) {
        return String.format(
                "jdbc:mysql://%s:%d/%s%s",
                config.getHostname(),
                config.getPort(),
                schema,
                composeJdbcProperties(config.getJdbc()));
    }

    private String composeJdbcProperties(Map<String, String> jdbcProperties) {
        if (jdbcProperties == null || jdbcProperties.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('?');
        jdbcProperties.forEach(
                (k, v) -> {
                    sb.append(k);
                    sb.append("=");
                    sb.append(v);
                    sb.append("&");
                });
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
