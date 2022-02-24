package com.dlink.alert.dingtalk;

import com.dlink.alert.AbstractAlert;
import com.dlink.alert.AlertResult;

/**
 * DingTalkAlert
 *
 * @author wenmo
 * @since 2022/2/23 19:28
 **/
public class DingTalkAlert extends AbstractAlert {

    @Override
    public String getType() {
        return DingTalkConstants.TYPE;
    }

    @Override
    public AlertResult send(String title, String content) {
        DingTalkSender sender = new DingTalkSender(getConfig().getParam());
        return sender.send(title, content);
    }
}
