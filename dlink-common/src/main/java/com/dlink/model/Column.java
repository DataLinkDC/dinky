package com.dlink.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Column
 *
 * @author wenmo
 * @since 2021/7/19 23:26
 */
@Setter
@Getter
public class Column implements Serializable {

    private static final long serialVersionUID = 6438514547501611599L;

    private String name;
    private String type;
    private String comment;
    private boolean keyFlag;
    /**
     * 主键是否为自增类型
     */
    private boolean keyIdentityFlag;
    private String fill;
    private String isNotNull;
    private String javaType;
    private String columnFamily;

}