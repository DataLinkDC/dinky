package org.dinky.data.model.ext;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@ApiModel(value = "TaskUdfRefer", description = "UDF (User-Defined Function) refer for Task")
@AllArgsConstructor
@NoArgsConstructor
public class TaskUdfRefer implements Serializable {

    @ApiModelProperty(value = "function name", dataType = "String", example = "add", notes = "Nmae of the UDF function")
    private String name;

    @ApiModelProperty(value = "Class Name", dataType = "String", notes = "Name of the UDF class")
    private String className;
}
