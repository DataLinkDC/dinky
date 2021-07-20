package com.dlink.explainer.ca;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * TableCANode
 *
 * @author wenmo
 * @since 2021/6/23 11:03
 **/
@Getter
@Setter
public class TableCANode implements Serializable {
    private static final long serialVersionUID = 356665302973181483L;
    private String id;
    private Integer tableId;
    private String name;
    private String title;
    private String value;
    private String type;
    private Integer columnSize;
//    private Tables tables;
    private List<String> columns;
    private List<TableCANode> children;

    public TableCANode() {
    }

    public TableCANode(String name) {
        this.name = name;
    }

    public TableCANode(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public TableCANode(Integer id,String name, List<String> columns) {
        this.id = id.toString();
        this.name = name;
        this.title = name;
        this.columnSize = columns.size();
        this.columns = columns;
    }
}
