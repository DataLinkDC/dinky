package com.dlink.model;

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
    private String startupMode;
    private String topic;
    private String brokers;

    public FlinkCDCConfig() {
    }

    public FlinkCDCConfig(String type, String hostname, int port, String username, String password, int checkpoint, int parallelism, String database, String schema, String table, String startupMode,
                          String topic, String brokers) {
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
        this.topic = topic;
        this.brokers = brokers;
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

    public void setTable(String table) {
        this.table = table;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }

    public String getStartupMode() {
        return startupMode;
    }

    public void setStartupMode(String startupMode) {
        this.startupMode = startupMode;
    }
}
