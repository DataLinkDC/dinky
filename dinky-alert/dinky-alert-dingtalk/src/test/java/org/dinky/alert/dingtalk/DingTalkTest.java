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

package org.dinky.alert.dingtalk;

import org.dinky.alert.Alert;
import org.dinky.alert.AlertConfig;
import org.dinky.alert.AlertMsg;
import org.dinky.alert.AlertResult;
import org.dinky.alert.ShowType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * DingTalkTest
 *
 * @author wenmo
 * @since 2022/2/23 20:18
 **/
public class DingTalkTest {

    private static Map<String, String> config = new HashMap<>();
    private AlertMsg alertMsg = new AlertMsg();
    @Before
    public void initDingTalkConfig() {
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

        config.put(DingTalkConstants.KEYWORD, "Dinky-Fink 钉钉告警测试");
        config.put(DingTalkConstants.WEB_HOOK, "url");
        config.put(DingTalkConstants.MSG_TYPE, ShowType.MARKDOWN.getValue());
        config.put(DingTalkConstants.AT_ALL, "true");

        config.put(DingTalkConstants.PROXY_ENABLE, "false");
        config.put(DingTalkConstants.PASSWORD, "password");
        config.put(DingTalkConstants.PORT, "9988");
        config.put(DingTalkConstants.USER, "user1,user2");
    }

    @Test
    public void sendMarkDownMsgTest() {
        AlertConfig config = AlertConfig.build("MarkDownTest", "DingTalk", DingTalkTest.config);
        Alert alert = Alert.build(config);
        AlertResult result = alert.send("Dinky钉钉告警测试", alertMsg.toString());
        Assert.assertEquals(true, result.getSuccess());
    }

    @Test
    public void sendTextMsgTest() {
        config.put(DingTalkConstants.MSG_TYPE, ShowType.TEXT.getValue());
        AlertConfig config = AlertConfig.build("TextMsgTest", "DingTalk", DingTalkTest.config);
        Alert alert = Alert.build(config);
        AlertResult result = alert.send("Dinky钉钉告警测试", alertMsg.toString());
        Assert.assertEquals(true, result.getSuccess());
    }

}
