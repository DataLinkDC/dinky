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
import org.dinky.alert.AlertResult;
import org.dinky.alert.ShowType;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * DingTalkTest
 *
 * @since 2022/2/23 20:18
 */
@Ignore
public class DingTalkTest {

    private static final Map<String, String> config = new HashMap<>();

    String msg =
            "> The Dinky platform has detected an abnormality in your task. Please go to the Dinky Task page to check the task status.\n"
                    + "- **Job Name : <font color='#0000FF'>Test Job</font>**\n"
                    + "- **Job Status : <font color='#FF0000'>FAILED</font>**\n"
                    + "- **Alert Time : 2023-01-01  12:00:00**\n"
                    + "- **Start Time : 2023-01-01  12:00:00**\n"
                    + "- **End Time : 2023-01-01  12:00:00**\n"
                    + "> **<font color='#FF0000'>The test exception, your job exception will pass here</font>**\n"
                    + "\n"
                    + "> Dinky Team  [Go toTask Web](https://github.com/DataLinkDC/dinky)";

    @Before
    public void initDingTalkConfig() {

        config.put(DingTalkConstants.KEYWORD, "Dinky");
        config.put(DingTalkConstants.WEB_HOOK, "https://oapi.dingtalk.com/robot/send?access_token=key");
        config.put(DingTalkConstants.MSG_TYPE, ShowType.MARKDOWN.getValue());
        config.put(DingTalkConstants.AT_ALL, "false");
        config.put(DingTalkConstants.AT_MOBILES, "");
        //        config.put(DingTalkConstants.SECRET, "");

        config.put(DingTalkConstants.PROXY_ENABLE, "false");
        config.put(DingTalkConstants.PASSWORD, "password");
        config.put(DingTalkConstants.PORT, "9988");
        config.put(DingTalkConstants.USER, "user1,user2");
    }

    @Ignore
    @Test
    public void sendMarkDownMsgTest() {
        AlertConfig config = AlertConfig.build("MarkDownTest", "DingTalk", DingTalkTest.config);
        Alert alert = Alert.build(config);
        AlertResult result = alert.send("Dinky", msg);
        Assert.assertEquals(true, result.getSuccess());
    }
}
