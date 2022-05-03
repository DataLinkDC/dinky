package com.dlink.alert.feishu;

import com.dlink.alert.AbstractAlert;
import com.dlink.alert.AlertResult;

/**
 * FeiShuAlert
 * @author zhumingye
 * @date: 2022/4/2
 **/
public class FeiShuAlert extends AbstractAlert {

    @Override
    public String getType() {
        return FeiShuConstants.TYPE;
    }

    @Override
    public AlertResult send(String title, String content) {
        FeiShuSender sender = new FeiShuSender(getConfig().getParam());
        return sender.send(title,content);
    }
}
