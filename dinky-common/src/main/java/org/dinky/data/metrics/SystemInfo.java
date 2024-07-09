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

package org.dinky.data.metrics;

import java.util.Map;
import java.util.Properties;

import lombok.Data;

@Data
public class SystemInfo {
    private String osName;
    private String osArch;
    private String osVersion;
    private String jvmVersion;
    private String jvmVendor;
    private String jvmName;
    private String computerName;
    private String computerUser;

    public static SystemInfo of() {
        SystemInfo system = new SystemInfo();
        Properties props = System.getProperties();
        system.setOsName(props.getProperty("os.name"));
        system.setOsArch(props.getProperty("os.arch"));
        system.setOsVersion(props.getProperty("os.version"));
        system.setJvmVersion(props.getProperty("java.vm.version"));
        system.setJvmVendor(props.getProperty("java.vm.vendor"));
        system.setJvmName(props.getProperty("java.vm.name"));
        Map<String, String> map = System.getenv();
        system.setComputerName(map.get("NAME"));
        system.setComputerUser(map.get("USER"));
        return system;
    }
}
