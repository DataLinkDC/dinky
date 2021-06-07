package com.dlink.executor.custom;

import org.apache.flink.table.types.DataType;

/**
 * @author  wenmo
 * @since  2021/6/7 22:06
 **/
public class TableSchemaField {
    private String name;
    private DataType type;

    public TableSchemaField(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }
}
