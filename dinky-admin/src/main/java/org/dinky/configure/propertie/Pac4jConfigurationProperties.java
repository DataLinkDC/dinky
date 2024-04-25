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

package org.dinky.configure.propertie;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * The pac4j configuration and callback URL.
 *
 * @author yangzehan
 *
 */
@ConfigurationProperties(prefix = "pac4j", ignoreUnknownFields = false)
@Getter
@Setter
public class Pac4jConfigurationProperties {
    private Map<String, String> properties = new LinkedHashMap<>();
    private Map<String, String> callback = new LinkedHashMap<>();
    private Map<String, String> centralLogout = new LinkedHashMap<>();
    private Map<String, String> logout = new LinkedHashMap<>();
    private String callbackUrl;
}
