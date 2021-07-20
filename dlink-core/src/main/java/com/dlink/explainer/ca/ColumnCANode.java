package com.dlink.explainer.ca;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * ColumnCANode
 *
 * @author wenmo
 * @since 2021/6/23 11:03
 **/
@Getter
@Setter
public class ColumnCANode implements Serializable {
    private static final long serialVersionUID = 122624200268430762L;
    private Integer id;
    private Integer tableId;
    private String name;
    private String title;
    private String value;
    private String type;
    private String operation;
//    private Tables tables;
//    private Columns columns;
    private List<ColumnCANode> children;

    public ColumnCANode() {
    }


}
