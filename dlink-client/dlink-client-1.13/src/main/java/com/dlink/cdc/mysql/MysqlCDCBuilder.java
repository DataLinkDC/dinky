package com.dlink.cdc.mysql;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractCDCBuilder;
import com.dlink.cdc.CDCBuilder;
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

    private String KEY_WORD = "mysql-cdc";

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
        MySqlSourceBuilder<String> sourceBuilder = MySqlSource.<String>builder()
            .hostname(config.getHostname())
            .port(config.getPort())
            .username(config.getUsername())
            .password(config.getPassword());
        String database = config.getDatabase();
        if (Asserts.isNotNullString(database)) {
            String[] databases = database.split(FlinkParamConstant.SPLIT);
            sourceBuilder.databaseList(databases);
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
        return env.fromSource(sourceBuilder.build(), WatermarkStrategy.noWatermarks(), "MySQL CDC Source");
    }
}
