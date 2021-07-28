package com.dlink.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Schema
 *
 * @author wenmo
 * @since 2021/7/19 23:27
 */
@Getter
@Setter
public class Schema implements Serializable, Comparable<Schema> {

    private static final long serialVersionUID = 4278304357661271040L;

    private String name;
    private List<Table> tables = new ArrayList<>();

    public Schema(String name) {
        this.name = name;
    }

    public Schema(String name, List<Table> tables) {
        this.name = name;
        this.tables = tables;
    }

    @Override
    public int compareTo(Schema o) {
        return this.name.compareTo(o.getName());
    }
}