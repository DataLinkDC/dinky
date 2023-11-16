package org.dinky.app.model;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(
        value = "ID",
        dataType = "Integer",
        example = "1",
        notes = "Unique identifier for the system configuration")
    private Integer id;

    @ApiModelProperty(value = "Configuration Name", dataType = "String", notes = "Name of the system configuration")
    private String name;

    @ApiModelProperty(value = "Configuration Value", dataType = "String", notes = "Value of the system configuration")
    private String value;
}
