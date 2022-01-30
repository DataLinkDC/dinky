package com.dlink.cdc;

import com.dlink.assertion.Asserts;
import com.dlink.model.FlinkCDCConfig;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.MySqlSourceBuilder;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

/**
 * FlinkCDCMergeBuilder
 *
 * @author wenmo
 * @since 2022/1/29 22:37
 */
public class FlinkCDCMergeBuilder {

    public static void buildMySqlCDC(StreamExecutionEnvironment env, FlinkCDCConfig config) throws Exception {
        if(Asserts.isNotNull(config.getParallelism())){
            env.setParallelism(config.getParallelism());
        }
        if(Asserts.isNotNull(config.getCheckpoint())){
            env.enableCheckpointing(config.getCheckpoint());
        }
        MySqlSourceBuilder<String> sourceBuilder = MySqlSource.<String>builder()
                .hostname(config.getHostname())
                .port(config.getPort())
                .username(config.getUsername())
                .password(config.getPassword());
        if(Asserts.isNotNull(config.getDatabase())&&config.getDatabase().size()>0){
            sourceBuilder.databaseList(config.getDatabase().toArray(new String[0]));
        }
        if(Asserts.isNotNull(config.getTable())&&config.getTable().size()>0){
            sourceBuilder.tableList(config.getTable().toArray(new String[0]));
        }
        MySqlSource<String> sourceFunction = sourceBuilder.deserializer(new CustomerDeserialization()) //这里需要自定义序列化格式
//                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
//                .deserializer(new StringDebeziumDeserializationSchema()) //默认是这个序列化格式
                .startupOptions(StartupOptions.latest())
                .build();
        DataStreamSource<String> streamSource = env.fromSource(sourceFunction, WatermarkStrategy.noWatermarks(), "MySQL Source");
        streamSource.print();
        streamSource.addSink(getKafkaProducer(config.getBrokers(),config.getTopic()));
//        JobExecutionResult jobExecutionResult = env.execute(config.getJobName());
//        return jobExecutionResult.getJobID().toHexString();
    }

    private static FlinkKafkaProducer<String> getKafkaProducer(String brokers, String topic) {
        return new FlinkKafkaProducer<String>(brokers,
                topic,
                new SimpleStringSchema());
    }
}
