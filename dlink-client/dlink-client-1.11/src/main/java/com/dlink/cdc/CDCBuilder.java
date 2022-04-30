package com.dlink.cdc;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;
import java.util.Map;

import com.dlink.model.FlinkCDCConfig;

/**
 * CDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:09
 **/
public interface CDCBuilder {

    String getHandle();

    CDCBuilder create(FlinkCDCConfig config);

    DataStreamSource<String> build(StreamExecutionEnvironment env);

    List<String> getSchemaList();

    List<String> getTableList();

    Map<String, Map<String, String>> parseMetaDataConfigs();

    String getSchemaFieldName();
}
