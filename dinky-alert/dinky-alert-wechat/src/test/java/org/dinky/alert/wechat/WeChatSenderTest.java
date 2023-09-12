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

import org.dinky.alert.AlertConfig;
import org.dinky.alert.AlertResult;
import org.dinky.alert.ShowType;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** WeChatSenderTest */
@Ignore
public class WeChatSenderTest {

    private static final Map<String, String> weChatConfig = new HashMap<>();

    String contentTest = "# Test Wechat Hook Robot Alert123 \n > test content \n normal";

    /** init WeChatConfig */
    @Before
    public void initWeChatConfig() {
        // Just for this test, I will delete these configurations before this PR is merged
        weChatConfig.put(WeChatConstants.AGENT_ID, "1000002");
        weChatConfig.put(WeChatConstants.SECRET, "V2w-9JDqSrF2wVW0eno6Vnrxbe6WZMHvO1Z1Hwj3JGg");
        weChatConfig.put(WeChatConstants.CORP_ID, "ww4ae244b25dda07cb");
        weChatConfig.put(WeChatConstants.CHARSET, "UTF-8");
        weChatConfig.put(WeChatConstants.AT_USERS, "MoWuHen");
        weChatConfig.put(WeChatConstants.TEAM_SEND_MSG, "msg");
        weChatConfig.put(WeChatConstants.MSG_TYPE, ShowType.MARKDOWN.getValue()); // default is "table"
        weChatConfig.put(
                WeChatConstants.WEB_HOOK,
                "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=32858c29-379b-4080-80d6-d7f72f2f1d4d");
    }

    @Ignore
    @Test
    public void testSendMarkDownMsg() {

        WeChatAlert weChatAlert = new WeChatAlert();
        AlertConfig alertConfig = new AlertConfig();
        weChatConfig.put(WeChatConstants.SEND_TYPE, WeChatType.CHAT.getValue());

        alertConfig.setType("WeChat");
        alertConfig.setParam(weChatConfig);
        weChatAlert.setConfig(alertConfig);

        AlertResult alertResult = weChatAlert.send("WeChat Alert Hook", contentTest);
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Ignore
    @Test
    public void testSendAPPMarkDownMsg() {

        WeChatAlert weChatAlert = new WeChatAlert();
        AlertConfig alertConfig = new AlertConfig();
        weChatConfig.put(WeChatConstants.SEND_TYPE, WeChatType.APP.getValue());

        alertConfig.setType("WeChat");
        alertConfig.setParam(weChatConfig);
        weChatAlert.setConfig(alertConfig);

        AlertResult alertResult = weChatAlert.send("WeChat Alert App", contentTest);
        Assert.assertEquals(true, alertResult.getSuccess());
    }
}
