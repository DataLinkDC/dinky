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

package org.dinky.data.model;

import org.dinky.mybatis.model.SuperEntity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Document
 *
 * @since 2021/6/3 14:27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_flink_document")
@ApiModel(value = "Document", description = "Document manage")
public class Document extends SuperEntity<Document> {

    private static final long serialVersionUID = -6340080980759236641L;

    @ApiModelProperty(value = "Category Type", required = true, example = "1", notes = "Category Type description")
    private String category;

    @ApiModelProperty(
            value = "Type",
            example = "ExampleType",
            notes = "Type description",
            dataType = "String",
            required = true)
    private String type;

    @ApiModelProperty(
            value = "Subtype",
            example = "ExampleSubtype",
            notes = "Subtype description",
            dataType = "String",
            required = true)
    private String subtype;

    @ApiModelProperty(
            value = "Description",
            example = "ExampleDescription",
            notes = "Description description",
            dataType = "String",
            required = true)
    private String description;

    @ApiModelProperty(
            value = "Version",
            example = "ExampleVersion",
            notes = "Version description",
            dataType = "String",
            required = true)
    private String version;

    @ApiModelProperty(
            value = "Fill Value",
            example = "ExampleFillValue",
            notes = "Fill Value description",
            dataType = "String",
            required = true)
    private String fillValue;

    @ApiModelProperty(
            value = "Like Num",
            example = "ExampleLikeNum",
            notes = "Like Num description",
            dataType = "String",
            required = true)
    private String likeNum;
}
