package com.dlink.explainer.lineage;

import java.util.List;

/**
 * LineageTable
 *
 * @author wenmo
 * @since 2022/3/15 22:55
 */
public class LineageTable {
    private String id;
    private String name;
    private List<LineageColumn> columns;

    public LineageTable() {
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

    public List<LineageColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<LineageColumn> columns) {
        this.columns = columns;
    }
}
