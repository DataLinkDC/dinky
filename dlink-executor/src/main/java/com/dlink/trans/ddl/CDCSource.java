package com.dlink.trans.ddl;

import com.dlink.assertion.Asserts;
import com.dlink.parser.SingleSqlParserFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 *
 * @author wenmo
 * @since 2022/1/29 23:30
 */
public class CDCSource {

    private String statement;
    private String name;
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private Integer checkpoint;
    private Integer parallelism;
    private List<String> database;
    private List<String> table;
    private String topic;
    private String brokers;

    public CDCSource(String statement, String name, String hostname, Integer port, String username, String password, Integer checkpoint, Integer parallelism, String topic, String brokers) {
        this.statement = statement;
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.topic = topic;
        this.brokers = brokers;
    }

    public CDCSource(String statement, String name, String hostname, Integer port, String username, String password, Integer checkpoint, Integer parallelism, List<String> database, List<String> table, String topic, String brokers) {
        this.statement = statement;
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.database = database;
        this.table = table;
        this.topic = topic;
        this.brokers = brokers;
    }

    public static CDCSource build(String statement) {
        Map<String, List<String>> map = SingleSqlParserFactory.generateParser(statement);
        Map<String, String> config = getKeyValue(map.get("WITH"));
        CDCSource cdcSource = new CDCSource(statement,
                map.get("CDCSOURCE").toString(),
                config.get("hostname"),
                Integer.valueOf(config.get("port")),
                config.get("username"),
                config.get("password"),
                Integer.valueOf(config.get("checkpoint")),
                Integer.valueOf(config.get("parallelism")),
                config.get("topic"),
                config.get("brokers")
        );
        if(Asserts.isNotNullString(config.get("database"))){
            cdcSource.setDatabase(Arrays.asList(config.get("database").split(":")));
        }
        if(Asserts.isNotNullString(config.get("table"))){
            cdcSource.setTable(Arrays.asList(config.get("table").split(":")));
        }
        return cdcSource;
    }

    private static Map<String, String> getKeyValue(List<String> list) {
        Map<String, String> map = new HashMap<>();
        Pattern p = Pattern.compile("'(.*?)'\\s*=\\s*'(.*?)'");
        for (int i = 0; i < list.size(); i++) {
            Matcher m = p.matcher(list.get(i));
            if(m.find()){
                map.put(m.group(1),m.group(2));
            }
        }
        return map;
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

    public List<String> getDatabase() {
        return database;
    }

    public void setDatabase(List<String> database) {
        this.database = database;
    }

    public List<String> getTable() {
        return table;
    }

    public void setTable(List<String> table) {
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
}
