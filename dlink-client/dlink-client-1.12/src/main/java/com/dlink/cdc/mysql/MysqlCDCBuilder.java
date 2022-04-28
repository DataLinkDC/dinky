package com.dlink.cdc.mysql;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractCDCBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.constant.ClientConstant;
import com.dlink.constant.FlinkParamConstant;
import com.dlink.model.FlinkCDCConfig;

/**
 * MysqlCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:29
 **/
public class MysqlCDCBuilder extends AbstractCDCBuilder implements CDCBuilder {

    private String KEY_WORD = "mysql-cdc";
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
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : config.getDebezium().entrySet()) {
            if (Asserts.isNotNullString(entry.getKey()) && Asserts.isNotNullString(entry.getValue())) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
        }
        MySQLSource.Builder<String> sourceBuilder = MySQLSource.<String>builder()
            .hostname(config.getHostname())
            .port(config.getPort())
            .username(config.getUsername())
            .password(config.getPassword());
        if (Asserts.isNotNullString(config.getDatabase())) {
            sourceBuilder.databaseList(config.getDatabase().split(FlinkParamConstant.SPLIT));
        }
        if (Asserts.isNotNullString(config.getTable())) {
            sourceBuilder.tableList(config.getTable().split(FlinkParamConstant.SPLIT));
        }
        sourceBuilder.deserializer(new StringDebeziumDeserializationSchema());
        sourceBuilder.debeziumProperties(properties);
        if (Asserts.isNotNullString(config.getStartupMode())) {
            switch (config.getStartupMode().toUpperCase()) {
                case "INITIAL":
                    sourceBuilder.startupOptions(StartupOptions.initial());
                    break;
                case "EARLIEST":
                    sourceBuilder.startupOptions(StartupOptions.earliest());
                    break;
                case "LATEST":
                    sourceBuilder.startupOptions(StartupOptions.latest());
                    break;
                default:
                    sourceBuilder.startupOptions(StartupOptions.latest());
            }
        } else {
            sourceBuilder.startupOptions(StartupOptions.latest());
        }
        return env.addSource(sourceBuilder.build(), "MySQL CDC Source");
    }

    public List<String> getSchemaList() {
        List<String> schemaList = new ArrayList<>();
        String schema = config.getDatabase();
        if (Asserts.isNullString(schema)) {
            return schemaList;
        }
        String[] schemas = schema.split(FlinkParamConstant.SPLIT);
        Collections.addAll(schemaList, schemas);
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
}
