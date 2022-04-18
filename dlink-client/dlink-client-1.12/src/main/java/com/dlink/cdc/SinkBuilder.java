package com.dlink.cdc;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.FlinkCDCConfig;

/**
 * SinkBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:09
 **/
public interface SinkBuilder {

    String getHandle();

    SinkBuilder create(FlinkCDCConfig config);

    DataStreamSource build(CDCBuilder cdcBuilder, StreamExecutionEnvironment env, CustomTableEnvironment customTableEnvironment, DataStreamSource<String> dataStreamSource);
}
