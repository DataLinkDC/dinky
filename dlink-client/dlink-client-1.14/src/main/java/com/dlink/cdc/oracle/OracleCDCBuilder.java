package com.dlink.cdc.oracle;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractCDCBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.constant.ClientConstant;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Table;
import com.ververica.cdc.connectors.oracle.OracleSource;
import com.ververica.cdc.connectors.oracle.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;

/**
 * MysqlCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:29
 **/
public class OracleCDCBuilder extends AbstractCDCBuilder implements CDCBuilder {

    private String KEY_WORD = "oracle-cdc";
    private final static String METADATA_TYPE = "MySql";


    public OracleCDCBuilder() {
    }

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
        for (Map.Entry<String, String> entry : config.getDebezium().entrySet()) {
            if (Asserts.isNotNullString(entry.getKey()) && Asserts.isNotNullString(entry.getValue())) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
        }
        OracleSource.Builder<String> sourceBuilder = OracleSource.<String>builder()
            .hostname(config.getHostname())
            .port(config.getPort())
            .username(config.getUsername())
            .password(config.getPassword())
            .database(config.getDatabase());
        String schema = config.getSchema();
        if (Asserts.isNotNullString(schema)) {
            sourceBuilder.schemaList(schema);
        }
        String table = config.getTable();
        if (Asserts.isNotNullString(table)) {
            sourceBuilder.tableList(table);
        }
        sourceBuilder.deserializer(new JsonDebeziumDeserializationSchema());
        sourceBuilder.debeziumProperties(properties);
        if (Asserts.isNotNullString(config.getStartupMode())) {
            switch (config.getStartupMode().toUpperCase()) {
                case "INITIAL":
                    sourceBuilder.startupOptions(StartupOptions.initial());
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
        return env.addSource(sourceBuilder.build(), "Oracle CDC Source");
    }

    public Map<String, Map<String, String>> parseMetaDataConfigs() {
        Map<String, Map<String, String>> allConfigList = new HashMap<>();
        List<String> schemaList = getSchemaList();
        for (String schema : schemaList) {
            Map<String, String> configMap = new HashMap<>();
            configMap.put(ClientConstant.METADATA_TYPE, METADATA_TYPE);
            StringBuilder sb = new StringBuilder("jdbc:oracle:thin:@");
            sb.append(config.getHostname());
            sb.append(":");
            sb.append(config.getPort());
            sb.append(":");
            sb.append(config.getDatabase());
            configMap.put(ClientConstant.METADATA_NAME, sb.toString());
            configMap.put(ClientConstant.METADATA_URL, sb.toString());
            configMap.put(ClientConstant.METADATA_USERNAME, config.getUsername());
            configMap.put(ClientConstant.METADATA_PASSWORD, config.getPassword());
            allConfigList.put(schema, configMap);
        }
        return allConfigList;
    }

    @Override
    public String getInsertSQL(Table table, String sourceName) {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(table.getName());
        sb.append(" SELECT\n");
        for (int i = 0; i < table.getColumns().size(); i++) {
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            sb.append("`" + table.getColumns().get(i).getName() + "` \n");
        }
        sb.append(" FROM ");
        sb.append(sourceName);
        sb.append(" WHERE schema_name = '");
        sb.append(table.getSchema());
        sb.append("' and table_name = '");
        sb.append(table.getName());
        sb.append("'");
        return sb.toString();
    }
}
