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

package org.dinky.metadata.config;

/**
 * 配置类 S3Configuration 用于存储 S3 配置信息
 *
 */
public class PaimonS3Configuration {
    public static String ACCESS_KEY = "s3.access-key";
    public static String SECRET_KEY = "s3.secret-key";
    public static String ENDPOINT = "s3.endpoint";
    public static String BUCKET_NAME = "s3.bucket-name";
    public static String PATH_STYLE_ACCESS = "s3.path.style.access";
    public static String REGION = "s3.region";
}
