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

package org.dinky.data.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FlinkCDCConfig
 *
 * @since 2022/1/29 22:50
 */
public class FlinkCDCConfig {

    public static final String SINK_DB = "sink.db";
    public static final String AUTO_CREATE = "auto.create";
    public static final String TABLE_PREFIX = "table.prefix";
    public static final String TABLE_SUFFIX = "table.suffix";
    public static final String TABLE_UPPER = "table.upper";
    public static final String TABLE_LOWER = "table.lower";
    public static final String TABLE_RENAME = "table.rename";
    public static final String TIMEZONE = "timezone";
    private String type;
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private Integer checkpoint;
    private Integer parallelism;
    private String database;
    private String schema;
    private String table;
    private List<String> schemaTableNameList;
    private String startupMode;
    private Map<String, String> split;
    private Map<String, String> debezium;
    private Map<String, String> source;
    private Map<String, String> jdbc;
    private Map<String, String> sink;
    private List<Map<String, String>> sinks;
    private List<Schema> schemaList;
    private String schemaFieldName;

    public FlinkCDCConfig(
            String type,
            String hostname,
            Integer port,
            String username,
            String password,
            Integer checkpoint,
            Integer parallelism,
            String database,
            String schema,
            String table,
            String startupMode,
            Map<String, String> split,
            Map<String, String> debezium,
            Map<String, String> source,
            Map<String, String> sink,
            List<Map<String, String>> sinks,
            Map<String, String> jdbc) {
        init(
                type,
                hostname,
                port,
                username,
                password,
                checkpoint,
                parallelism,
                database,
                schema,
                table,
                startupMode,
                split,
                debezium,
                source,
                sink,
                sinks,
                jdbc);
    }

    public void init(
            String type,
            String hostname,
            Integer port,
            String username,
            String password,
            Integer checkpoint,
            Integer parallelism,
            String database,
            String schema,
            String table,
            String startupMode,
            Map<String, String> split,
            Map<String, String> debezium,
            Map<String, String> source,
            Map<String, String> sink,
            List<Map<String, String>> sinks,
            Map<String, String> jdbc) {
        this.type = type;
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.database = database;
        this.schema = schema;
        this.table = table;
        this.startupMode = startupMode;
        this.split = split;
        this.debezium = debezium;
        this.source = source;
        this.sink = sink;
        this.sinks = sinks;
        this.jdbc = jdbc;
    }

    private boolean isSkip(String key) {
        if (key.equals("url")) {
            return !(sink.containsKey("connector")
                    && Arrays.asList("jdbc", "clickhouse").contains(sink.get("connector")));
        }
        switch (key) {
            case SINK_DB:
            case AUTO_CREATE:
            case TABLE_PREFIX:
            case TABLE_SUFFIX:
            case TABLE_UPPER:
            case TABLE_LOWER:
            case TABLE_RENAME:
            case TIMEZONE:
                return true;
            default:
                return false;
        }
    }

    public String getSinkConfigurationString() {
        return sink.entrySet().stream()
                .filter(t -> !isSkip(t.getKey()))
                .map(t -> String.format("'%s' = '%s'", t.getKey(), t.getValue()))
                .collect(Collectors.joining(",\n"));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Integer checkpoint) {
        this.checkpoint = checkpoint;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public Map<String, String> getSource() {
        return source;
    }

    public void setSource(Map<String, String> source) {
        this.source = source;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, String> getSink() {
        return sink;
    }

    public List<Map<String, String>> getSinks() {
        return sinks;
    }

    public List<String> getSchemaTableNameList() {
        return schemaTableNameList;
    }

    public void setSchemaTableNameList(List<String> schemaTableNameList) {
        this.schemaTableNameList = schemaTableNameList;
    }

    public void setSink(Map<String, String> sink) {
        this.sink = sink;
    }

    public String getStartupMode() {
        return startupMode;
    }

    public void setStartupMode(String startupMode) {
        this.startupMode = startupMode;
    }

    public List<Schema> getSchemaList() {
        return schemaList;
    }

    public void setSchemaList(List<Schema> schemaList) {
        this.schemaList = schemaList;
    }

    public String getSchemaFieldName() {
        return schemaFieldName;
    }

    public void setSchemaFieldName(String schemaFieldName) {
        this.schemaFieldName = schemaFieldName;
    }

    public Map<String, String> getDebezium() {
        return debezium;
    }

    public Map<String, String> getJdbc() {
        return jdbc;
    }

    public void setJdbc(Map<String, String> jdbc) {
        this.jdbc = jdbc;
    }

    public void setDebezium(Map<String, String> debezium) {
        this.debezium = debezium;
    }

    public Map<String, String> getSplit() {
        return split;
    }

    public void setSplit(Map<String, String> split) {
        this.split = split;
    }
}
