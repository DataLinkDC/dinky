package com.dlink.cdc;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkParamConstant;
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
        DataStreamSource<String> streamSource = CDCBuilderFactory.buildCDCBuilder(config).build(env);
        streamSource.addSink(getKafkaProducer(config.getBrokers(), config.getTopic()));
    }

    private static FlinkKafkaProducer<String> getKafkaProducer(String brokers, String topic) {
        return new FlinkKafkaProducer<String>(brokers,
            topic,
            new SimpleStringSchema());
    }
}
