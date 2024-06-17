package org.dinky.data.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DashboardDTO {

    @ApiModelProperty(value = "ID", dataType = "Integer", example = "1", notes = "Unique identifier for the metrics")
    private Integer id;

    @NotNull(message = "name cannot be null")
    private String name;
    private String remark;
    @NotNull(message = "chartTheme cannot be null")
    private String chartTheme;
    private String layouts= "[]";
}
