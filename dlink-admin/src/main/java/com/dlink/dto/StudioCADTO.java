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
public class StudioCADTO extends AbstractStatementDTO {
    // It's useless for the time being
    private Boolean statementSet;
    private Integer type;
}
