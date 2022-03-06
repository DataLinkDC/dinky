/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dlink.alert.wechat;

import com.dlink.alert.AlertResult;
import com.dlink.alert.ShowType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * WeChatSenderTest
 */
public class WeChatSenderTest {

    private static Map<String, String> weChatConfig = new HashMap<>();

    private String contentTest = "[{\"id\":\"70\","
            +
            "\"name\":\"UserBehavior-0--1193959466\","
            +
            "\"Job name\":\"Start workflow\","
            +
            "\"State\":\"SUCCESS\","
            +
            "\"Recovery\":\"NO\","
            +
            "\"Run time\":\"1\","
            +
            "\"Start time\": \"2018-08-06 10:31:34.0\","
            +
            "\"End time\": \"2018-08-06 10:31:49.0\","
            +
            "\"Host\": \"192.168.xx.xx\","
            +
            "\"Notify group\" :\"4\"}]";

    /**
     * init WeChatConfig
     */
    @Before
    public void initWeChatConfig() {
        // Just for this test, I will delete these configurations before this PR is merged
        weChatConfig.put(WeChatConstants.AGENT_ID, "AGENT_ID");
        weChatConfig.put(WeChatConstants.SECRET, "SECRET");
        weChatConfig.put(WeChatConstants.CORP_ID, "CORP_ID");
        weChatConfig.put(WeChatConstants.CHARSET, "UTF-8");
        weChatConfig.put(WeChatConstants.USER_SEND_MSG, "{\"touser\":\"{toUser}\",\"agentid\":{agentId}"
                +
                ",\"msgtype\":\"{showType}\",\"{showType}\":{\"content\":\"{msg}\"}}"
        );
        weChatConfig.put(WeChatConstants.USERS, "USERS");
        weChatConfig.put(WeChatConstants.TEAM_SEND_MSG, "msg");
        weChatConfig.put(WeChatConstants.SHOW_TYPE, ShowType.TABLE.getValue());// default is "table"
        weChatConfig.put(WeChatConstants.SEND_TYPE, WeChatType.APP.getValue());

    }


    @Test
    public void testSendWeChatTableMsg() {
        WeChatSender weChatSender = new WeChatSender(weChatConfig);
        AlertResult alertResult = weChatSender.send("TABLE-TEST", contentTest);
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Test
    public void testSendWeChatTextMsg() {
        weChatConfig.put(WeChatConstants.SHOW_TYPE,ShowType.TEXT.getValue());
        WeChatSender weChatSender = new WeChatSender(weChatConfig);
        AlertResult alertResult = weChatSender.send("TEXT-TEST", contentTest);
        Assert.assertEquals(true, alertResult.getSuccess());
    }
}
