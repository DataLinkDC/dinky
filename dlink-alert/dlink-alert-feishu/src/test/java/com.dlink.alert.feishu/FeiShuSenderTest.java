package com.dlink.alert.feishu;

import com.dlink.alert.AlertMsg;
import com.dlink.alert.AlertResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhumingye
 * @date: 2022/4/2
 * @Description: 飞书消息发送 单元测试
 */
public class FeiShuSenderTest {


    private static Map<String, String> feiShuConfig = new HashMap<>();

    String alertMsgContentTemplate = "[\n"
            + "  {\n"
            + "    \"owner\": \"dlink\",\n"
            + "    \"processEndTime\": \"2021-01-29 19:01:11\",\n"
            + "    \"processHost\": \"10.81.129.4:5678\",\n"
            + "    \"processId\": 2926,\n"
            + "    \"processName\": \"3-20210129190038108\",\n"
            + "    \"processStartTime\": \"2021-01-29 19:00:38\",\n"
            + "    \"processState\": \"SUCCESS\",\n"
            + "    \"processType\": \"START_PROCESS\",\n"
            + "    \"projectId\": 2,\n"
            + "    \"projectName\": \"testdelproject\",\n"
            + "    \"recovery\": \"NO\",\n"
            + "    \"retryTimes\": 0,\n"
            + "    \"runTimes\": 1,\n"
            + "    \"taskId\": 0\n"
            + "  }\n"
            + "]";
    @Before
    public void initFeiShuConfig() {
        feiShuConfig.put(FeiShuConstants.WEB_HOOK, "https://open.feishu.cn/open-apis/bot/v2/hook/aea3cd7f13154854541dsadsadas08f2a9");
        feiShuConfig.put(FeiShuConstants.KEY_WORD, "Dinky 飞书WebHook 告警测试");
        feiShuConfig.put(FeiShuConstants.MSG_TYPE,"text");
        feiShuConfig.put(FeiShuConstants.AT_ALL, "false");
        feiShuConfig.put(FeiShuConstants.AT_USERS, "user1,user2,user3");
    }

    @Test
    public void testTextTypeSend() {
        AlertMsg alertMsg = new AlertMsg();
        alertMsg.setName("Dinky 飞书WebHook 告警测试");
        alertMsg.setContent(alertMsgContentTemplate);
        FeiShuSender feiShuSender = new FeiShuSender(feiShuConfig);
        AlertResult alertResult = feiShuSender.send(alertMsg.getName(),alertMsg.getContent());
        Assert.assertEquals(true, alertResult.getSuccess());
    }

    @Test
    public void testPostTypeSend() {
        feiShuConfig.put(FeiShuConstants.MSG_TYPE,"post");
        AlertMsg alertMsg = new AlertMsg();
        alertMsg.setName("Dinky 飞书WebHook 告警测试");
        alertMsg.setContent(alertMsgContentTemplate);
        FeiShuSender feiShuSender = new FeiShuSender(feiShuConfig);
        AlertResult alertResult = feiShuSender.send(alertMsg.getName(),alertMsg.getContent());
        Assert.assertEquals(true, alertResult.getSuccess());
    }

}
