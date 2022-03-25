package com.dlink.dto;

import lombok.Getter;
import lombok.Setter;

import com.dlink.gateway.config.SavePointType;

/**
 * APISavePointTaskDTO
 *
 * @author wenmo
 * @since 2022/03/25 19:05
 */
@Getter
@Setter
public class APISavePointTaskDTO {
    private Integer taskId;
    private String type = SavePointType.TRIGGER.getValue();

}
