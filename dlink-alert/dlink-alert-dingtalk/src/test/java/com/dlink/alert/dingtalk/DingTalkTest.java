package com.dlink.alert.dingtalk;

import com.dlink.alert.Alert;
import com.dlink.alert.AlertConfig;
import com.dlink.alert.AlertResult;
import com.dlink.alert.ShowType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * DingTalkTest
 *
 * @author wenmo
 * @since 2022/2/23 20:18
 **/
public class DingTalkTest {

    private static Map<String, String> config = new HashMap<>();
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

    @Before
    public void initDingTalkConfig() {

        config.put(DingTalkConstants.KEYWORD, "Dlinky-Fink 钉钉告警测试");
        config.put(DingTalkConstants.WEB_HOOK, "url");
        config.put(DingTalkConstants.MSG_TYPE, ShowType.TABLE.getValue());

        config.put(DingTalkConstants.PROXY_ENABLE, "false");
        config.put(DingTalkConstants.PASSWORD, "password");
        config.put(DingTalkConstants.PORT, "9988");
        config.put(DingTalkConstants.USER, "user1,user2");
    }

    @Test
    public void sendMarkDownMsgTest() {
        AlertConfig config = AlertConfig.build("MarkDownTest", "DingTalk", DingTalkTest.config);
        Alert alert = Alert.build(config);
        AlertResult result = alert.send("Dlinky钉钉告警测试", contentTest);
        Assert.assertEquals(true, result.getSuccess());
    }

    @Test
    public void sendTextMsgTest() {
        config.put(DingTalkConstants.MSG_TYPE, ShowType.TEXT.getValue());
        AlertConfig config = AlertConfig.build("TextMsgTest", "DingTalk", DingTalkTest.config);
        Alert alert = Alert.build(config);
        AlertResult result = alert.send("Dlinky钉钉告警测试", contentTest);
        Assert.assertEquals(true, result.getSuccess());
    }

}
