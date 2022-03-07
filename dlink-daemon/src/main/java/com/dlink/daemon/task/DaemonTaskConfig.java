package com.dlink.daemon.task;


public class DaemonTaskConfig {

    private String type;
    private Integer id;

    public DaemonTaskConfig() {
    }

    public DaemonTaskConfig(String type, Integer id) {
        this.type = type;
        this.id = id;
    }

    public static DaemonTaskConfig build(String type,Integer id){
        return new DaemonTaskConfig(type,id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
