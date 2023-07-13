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

package org.dinky.gateway.config;

import org.dinky.data.constant.CommonConstant;

import lombok.Data;
import lombok.With;

/**
 * AppConfig
 *
 * @since 2021/11/3 21:55
 */
@Data
@With
public class AppConfig {

    private String userJarPath;
    private String[] userJarParas;
    private String userJarMainAppClass = CommonConstant.DINKY_APP_MAIN_CLASS;
}
