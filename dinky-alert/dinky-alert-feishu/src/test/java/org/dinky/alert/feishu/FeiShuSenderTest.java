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

import org.dinky.alert.AlertMsg;
import org.dinky.alert.AlertResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @Author: zhumingye
 *
 * @date: 2022/4/2 @Description: 飞书消息发送 单元测试
 */
public class FeiShuSenderTest {

    private static Map<String, String> feiShuConfig = new HashMap<>();
    private AlertMsg alertMsg = new AlertMsg();

    @Before
    public void initFeiShuConfig() {
        String uuid = UUID.randomUUID().toString();

        alertMsg.setAlertType("实时告警监控");
        alertMsg.setAlertTime("2018-08-06 10:31:34.0");
        alertMsg.setJobID(uuid);
        alertMsg.setJobName("测试任务");
        alertMsg.setJobType("SQL");
        alertMsg.setJobStatus("FAILED");
        alertMsg.setJobStartTime("2018-08-06 10:31:34.0");
        alertMsg.setJobEndTime("2018-08-06 10:31:49.0");
        alertMsg.setJobDuration("23 Seconds");
        String linkUrl = "[跳转至该任务的FlinkWeb](http://cdh1:8081/#/job/" + uuid + "/overview)";
        alertMsg.setLinkUrl(linkUrl);
        String exceptionUrl = "[点击查看该任务的异常日志](http://cdh1:8081/#/job/" + uuid + "/exceptions)";
        alertMsg.setExceptionUrl(exceptionUrl);

        feiShuConfig.put(
                FeiShuConstants.WEB_HOOK,
                "https://open.feishu.cn/open-apis/bot/v2/hook/aea3cd7f-75b4-45cd-abea-2c0dc808f2a9");
        feiShuConfig.put(FeiShuConstants.KEYWORD, "Dinky 飞书WebHook 告警测试");
        feiShuConfig.put(FeiShuConstants.MSG_TYPE, "text");
        feiShuConfig.put(FeiShuConstants.AT_ALL, "true");
        feiShuConfig.put(FeiShuConstants.AT_USERS, "zhumingye");
    }

    @Ignore
    @Test
    public void testTextTypeSend() {
        FeiShuSender feiShuSender = new FeiShuSender(feiShuConfig);
        AlertResult alertResult = feiShuSender.send("FeiShu Alert", alertMsg.toString());
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Ignore
    @Test
    public void testPostTypeSend() {
        feiShuConfig.put(FeiShuConstants.MSG_TYPE, "post");
        FeiShuSender feiShuSender = new FeiShuSender(feiShuConfig);
        AlertResult alertResult = feiShuSender.send("FeiShu Alert", alertMsg.toString());
        Assert.assertEquals(true, alertResult.getSuccess());
    }
}
