package com.dlink.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Catalog
 *
 * @author wenmo
 * @since 2022/7/17 21:37
 */
@Getter
@Setter
public class Catalog implements Serializable {

    private static final long serialVersionUID = -7535759384541414568L;

    private String name;
    private List<Schema> schemas = new ArrayList<>();

    public Catalog() {
    }

    public Catalog(String name) {
        this.name = name;
    }

    public Catalog(String name, List<Schema> schemas) {
        this.name = name;
        this.schemas = schemas;
    }

    public static Catalog build(String name) {
        return new Catalog(name);
    }
}
