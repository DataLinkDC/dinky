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


package com.dlink.executor;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkConstant;
import com.dlink.constant.NetConstant;
import lombok.Getter;
import lombok.Setter;

/**
 * EnvironmentSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:45
 **/
@Getter
@Setter
public class EnvironmentSetting {
    private String host;
    private int port;
    private boolean useRemote;

    public static final EnvironmentSetting LOCAL = new EnvironmentSetting(false);

    public EnvironmentSetting(boolean useRemote) {
        this.useRemote = useRemote;
    }

    public EnvironmentSetting(String host, int port) {
        this.host = host;
        this.port = port;
        this.useRemote = true;
    }

    public static EnvironmentSetting build(String address) {
        Asserts.checkNull(address, "Flink 地址不能为空");
        String[] strs = address.split(NetConstant.COLON);
        if (strs.length >= 2) {
            return new EnvironmentSetting(strs[0], Integer.parseInt(strs[1]));
        } else {
            return new EnvironmentSetting(strs[0], FlinkConstant.FLINK_REST_DEFAULT_PORT);
        }
    }

    public String getAddress() {
        return host + NetConstant.COLON + port;
    }

}
