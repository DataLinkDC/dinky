package com.dlink.alert.email;

import com.dlink.alert.AbstractAlert;
import com.dlink.alert.AlertResult;

/**
 * EmailAlert
 * @author zhumingye
 * @date: 2022/4/2
 **/
public class EmailAlert extends AbstractAlert {

    @Override
    public String getType() {
        return EmailConstants.TYPE;
    }

    @Override
    public AlertResult send(String title, String content) {
        MailSender mailSender=new MailSender(getConfig().getParam());
        return mailSender.send(title,content);
    }
}
