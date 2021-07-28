package com.dlink.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * StudioCADTO
 *
 * @author wenmo
 * @since 2021/6/23 14:00
 **/
@Getter
@Setter
public class StudioCADTO {
    private String statement;
    /* 1:单表表级血缘
     * 2:单表字段血缘
     * 3.全局表级血缘
     * 4.全局字段血缘
     * */
    private Integer type;
}
