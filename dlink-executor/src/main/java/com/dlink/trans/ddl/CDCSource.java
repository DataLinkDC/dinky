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

package com.dlink.trans.ddl;

import com.dlink.parser.SingleSqlParserFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CDCSource
 *
 * @author wenmo
 * @since 2022/1/29 23:30
 */
public class CDCSource {

    private String connector;
    private String statement;
    private String name;
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private Integer checkpoint;
    private Integer parallelism;
    private String database;
    private String schema;
    private String table;
    private String startupMode;
    private Map<String, String> debezium;
    private Map<String, String> split;
    private Map<String, String> jdbc;
    private Map<String, String> source;
    private Map<String, String> sink;
    private List<Map<String, String>> sinks;

    public CDCSource() {
    }

    public CDCSource(String connector, String statement, String name, String hostname, Integer port, String username,
            String password, Integer checkpoint, Integer parallelism, String startupMode,
            Map<String, String> split, Map<String, String> debezium, Map<String, String> source,
            Map<String, String> sink, Map<String, String> jdbc) {
        this(connector, statement, name, hostname, port, username, password, checkpoint, parallelism, startupMode,
                split, debezium, source, sink, null, jdbc);
    }

    public CDCSource(String connector, String statement, String name, String hostname, Integer port, String username,
            String password, Integer checkpoint, Integer parallelism, String startupMode,
            Map<String, String> split, Map<String, String> debezium, Map<String, String> source,
            Map<String, String> sink, List<Map<String, String>> sinks, Map<String, String> jdbc) {
        this.connector = connector;
        this.statement = statement;
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.startupMode = startupMode;
        this.debezium = debezium;
        this.split = split;
        this.jdbc = jdbc;
        this.source = source;
        this.sink = sink;
        this.sinks = sinks;
    }

    public static CDCSource build(String statement) throws Exception {
        Optional.ofNullable(statement)
                .orElseThrow(() -> new Exception("Statement can not be null. Please specify a statement."));
        Map<String, List<String>> map = SingleSqlParserFactory.generateParser(statement);
        Optional.ofNullable(map)
                .orElseThrow(() -> new Exception("Job configurations can not be null. Please specify configurations."));
        Optional.ofNullable(map.get("WITH"))
                .orElseThrow(() -> new Exception("Job configurations can not be null. Please specify configurations."));

        Map<String, String> config = getKeyValue(map.get("WITH"));
        Optional.ofNullable(config)
                .orElseThrow(() -> new Exception("Job configurations can not be null. Please specify configurations."));

        CDCSource cdcSource = new CDCSource();

        Optional.ofNullable(config.get("connector"))
                .orElseThrow(() -> new Exception("Please specify connector in configurations."));
        cdcSource.setConnector(config.get("connector"));
        cdcSource.setStatement(config.get("statement"));

        String name = Optional.ofNullable(map.get("CDCSOURCE")).orElseGet(() -> {
            return Arrays.asList("dinky_cdcsource_task");
        }).toString();
        cdcSource.setName(name);

        Optional.ofNullable(config.get("hostname"))
                .orElseThrow(() -> new Exception("Please specify hostname in configurations."));
        cdcSource.setHostname(config.get("hostname"));

        Optional.ofNullable(config.get("port"))
                .orElseThrow(() -> new Exception("Please specify port in configurations."));
        cdcSource.setPort(Integer.valueOf(config.get("port")));

        cdcSource.setUsername(config.get("username"));
        cdcSource.setPassword(config.get("password"));

        Optional.ofNullable(config.get("checkpoint")).ifPresent(s -> {
            cdcSource.setCheckpoint(Integer.valueOf(s));
        });
        Optional.ofNullable(config.get("parallelism")).ifPresent(s -> {
            cdcSource.setParallelism(Integer.valueOf(s));
        });
        Optional.ofNullable(config.get("scan.startup.mode")).ifPresent(s -> {
            cdcSource.setStartupMode(s);
        });
        Optional.ofNullable(config.get("database-name")).ifPresent(s -> {
            cdcSource.setDatabase(s);
        });
        Optional.ofNullable(config.get("schema-name")).ifPresent(s -> {
            cdcSource.setSchema(s);
        });
        Optional.ofNullable(config.get("table-name")).ifPresent(s -> {
            cdcSource.setTable(s);
        });

        // debezium params. (debezium.*)
        Map<String, String> debezium = new HashMap<>();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            if (entry.getKey().startsWith("debezium.")) {
                String key = entry.getKey();
                key = key.replaceFirst("debezium.", "");
                if (!debezium.containsKey(key)) {
                    debezium.put(key, entry.getValue());
                }
            }
        }
        cdcSource.setDebezium(debezium);

