package com.zdpx.coder.operator;


import com.zdpx.coder.graph.DataType;
import com.zdpx.coder.graph.PseudoData;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TableInfo implements PseudoData<TableInfo> {
    private String name;

    private List<Column> columns = new ArrayList<>();

    public TableInfo(String name) {
        this.name = name;
    }

    public TableInfo(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    private TableInfo(Builder builder) {
        setName(builder.name);
        setColumns(builder.columns);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    //region g/s

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public DataType getType() {
        return DataType.TABLE;
    }

    //endregion

    public static final class Builder {
        private String name;
        private List<Column> columns;

        private Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder columns(List<Column> val) {
            columns = val;
            return this;
        }

        public TableInfo build() {
            return new TableInfo(this);
        }
    }


}
