package com.dlink.model;

import com.dlink.assertion.Asserts;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SystemConfiguration
 *
 * @author wenmo
 * @since 2021/11/18
 **/
public class SystemConfiguration {

    private static volatile SystemConfiguration systemConfiguration = new SystemConfiguration();
    public static SystemConfiguration getInstances() {
        return systemConfiguration;
    }
    private static final List<Configuration> CONFIGURATION_LIST = new ArrayList<Configuration>(){{
        add(systemConfiguration.sqlSubmitJarPath);
        add(systemConfiguration.sqlSubmitJarParas);
        add(systemConfiguration.sqlSubmitJarMainAppClass);
        add(systemConfiguration.useRestAPI);
    }};

    private Configuration sqlSubmitJarPath = new Configuration(
            "sqlSubmitJarPath",
            "FlinkSQL提交Jar路径",
            ValueType.STRING,
            "hdfs:///dlink/jar/dlink-app.jar",
            "用于指定Applcation模式提交FlinkSQL的Jar的路径"
    );
    private Configuration sqlSubmitJarParas = new Configuration(
            "sqlSubmitJarParas",
            "FlinkSQL提交Jar参数",
            ValueType.STRING,
            "",
            "用于指定Applcation模式提交FlinkSQL的Jar的参数"
    );
    private Configuration sqlSubmitJarMainAppClass = new Configuration(
            "sqlSubmitJarMainAppClass",
            "FlinkSQL提交Jar主类",
            ValueType.STRING,
            "com.dlink.app.MainApp",
            "用于指定Applcation模式提交FlinkSQL的Jar的主类"
    );
    private Configuration useRestAPI = new Configuration(
            "useRestAPI",
            "使用 RestAPI",
            ValueType.BOOLEAN,
            true,
            "在运维 Flink 任务时是否使用 RestAPI"
    );

    public void setConfiguration(JsonNode jsonNode){
        for(Configuration item : CONFIGURATION_LIST){
            if(!jsonNode.has(item.getName())){
                continue;
            }
            switch (item.getType()){
                case BOOLEAN:
                    item.setValue(jsonNode.get(item.getName()).asBoolean());
                    break;
                case INT:
                    item.setValue(jsonNode.get(item.getName()).asInt());
                    break;
                default:
                    item.setValue(jsonNode.get(item.getName()).asText());
            }
        }
    }

    public void addConfiguration(Map<String,Object> map){
        for(Configuration item : CONFIGURATION_LIST){
            if(map.containsKey(item.getName())&&item.getType().equals(ValueType.BOOLEAN)){
                map.put(item.getName(), Asserts.isEqualsIgnoreCase("true",map.get(item.getName()).toString()));
            }
            if(!map.containsKey(item.getName())) {
                map.put(item.getName(), item.getValue());
            }
        }
    }

    public String getSqlSubmitJarParas() {
        return sqlSubmitJarParas.getValue().toString();
    }

    public void setSqlSubmitJarParas(String sqlSubmitJarParas) {
        this.sqlSubmitJarParas.setValue(sqlSubmitJarParas);
    }

    public String getSqlSubmitJarPath() {
        return sqlSubmitJarPath.getValue().toString();
    }

    public void setSqlSubmitJarPath(String sqlSubmitJarPath) {
        this.sqlSubmitJarPath.setValue(sqlSubmitJarPath);
    }

    public String getSqlSubmitJarMainAppClass() {
        return sqlSubmitJarMainAppClass.getValue().toString();
    }

    public void setSqlSubmitJarMainAppClass(String sqlSubmitJarMainAppClass) {
        this.sqlSubmitJarMainAppClass.setValue(sqlSubmitJarMainAppClass);
    }

    public boolean isUseRestAPI() {
        return (boolean) useRestAPI.getValue();
    }

    public void setUseRestAPI(boolean useRestAPI) {
        this.useRestAPI.setValue(useRestAPI);
    }

    enum ValueType{
        STRING,INT,DOUBLE,FLOAT,BOOLEAN,DATE
    }

    public class Configuration{
        private String name;
        private String label;
        private ValueType type;
        private Object defaultValue;
        private Object value;
        private String note;

        public Configuration(String name, String label, ValueType type, Object defaultValue, String note) {
            this.name = name;
            this.label = label;
            this.type = type;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.note = note;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public ValueType getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }
}
