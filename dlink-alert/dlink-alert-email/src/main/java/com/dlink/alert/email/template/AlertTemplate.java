package com.dlink.alert.email.template;


import com.dlink.alert.ShowType;

/**
 * @Author: zhumingye
 * @date: 2022/4/3
 * @Description: 邮件告警模板接口
 */
public interface AlertTemplate {

    String getMessageFromTemplate(String title,String content, ShowType showType, boolean showAll);

    /**
     * default showAll is true
     * @param content alert message content
     * @param showType show type
     * @return a message from a specified alert template
     */
    default String getMessageFromTemplate(String title,String content, ShowType showType) {
        return getMessageFromTemplate(title,content, showType, true);
    }
}
