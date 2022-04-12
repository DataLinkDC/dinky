package com.dlink.cdc.mysql;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractCDCBuilder;
import com.dlink.cdc.CDCBuilder;
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
        sourceBuilder
            .deserializer(new StringDebeziumDeserializationSchema());
        return env.addSource(sourceBuilder.build(), "MySQL CDC Source");
    }
}
