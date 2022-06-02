package com.dlink.cdc.mysql;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractCDCBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.constant.ClientConstant;
import com.dlink.constant.FlinkParamConstant;
import com.dlink.model.FlinkCDCConfig;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.MySqlSourceBuilder;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;

/**
 * MysqlCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:29
 **/
public class MysqlCDCBuilder extends AbstractCDCBuilder implements CDCBuilder {

    private final static String KEY_WORD = "mysql-cdc";
    private final static String METADATA_TYPE = "MySql";

    public MysqlCDCBuilder() {
    }

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
        String database = config.getDatabase();
        String serverId = config.getSource().get("server-id");
        String serverTimeZone = config.getSource().get("server-time-zone");
        String fetchSize = config.getSource().get("scan.snapshot.fetch.size");
        String connectTimeout = config.getSource().get("connect.timeout");
        String connectMaxRetries = config.getSource().get("connect.max-retries");
        String connectionPoolSize = config.getSource().get("connection.pool.size");
        String heartbeatInterval = config.getSource().get("heartbeat.interval");

        Properties debeziumProperties = new Properties();
        debeziumProperties.setProperty("bigint.unsigned.handling.mode","long");
        for (Map.Entry<String, String> entry : config.getDebezium().entrySet()) {
            if (Asserts.isNotNullString(entry.getKey()) && Asserts.isNotNullString(entry.getValue())) {
                debeziumProperties.setProperty(entry.getKey(), entry.getValue());
            }
        }

        MySqlSourceBuilder<String> sourceBuilder = MySqlSource.<String>builder()
            .hostname(config.getHostname())
            .port(config.getPort())
            .username(config.getUsername())
            .password(config.getPassword());

        if (Asserts.isNotNullString(database)) {
            String[] databases = database.split(FlinkParamConstant.SPLIT);
            sourceBuilder.databaseList(databases);
        } else {
            sourceBuilder.databaseList(new String[0]);
        }

        List<String> schemaTableNameList = config.getSchemaTableNameList();
        if (Asserts.isNotNullCollection(schemaTableNameList)) {
            sourceBuilder.tableList(schemaTableNameList.toArray(new String[schemaTableNameList.size()]));
        } else {
            sourceBuilder.tableList(new String[0]);
        }

        sourceBuilder.deserializer(new JsonDebeziumDeserializationSchema());
        sourceBuilder.debeziumProperties(debeziumProperties);

        if (Asserts.isNotNullString(config.getStartupMode())) {
            switch (config.getStartupMode().toLowerCase()) {
                case "initial":
                    sourceBuilder.startupOptions(StartupOptions.initial());
                    break;
                case "latest-offset":
                    sourceBuilder.startupOptions(StartupOptions.latest());
                    break;
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
            sourceBuilder.fetchSize(Integer.valueOf(fetchSize));
        }

        if (Asserts.isNotNullString(connectTimeout)) {
            sourceBuilder.connectTimeout(Duration.ofMillis(Long.valueOf(connectTimeout)));
        }

        if (Asserts.isNotNullString(connectMaxRetries)) {
            sourceBuilder.connectMaxRetries(Integer.valueOf(connectMaxRetries));
        }

        if (Asserts.isNotNullString(connectionPoolSize)) {
            sourceBuilder.connectionPoolSize(Integer.valueOf(connectionPoolSize));
        }

        if (Asserts.isNotNullString(heartbeatInterval)) {
            sourceBuilder.heartbeatInterval(Duration.ofMillis(Long.valueOf(heartbeatInterval)));
        }
        return env.fromSource(sourceBuilder.build(), WatermarkStrategy.noWatermarks(), "MySQL CDC Source");
    }

    public List<String> getSchemaList() {
        List<String> schemaList = new ArrayList<>();
        String schema = config.getDatabase();
        if (Asserts.isNotNullString(schema)) {
            String[] schemas = schema.split(FlinkParamConstant.SPLIT);
            Collections.addAll(schemaList, schemas);
        }
        List<String> tableList = getTableList();
        for (String tableName : tableList) {
            tableName = tableName.trim();
            if (Asserts.isNotNullString(tableName) && tableName.contains(".")) {
                String[] names = tableName.split("\\\\.");
                if (!schemaList.contains(names[0])) {
                    schemaList.add(names[0]);
                }
            }
        }
        return schemaList;
    }

    public Map<String, Map<String, String>> parseMetaDataConfigs() {
        Map<String, Map<String, String>> allConfigMap = new HashMap<>();
        List<String> schemaList = getSchemaList();
        for (String schema : schemaList) {
            Map<String, String> configMap = new HashMap<>();
            configMap.put(ClientConstant.METADATA_TYPE, METADATA_TYPE);
            StringBuilder sb = new StringBuilder("jdbc:mysql://");
            sb.append(config.getHostname());
            sb.append(":");
            sb.append(config.getPort());
            sb.append("/");
            sb.append(schema);
            configMap.put(ClientConstant.METADATA_NAME, sb.toString());
            configMap.put(ClientConstant.METADATA_URL, sb.toString());
            configMap.put(ClientConstant.METADATA_USERNAME, config.getUsername());
            configMap.put(ClientConstant.METADATA_PASSWORD, config.getPassword());
            allConfigMap.put(schema, configMap);
        }
        return allConfigMap;
    }

    @Override
    public String getSchemaFieldName() {
        return "db";
    }
}
