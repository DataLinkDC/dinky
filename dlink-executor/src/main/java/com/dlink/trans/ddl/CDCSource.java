package com.dlink.trans.ddl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dlink.assertion.Asserts;
import com.dlink.parser.SingleSqlParserFactory;

/**
 * CDCSource
 *
 * @author wenmo
 * @since 2022/1/29 23:30
 */
public class CDCSource {

    private String type;
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
    private String topic;
    private String brokers;

    public CDCSource(String type, String statement, String name, String hostname, Integer port, String username, String password, Integer checkpoint, Integer parallelism, String startupMode,
                     String topic, String brokers) {
        this.type = type;
        this.statement = statement;
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.startupMode = startupMode;
        this.topic = topic;
        this.brokers = brokers;
    }

    public static CDCSource build(String statement) {
        Map<String, List<String>> map = SingleSqlParserFactory.generateParser(statement);
        Map<String, String> config = getKeyValue(map.get("WITH"));
        CDCSource cdcSource = new CDCSource(
            config.get("type"),
            statement,
            map.get("CDCSOURCE").toString(),
            config.get("hostname"),
            Integer.valueOf(config.get("port")),
            config.get("username"),
            config.get("password"),
            Integer.valueOf(config.get("checkpoint")),
            Integer.valueOf(config.get("parallelism")),
            config.get("startup"),
            config.get("topic"),
            config.get("brokers")
        );
        if (Asserts.isNotNullString(config.get("database"))) {
            cdcSource.setDatabase(config.get("database"));
        }
        if (Asserts.isNotNullString(config.get("schema"))) {
            cdcSource.setSchema(config.get("schema"));
        }
        if (Asserts.isNotNullString(config.get("table"))) {
            cdcSource.setTable(config.get("table"));
        }
        return cdcSource;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
