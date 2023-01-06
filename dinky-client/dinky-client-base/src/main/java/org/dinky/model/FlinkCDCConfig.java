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

package com.dlink.model;

import java.util.List;
import java.util.Map;

/**
 * FlinkCDCConfig
 *
 * @author wenmo
 * @since 2022/1/29 22:50
 */
public class FlinkCDCConfig {

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
    private List<Schema> schemaList;
    private String schemaFieldName;

    public FlinkCDCConfig() {
    }

    public FlinkCDCConfig(String type, String hostname, Integer port, String username, String password, Integer checkpoint, Integer parallelism, String database, String schema, String table,
                          String startupMode,
                          Map<String, String> split, Map<String, String> debezium, Map<String, String> source, Map<String, String> sink, Map<String, String> jdbc) {
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
        this.jdbc = jdbc;
    }

    public void init(String type, String hostname, Integer port, String username, String password, Integer checkpoint, Integer parallelism, String database, String schema, String table,
                          String startupMode,
                          Map<String, String> split, Map<String, String> debezium, Map<String, String> source, Map<String, String> sink, Map<String, String> jdbc) {
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
        this.jdbc = jdbc;
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

    public List<String> getSchemaTableNameList() {
        return schemaTableNameList;
    }

    public void setSchemaTableNameList(List<String> schemaTableNameList) {
        this.schemaTableNameList = schemaTableNameList;
    }

    private boolean skip(String key) {
        switch (key) {
            case "sink.db":
            case "auto.create":
            case "table.prefix":
            case "table.suffix":
            case "table.upper":
            case "table.lower":
            case "column.replace.line-break":
            case "timezone":
                return true;
            default:
                return false;
        }
    }

    public String getSinkConfigurationString() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (Map.Entry<String, String> entry : sink.entrySet()) {
            if (skip(entry.getKey())) {
                continue;
            }
            if (index > 0) {
                sb.append(",");
            }
            sb.append("'");
            sb.append(entry.getKey());
            sb.append("' = '");
            sb.append(entry.getValue());
            sb.append("'\n");
            index++;
        }
        return sb.toString();
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
