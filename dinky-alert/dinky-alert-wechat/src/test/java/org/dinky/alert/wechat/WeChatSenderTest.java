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

package org.dinky.alert.wechat;

import org.dinky.alert.AlertResult;
import org.dinky.alert.ShowType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** WeChatSenderTest */
public class WeChatSenderTest {

    private static Map<String, String> weChatConfig = new HashMap<>();

    private String contentTest =
            "[{\"id\":\"70\","
                    + "\"name\":\"UserBehavior-0--1193959466\","
                    + "\"Job name\":\"Start workflow\","
                    + "\"State\":\"SUCCESS\","
                    + "\"Recovery\":\"NO\","
                    + "\"Run time\":\"1\","
                    + "\"Start time\": \"2018-08-06 10:31:34.0\","
                    + "\"End time\": \"2018-08-06 10:31:49.0\","
                    + "\"Host\": \"192.168.xx.xx\","
                    + "\"Notify group\" :\"4\"}]";

    /** init WeChatConfig */
    @Before
    public void initWeChatConfig() {
        // Just for this test, I will delete these configurations before this PR is merged
        weChatConfig.put(WeChatConstants.AGENT_ID, "AGENT_ID");
        weChatConfig.put(WeChatConstants.SECRET, "SECRET");
        weChatConfig.put(WeChatConstants.CORP_ID, "CORP_ID");
        weChatConfig.put(WeChatConstants.CHARSET, "UTF-8");
        weChatConfig.put(
                WeChatConstants.WECHAT_APP_TEMPLATE,
                "{\"touser\":\"{toUser}\",\"agentid\":{agentId}"
                        + ",\"msgtype\":\"{showType}\",\"{showType}\":{\"content\":\"{msg}\"}}");
        weChatConfig.put(WeChatConstants.AT_USERS, "all");
        weChatConfig.put(WeChatConstants.TEAM_SEND_MSG, "msg");
        weChatConfig.put(
                WeChatConstants.MSG_TYPE, ShowType.MARKDOWN.getValue()); // default is "table"
        weChatConfig.put(WeChatConstants.SEND_TYPE, WeChatType.APP.getValue());
    }

    @Ignore
    @Test
    public void testSendAPPMarkDownMsg() {
        WeChatSender weChatSender = new WeChatSender(weChatConfig);
        AlertResult alertResult = weChatSender.send("Dinky企微APP MarkDown方式 告警测试", contentTest);
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Ignore
    @Test
    public void testSendAPPTextMsg() {
        weChatConfig.put(WeChatConstants.MSG_TYPE, ShowType.TEXT.getValue());
        WeChatSender weChatSender = new WeChatSender(weChatConfig);
        AlertResult alertResult = weChatSender.send("Dinky企微APP TEXT方式 告警测试", contentTest);
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Ignore
    @Test
    public void testChatMarkDownMsg() throws IOException {
        weChatConfig.put(
                WeChatConstants.WEB_HOOK,
                "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=8xxxxxxxxxxxxxxxxx6fe13396c");
        weChatConfig.put(WeChatConstants.SEND_TYPE, WeChatType.CHAT.getValue());
        weChatConfig.put(
                WeChatConstants.WECHAT_APP_TEMPLATE, WeChatConstants.WECHAT_WEBHOOK_TEMPLATE);
        weChatConfig.put(WeChatConstants.MSG_TYPE, ShowType.MARKDOWN.getValue());
        weChatConfig.put(WeChatConstants.KEYWORD, "Dinky企微WEBHOOK  MarkDown方式 告警测试");
        WeChatSender weChatSender = new WeChatSender(weChatConfig);
        AlertResult alertResult = weChatSender.send("TEXT-TEST", contentTest);
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Ignore
    @Test
    public void testChatTextMsg() throws IOException {
        weChatConfig.put(
                WeChatConstants.WEB_HOOK,
                "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=822d17a1-d6e5-43c2-a566-4846fe13396c");
        weChatConfig.put(WeChatConstants.SEND_TYPE, WeChatType.CHAT.getValue());
        weChatConfig.put(
                WeChatConstants.WECHAT_APP_TEMPLATE, WeChatConstants.WECHAT_WEBHOOK_TEMPLATE);
        weChatConfig.put(WeChatConstants.MSG_TYPE, ShowType.TEXT.getValue());
        weChatConfig.put(WeChatConstants.KEYWORD, "Dinky企微WEBHOOK  TEXT方式 告警测试");
        WeChatSender weChatSender = new WeChatSender(weChatConfig);
        AlertResult alertResult = weChatSender.send("TEXT-TEST", contentTest);
        Assert.assertEquals(true, alertResult.getSuccess());
    }
}
