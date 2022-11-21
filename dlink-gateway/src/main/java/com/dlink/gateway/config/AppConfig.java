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

package com.dlink.gateway.config;

import com.dlink.assertion.Asserts;

import lombok.Getter;
import lombok.Setter;

/**
 * AppConfig
 *
 * @author wenmo
 * @since 2021/11/3 21:55
 */
@Setter
@Getter
public class AppConfig {
    private String userJarPath;
    private String[] userJarParas;
    private String userJarMainAppClass;
    private Integer parallelism;

    public AppConfig() {
    }

    public AppConfig(String userJarPath, String[] userJarParas, String userJarMainAppClass) {
        this.userJarPath = userJarPath;
        this.userJarParas = userJarParas;
        this.userJarMainAppClass = userJarMainAppClass;
    }

    public static AppConfig build(String userJarPath, String userJarParasStr, String userJarMainAppClass) {
        if (Asserts.isNotNullString(userJarParasStr)) {
            return new AppConfig(userJarPath, userJarParasStr.split(" "), userJarMainAppClass);
        } else {
            return new AppConfig(userJarPath, new String[]{}, userJarMainAppClass);

        }
    }
}
