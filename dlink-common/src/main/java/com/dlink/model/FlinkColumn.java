package com.dlink.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * FlinkColumn
 *
 * @author wenmo
 * @since 2022/7/18 19:55
 **/
@Getter
@Setter
public class FlinkColumn implements Serializable {
    private static final long serialVersionUID = 4820196727157711974L;

    private int position;
    private String name;
    private String type;
    private String key;
    private String nullable;
    private String extras;
    private String watermark;

    public FlinkColumn() {
    }

    public FlinkColumn(int position, String name, String type, String key, String nullable, String extras, String watermark) {
        this.position = position;
        this.name = name;
        this.type = type;
        this.key = key;
        this.nullable = nullable;
        this.extras = extras;
        this.watermark = watermark;
    }

    public static FlinkColumn build(int position, String name, String type, String key, String nullable, String extras, String watermark) {
        return new FlinkColumn(position, name, type, key, nullable, extras, watermark);
    }
}
