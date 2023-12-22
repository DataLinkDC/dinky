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

package org.dinky.data.properties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "OssProperties", description = "Configuration Properties for Object Storage Service (OSS)")
@Builder
public class OssProperties {

    @ApiModelProperty(
            value = "Enable OSS",
            dataType = "boolean",
            notes = "Whether to enable OSS (Object Storage Service)",
            example = "true")
    private boolean enable;

    @ApiModelProperty(
            value = "OSS Endpoint",
            dataType = "String",
            notes = "URL of the Object Storage Service",
            example = "https://example.oss-cn-hangzhou.aliyuncs.com")
    private String endpoint;

    @ApiModelProperty(
            value = "Path Style Access",
            dataType = "Boolean",
            notes = "Path style access configuration (true for path-style, false for virtual-hosted-style)",
            example = "true")
    private Boolean pathStyleAccess;

    @ApiModelProperty(value = "Region", dataType = "String", notes = "Region for OSS", example = "oss-cn-hangzhou")
    private String region;

    @ApiModelProperty(
            value = "Access Key",
            dataType = "String",
            notes = "Access key for authentication",
            example = "your-access-key")
    private String accessKey;

    @ApiModelProperty(
            value = "Secret Key",
            dataType = "String",
            notes = "Secret key for authentication",
            example = "your-secret-key")
    private String secretKey;

    @ApiModelProperty(
            value = "Default Bucket Name",
            dataType = "String",
            notes = "Default bucket name",
            example = "my-bucket")
    private String bucketName;
}
