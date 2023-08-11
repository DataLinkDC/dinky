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

package org.dinky.executor;

import org.dinky.assertion.Asserts;
import org.dinky.data.constant.NetConstant;

import lombok.Getter;
import lombok.Setter;

/**
 * EnvironmentSetting
 *
 * @since 2021/5/25 13:45
 */
@Getter
@Setter
public class EnvironmentSetting {

    private String host;
    private Integer port;
    private String[] jarFiles;

    private boolean useRemote;

    public static final EnvironmentSetting LOCAL = new EnvironmentSetting(false);

    public EnvironmentSetting(boolean useRemote) {
        this.useRemote = useRemote;
    }

    public EnvironmentSetting(String... jarFiles) {
        this.useRemote = false;
        this.jarFiles = jarFiles;
    }

    public EnvironmentSetting(String host, Integer port, String... jarFiles) {
        this.host = host;
        this.port = port;
        this.useRemote = true;
        this.jarFiles = jarFiles;
    }

    public static EnvironmentSetting build(String address, String... jarFiles) {
        Asserts.checkNull(address, "Flink 地址不能为空");
        String[] strs = getHostAndPort(address);
        if (strs.length >= 2) {
            return new EnvironmentSetting(strs[0], Integer.parseInt(strs[1]), jarFiles);
        } else {
            return new EnvironmentSetting(jarFiles);
        }
    }

    /**
     * 修复地址，解决无只有域名无端口的情况,具体情况，请看测试用例
     *
     * @param address flink地址（前端配置）
     * @return String[]{域名，端口}
     */
    static String[] getHostAndPort(String address) {
        address = address.toLowerCase().replace("://", "//");
        String[] strs = address.split(NetConstant.COLON);

        // 兼容直接填写域名情况(走k8s ingress时没有端口号)
        if (address.startsWith("http//")) {
            String port = strs.length == 1 ? "80" : strs[1];
            strs = new String[] {strs[0].replace("http//", ""), port};
        }
        if (address.startsWith("https//")) {
            String port = strs.length == 1 ? "443" : strs[1];
            strs = new String[] {strs[0].replace("https//", ""), port};
        }
        // 如果没有填端口，则默认80
        if (strs.length == 1) {
            strs = new String[] {address, "80"};
        }
        return strs;
    }

    public String getAddress() {
        if (Asserts.isAllNotNull(host, port)) {
            return host + NetConstant.COLON + port;
        } else {
            return "";
        }
    }
}
