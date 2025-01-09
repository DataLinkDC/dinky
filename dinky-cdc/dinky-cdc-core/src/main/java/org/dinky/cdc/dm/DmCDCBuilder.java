package org.dinky.cdc.dm;

/**
 * Author: lwjhn
 * Date: 2024/3/19 16:30
 * Description:
 */

import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.dm.source.DmSourceBuilder;
import com.ververica.cdc.debezium.GenericJsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
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

public class DmCDCBuilder extends AbstractCDCBuilder {

    public static final String KEY_WORD = "dm-cdc";
    private static final String METADATA_TYPE = "dm";

    public DmCDCBuilder() {
    }

    public DmCDCBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public CDCBuilder create(FlinkCDCConfig config) {
        return new DmCDCBuilder(config);
    }

    @Override
    public DataStreamSource<String> build(StreamExecutionEnvironment env) {
        Properties debeziumProperties = new Properties();// 为部分转换添加默认值
        debeziumProperties.setProperty("bigint.unsigned.handling.mode", "long");
        debeziumProperties.setProperty("decimal.handling.mode", "string");

        for (Map.Entry<String, String> entry : config.getDebezium().entrySet()) {
            if (Asserts.isNotNullString(entry.getKey()) && Asserts.isNotNullString(entry.getValue())) {
                debeziumProperties.setProperty(entry.getKey(), entry.getValue());
            }
        }
        DmSourceBuilder sourceBuilder = new DmSourceBuilder()
                .hostname(config.getHostname())
                .port(config.getPort())
                .databaseList(config.getDatabase())
                .username(config.getUsername())
                .password(config.getPassword())
                .deserializer(new GenericJsonDebeziumDeserializationSchema())
                .includeSchemaChanges(true)
                .debeziumProperties(debeziumProperties);

        String schema = config.getSchema();
        if (Asserts.isNotNullString(schema)) {
            String[] schemas = schema.split(FlinkParamConstant.SPLIT);
            sourceBuilder.schemaList(schemas);
        } else {
            sourceBuilder.schemaList(new String[0]);
        }
        List<String> schemaTableNameList = config.getSchemaTableNameList();
        if (Asserts.isNotNullCollection(schemaTableNameList)) {
            sourceBuilder.tableList(schemaTableNameList.toArray(new String[schemaTableNameList.size()]));
        } else {
            sourceBuilder.tableList(new String[0]);
        }

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
        return env.fromSource(sourceBuilder.build(), WatermarkStrategy.noWatermarks(), "Dameng CDC Source");
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
                "jdbc:dm://%s:%d/%s%s",
                config.getHostname(), config.getPort(), schema, composeJdbcProperties(config.getJdbc()));
    }

    private String composeJdbcProperties(Map<String, String> jdbcProperties) {
        if (jdbcProperties == null || jdbcProperties.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('?');
        jdbcProperties.forEach((k, v) -> {
            sb.append(k);
            sb.append("=");
            sb.append(v);
            sb.append("&");
        });
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}