/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.dlink.cdc.mysql;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractCDCBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.constant.ClientConstant;
import com.dlink.constant.FlinkParamConstant;
import com.dlink.model.FlinkCDCConfig;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;

/**
 * MysqlCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:29
 **/
public class MysqlCDCBuilder extends AbstractCDCBuilder implements CDCBuilder {

    private static final String KEY_WORD = "mysql-cdc";
    private static final String METADATA_TYPE = "MySql";

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
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : config.getDebezium().entrySet()) {
            if (Asserts.isNotNullString(entry.getKey()) && Asserts.isNotNullString(entry.getValue())) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
        }
        MySQLSource.Builder<String> sourceBuilder = MySQLSource.<String>builder()
                .hostname(config.getHostname())
                .port(config.getPort())
                .username(config.getUsername())
                .password(config.getPassword());
        String database = config.getDatabase();
        if (Asserts.isNotNullString(database)) {
            String[] databases = database.split(FlinkParamConstant.SPLIT);
            sourceBuilder.databaseList(databases);
        } else {
            sourceBuilder.databaseList(new String[0]);
        }
        List<String> schemaTableNameList = config.getSchemaTableNameList();
        if (Asserts.isNotNullCollection(schemaTableNameList)) {
            sourceBuilder.tableList(schemaTableNameList.toArray(new String[schemaTableNameList.size()]));
        } else {
            sourceBuilder.tableList(new String[0]);
        }
        sourceBuilder.deserializer(new MysqlJsonDebeziumDeserializationSchema());
        sourceBuilder.debeziumProperties(properties);
        return env.addSource(sourceBuilder.build(), "MySQL CDC Source");
    }

    public Map<String, Map<String, String>> parseMetaDataConfigs() {
        Map<String, Map<String, String>> allConfigMap = new HashMap<>();
        List<String> schemaList = getSchemaList();
        for (String schema : schemaList) {
            Map<String, String> configMap = new HashMap<>();
            configMap.put(ClientConstant.METADATA_TYPE, METADATA_TYPE);
            StringBuilder sb = new StringBuilder("jdbc:mysql://");
            sb.append(config.getHostname());
            sb.append(":");
            sb.append(config.getPort());
            sb.append("/");
            sb.append(schema);
            configMap.put(ClientConstant.METADATA_NAME, sb.toString());
            configMap.put(ClientConstant.METADATA_URL, sb.toString());
            configMap.put(ClientConstant.METADATA_USERNAME, config.getUsername());
            configMap.put(ClientConstant.METADATA_PASSWORD, config.getPassword());
            allConfigMap.put(schema, configMap);
        }
        return allConfigMap;
    }

    @Override
    public String getSchema() {
        return config.getDatabase();
    }
}
