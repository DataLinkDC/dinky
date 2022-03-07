package com.dlink.alert;

/**
 * AlertMsg
 *
 * @author wenmo
 * @since 2022/3/7 18:30
 **/
public class AlertMsg {
    private String type;
    private String time;
    private String id;
    private String name;
    private String status;
    private String content;

    public AlertMsg() {
    }

    public AlertMsg(String type, String time, String id, String name, String status, String content) {
        this.type = type;
        this.time = time;
        this.id = id;
        this.name = name;
        this.status = status;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
