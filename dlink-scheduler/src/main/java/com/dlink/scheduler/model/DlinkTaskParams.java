package com.dlink.scheduler.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 郑文豪
 */
@Data
public class DlinkTaskParams {
    @ApiModelProperty(value = "自定义参数")
    private List<Property> localParams;

    @ApiModelProperty(value = "dlink地址")
    private String address;

    @ApiModelProperty(value = "dlink任务id", required = true)
    private String taskId;
}