        // partition table params. (split.*)
        Map<String, String> split = new HashMap<>();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            if (entry.getKey().startsWith("split.")) {
                String key = entry.getKey();
                key = key.replaceFirst("split.", "");
                if (!split.containsKey(key)) {
                    split.put(key, entry.getValue());
                }
            }
        }
        if (split.size() > 0) {
            splitMapInit(split);
        }
        cdcSource.setSplit(split);

        // source custom params. (source.*)
        Map<String, String> source = new HashMap<>();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            if (entry.getKey().startsWith("source.")) {
                String key = entry.getKey();
                key = key.replaceFirst("source.", "");
                if (!source.containsKey(key)) {
                    source.put(key, entry.getValue());
                }
            }
        }
        cdcSource.setSource(source);

        // jdbc params. (jdbc.properties.*)
        Map<String, String> jdbc = new HashMap<>();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            if (entry.getKey().startsWith("jdbc.properties.")) {
                String key = entry.getKey();
                key = key.replaceFirst("jdbc.properties.", "");
                if (!jdbc.containsKey(key)) {
                    jdbc.put(key, entry.getValue());
                }
            }
        }
        cdcSource.setJdbc(jdbc);

        // sink custom params. (sink.*)
        Map<String, String> sink = new HashMap<>();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            if (entry.getKey().startsWith("sink.")) {
                String key = entry.getKey();
                key = key.replaceFirst("sink.", "");
                if (!sink.containsKey(key)) {
                    sink.put(key, entry.getValue());
                }
            }
        }

        // multiple sinks custom params. (sink[i].*)
        Map<String, Map<String, String>> sinks = new HashMap<>();
        final Pattern p = Pattern.compile("sink\\[(?<index>.*)\\]");
        for (Map.Entry<String, String> entry : config.entrySet()) {
            if (entry.getKey().startsWith("sink[")) {
                String key = entry.getKey();
                Matcher matcher = p.matcher(key);
                if (matcher.find()) {
                    final String index = matcher.group("index");
                    Map<String, String> sinkMap = sinks.get(index);
                    if (sinkMap == null) {
                        sinkMap = new HashMap<>();
                        sinks.put(index, sinkMap);
                    }
                    key = key.replaceFirst("sink\\[" + index + "\\].", "");
                    if (!sinkMap.containsKey(key)) {
                        sinkMap.put(key, entry.getValue());
                    }
                }
            }
        }
        final ArrayList<Map<String, String>> sinkList = new ArrayList<>(sinks.values());
        if (sink.isEmpty() && sinkList.size() > 0) {
            sink = sinkList.get(0);
        }
        cdcSource.setSink(sink);
        cdcSource.setSinks(sinkList);

        return cdcSource;
    }

    private static void splitMapInit(Map<String, String> split) {
        split.putIfAbsent("max_match_value", "100");
        split.putIfAbsent("match_number_regex", "_[0-9]+");
        split.putIfAbsent("match_way", "suffix");
        split.putIfAbsent("enable", "false");
    }

    private static Map<String, String> getKeyValue(List<String> list) {
        Map<String, String> map = new HashMap<>();
        Pattern p = Pattern.compile("'(.*?)'\\s*=\\s*'(.*?)'");
        for (int i = 0; i < list.size(); i++) {
            Matcher m = p.matcher(list.get(i) + "'");
            if (m.find()) {
                map.put(m.group(1), m.group(2));
            }
        }
        return map;
    }

    public String getConnector() {
        return connector;
    }

    public void setConnector(String connector) {
        this.connector = connector;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, String> getSink() {
        return sink;
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

    public Map<String, String> getDebezium() {
        return debezium;
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

    public void setSinks(List<Map<String, String>> sinks) {
        this.sinks = sinks;
    }

    public Map<String, String> getSource() {
        return source;
    }

    public void setSource(Map<String, String> source) {
        this.source = source;
    }

    public Map<String, String> getJdbc() {
        return jdbc;
    }

    public void setJdbc(Map<String, String> jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, String>> getSinks() {
        return sinks;
    }
}
