package com.dlink.scheduler.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * task definition log
 */
@Data
public class TaskDefinitionLog extends TaskDefinition {

    @ApiModelProperty(value = "操作人")
    private Integer operator;

    @ApiModelProperty(value = "操作时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

}
