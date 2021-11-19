package com.dlink.model;

import com.fasterxml.jackson.databind.JsonNode;

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

    public void setConfiguration(JsonNode jsonNode){
        if(jsonNode.has("sqlSubmitJarPath")){
            setSqlSubmitJarPath(jsonNode.get("sqlSubmitJarPath").asText());
        }
        if(jsonNode.has("sqlSubmitJarParas")){
            setSqlSubmitJarParas(jsonNode.get("sqlSubmitJarParas").asText());
        }
        if(jsonNode.has("sqlSubmitJarMainAppClass")){
            setSqlSubmitJarMainAppClass(jsonNode.get("sqlSubmitJarMainAppClass").asText());
        }
    }

    public void addConfiguration(Map<String,String> map){
        if(!map.containsKey("sqlSubmitJarPath")){
            map.put("sqlSubmitJarPath",sqlSubmitJarPath.getValue().toString());
        }
        if(!map.containsKey("sqlSubmitJarParas")){
            map.put("sqlSubmitJarParas",sqlSubmitJarParas.getValue().toString());
        }
        if(!map.containsKey("sqlSubmitJarMainAppClass")){
            map.put("sqlSubmitJarMainAppClass",sqlSubmitJarMainAppClass.getValue().toString());
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
    }
}
