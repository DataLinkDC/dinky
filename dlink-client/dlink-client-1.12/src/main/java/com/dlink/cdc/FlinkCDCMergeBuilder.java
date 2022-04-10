package com.dlink.cdc;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import com.dlink.assertion.Asserts;
import com.dlink.model.FlinkCDCConfig;

/**
 * FlinkCDCMergeBuilder
 *
 * @author wenmo
 * @since 2022/1/29 22:37
 */
public class FlinkCDCMergeBuilder {

    public static void buildMySqlCDC(StreamExecutionEnvironment env, FlinkCDCConfig config) {
        if (Asserts.isNotNull(config.getParallelism())) {
            env.setParallelism(config.getParallelism());
        }
        if (Asserts.isNotNull(config.getCheckpoint())) {
            env.enableCheckpointing(config.getCheckpoint());
        }
        MySQLSource.Builder<String> sourceBuilder = MySQLSource.<String>builder()
            .hostname(config.getHostname())
            .port(config.getPort())
            .username(config.getUsername())
            .password(config.getPassword());
        if (Asserts.isNotNull(config.getDatabase()) && config.getDatabase().size() > 0) {
            sourceBuilder.databaseList(config.getDatabase().toArray(new String[0]));
        }
        if (Asserts.isNotNull(config.getTable()) && config.getTable().size() > 0) {
            sourceBuilder.tableList(config.getTable().toArray(new String[0]));
        }
        sourceBuilder
            .deserializer(new StringDebeziumDeserializationSchema());
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
        DataStreamSource<String> streamSource = env.addSource(sourceBuilder.build(), "MySQL CDC Source");
        streamSource.addSink(getKafkaProducer(config.getBrokers(), config.getTopic()));
    }

    private static FlinkKafkaProducer<String> getKafkaProducer(String brokers, String topic) {
        return new FlinkKafkaProducer<String>(brokers,
            topic,
            new SimpleStringSchema());
    }
}
