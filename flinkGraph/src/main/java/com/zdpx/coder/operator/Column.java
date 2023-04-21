package com.zdpx.coder.operator;

/**
 * 表示库列信息
 *
 * @author Licho Sun
 */
public class Column {
    private String name;
    private String type;

    public Column() {
    }

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }

    //region g/s
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    //endregion
}
