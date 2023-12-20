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

package org.dinky.alert.feishu;

import org.dinky.alert.AlertBaseConstant;
import org.dinky.alert.AlertConfig;
import org.dinky.alert.AlertResult;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** @Author: zhumingye */
@Ignore
public class FeiShuSenderTest {

    private static Map<String, Object> feiShuConfig = new HashMap<>();

    @Before
    public void initFeiShuConfig() {
        feiShuConfig.put(FeiShuConstants.WEB_HOOK, "https://open.feishu.cn/open-apis/bot/v2/hook/key");
        feiShuConfig.put(FeiShuConstants.KEYWORD, "Dinky");
        feiShuConfig.put(FeiShuConstants.AT_ALL, "false");
        feiShuConfig.put(FeiShuConstants.AT_USERS, "gaoyan");
    }

    @Ignore
    @Test
    public void testSend() {

        FeiShuAlert feiShuAlert = new FeiShuAlert();
        AlertConfig alertConfig = new AlertConfig();

        alertConfig.setType("FeiShu");
        alertConfig.setParam(feiShuConfig);
        feiShuAlert.setConfig(alertConfig);

        AlertResult alertResult =
                feiShuAlert.send(AlertBaseConstant.ALERT_TEMPLATE_TITLE, AlertBaseConstant.ALERT_TEMPLATE_MSG);
        Assert.assertEquals(true, alertResult.getSuccess());
    }
}
