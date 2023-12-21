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

package org.dinky.alert.http;

import org.dinky.alert.AlertBaseConstant;
import org.dinky.alert.AlertConfig;
import org.dinky.alert.AlertResult;
import org.dinky.alert.http.params.HttpParams;
import org.dinky.data.ext.ConfigItem;
import org.dinky.utils.JsonUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.hutool.json.JSONUtil;

public class HttpSenderTest {

    private static Map<String, Object> httpConfig = new HashMap<>();

    @Before
    public void initConfig() {
        HttpParams httpParams = new HttpParams();
        httpParams.setUrl("https://oapi.dingtalk.com/robot/send?access_token=xxxxxxxxxxxxxxxxx");
        httpParams.setMethod(HttpConstants.REQUEST_TYPE_POST);
        ConfigItem configItem = new ConfigItem("Content-Type", "application/json");
        httpParams.setHeaders(Arrays.asList(configItem));

        httpParams.setBody(" {\n" + "    \"msgtype\": \"markdown\",\n"
                + "    \"markdown\": {\n"
                + "        \"title\": \"http 测试\",\n"
                + "        \"text\": \"\"\n"
                + "    }\n"
                + "}");

        httpConfig = JsonUtils.toMap(JSONUtil.toJsonStr(httpParams), String.class, Object.class);
    }

    @Test
    public void sendTest() {
        HttpAlert httpAlert = new HttpAlert();
        AlertConfig alertConfig = new AlertConfig();
        alertConfig.setType(HttpConstants.TYPE);
        alertConfig.setParam(httpConfig);
        httpAlert.setConfig(alertConfig);
        AlertResult alertResult =
                httpAlert.send(AlertBaseConstant.ALERT_TEMPLATE_TITLE, AlertBaseConstant.ALERT_TEMPLATE_MSG);
        Assert.assertEquals(true, alertResult.getSuccess());
    }
}
