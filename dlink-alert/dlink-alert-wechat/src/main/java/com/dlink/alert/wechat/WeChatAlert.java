package com.dlink.alert.wechat;

import com.dlink.alert.AbstractAlert;
import com.dlink.alert.AlertResult;

/**
 * WeChatAlert
 *
 * @author wenmo
 * @since 2022/2/23 21:09
 **/
public class WeChatAlert extends AbstractAlert {
    @Override
    public String getType() {
        return WeChatConstants.TYPE;
    }

    @Override
    public AlertResult send(String title, String content) {
        WeChatSender sender = new WeChatSender(getConfig().getParam());
        return sender.send(title, content);
    }
}
