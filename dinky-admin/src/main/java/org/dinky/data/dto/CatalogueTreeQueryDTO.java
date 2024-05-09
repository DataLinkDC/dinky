/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * CatalogueTreeQueryDTO
 *
 * @since 2024/4/25 17:59
 */
@Getter
@Setter
@ToString
@ApiModel(value = "CatalogueTreeQueryDTO", description = "DTO for catalogue tree query")
public class CatalogueTreeQueryDTO {

    @ApiModelProperty(value = "Sort Value", dataType = "String", example = "create_time")
    private String sortValue;

    @ApiModelProperty(value = "Sort Type", dataType = "String", example = "asc")
    private String sortType;
}
