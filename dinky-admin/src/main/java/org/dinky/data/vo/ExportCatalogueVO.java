package org.dinky.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ExportCatalogueVO", description = "The return value of export catalogue")
public class ExportCatalogueVO {

    @ApiModelProperty(value = "FileName", dataType = "String", example = "data.json")
    private String fileName;

    @ApiModelProperty(value = "DataJson", dataType = "String")
    private String dataJson;

}
