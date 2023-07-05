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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OssProperties {

    /** 配置前缀 */
    public static final String PREFIX = "oss";

    /** 是否启用 oss，默认为：true */
    private boolean enable = true;

    /** 对象存储服务的URL */
    private String endpoint;

    /** 自定义域名 */
    private String customDomain;

    /**
     * true path-style nginx 反向代理和S3默认支持 pathStyle {http://endpoint/bucketname} false supports
     * virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style 模式{http://bucketname.endpoint}
     */
    private Boolean pathStyleAccess = true;

    /** 区域 */
    private String region;

    /** Access key就像用户ID，可以唯一标识你的账户 */
    private String accessKey;

    /** Secret key是你账户的密码 */
    private String secretKey;

    /** 默认的存储桶名称 */
    private String bucketName;
}
