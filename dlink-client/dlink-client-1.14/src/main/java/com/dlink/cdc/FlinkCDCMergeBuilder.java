package com.dlink.cdc;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.dlink.assertion.Asserts;
import com.dlink.model.FlinkCDCConfig;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.MySqlSourceBuilder;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;

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
        MySqlSourceBuilder<String> sourceBuilder = MySqlSource.<String>builder()
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
        DataStreamSource<String> streamSource = env.fromSource(sourceBuilder.build(), WatermarkStrategy.noWatermarks(), "MySQL CDC Source");
        streamSource.sinkTo(getKafkaProducer(config.getBrokers(), config.getTopic()));
    }

    private static KafkaSink<String> getKafkaProducer(String brokers, String topic) {
        return KafkaSink.<String>builder()
            .setBootstrapServers(brokers)
            .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                .setTopic(topic)
                .setValueSerializationSchema(new SimpleStringSchema())
                .build()
            )
            .build();
    }
}
