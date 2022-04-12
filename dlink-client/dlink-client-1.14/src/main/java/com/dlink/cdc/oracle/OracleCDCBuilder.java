package com.dlink.cdc.oracle;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractCDCBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.model.FlinkCDCConfig;
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
}
