package com.dlink.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Table
 *
 * @author wenmo
 * @since 2021/7/19 23:27
 */
@Getter
@Setter
public class Table implements Serializable, Comparable<Table> {

    private static final long serialVersionUID = 4209205512472367171L;

    private String name;
    private String schema;
    private String comment;
    private List<Column> columns;

    public Table() {
    }

    public Table(String name, String schema, List<Column> columns) {
        this.name = name;
        this.schema = schema;
        this.columns = columns;
    }

    @Override
    public int compareTo(Table o) {
        return this.name.compareTo(o.getName());
    }

    public static Table build(String name) {
        return new Table(name, null, null);
    }

    public static Table build(String name, String schema) {
        return new Table(name, schema, null);
    }

    public static Table build(String name, String schema, List<Column> columns) {
        return new Table(name, schema, columns);
    }
}
