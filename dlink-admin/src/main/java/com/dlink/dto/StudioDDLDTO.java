package com.dlink.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * StudioDDLDTO
 *
 * @author wenmo
 * @since 2021/6/3
 */
@Getter
@Setter
public class StudioDDLDTO {
    private String session;
    private String statement;
    private Integer clusterId=0;
}
