package com.dlink.alert.dingtalk;

import com.dlink.alert.Alert;
import com.dlink.alert.AlertConfig;
import com.dlink.alert.AlertResult;
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

    private static final Map<String, String> config = new HashMap<>();

    @Before
    public void initDingTalkConfig() {

        config.put(DingTalkConstants.KEYWORD, "keyword");
        config.put(DingTalkConstants.WEB_HOOK, "url");
        config.put(DingTalkConstants.MSG_TYPE, DingTalkConstants.MSG_TYPE_MARKDOWN);

        config.put(DingTalkConstants.PROXY_ENABLE, "false");
        config.put(DingTalkConstants.PASSWORD, "password");
        config.put(DingTalkConstants.PORT, "9988");
        config.put(DingTalkConstants.USER, "user1,user2");
    }

    @Test
    public void sendTest(){
        AlertConfig config = AlertConfig.build("test", "DingTalk", DingTalkTest.config);
        Alert alert = Alert.build(config);
        AlertResult result = alert.send("hello word", "UTF-8");
        Assert.assertEquals(false, result.getSuccess());
    }
}
