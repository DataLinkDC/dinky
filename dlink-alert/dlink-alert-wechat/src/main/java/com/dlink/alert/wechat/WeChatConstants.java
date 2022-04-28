package com.dlink.alert.wechat;

/**
 * WeChatConstants
 *
 * @author wenmo
 * @since 2022/2/23 21:10
 **/
public class WeChatConstants {

    static final String TYPE = "WeChat";

    static final String MARKDOWN_QUOTE = ">";

    static final String MARKDOWN_ENTER = "\n";

    static final String CHARSET = "UTF-8";

    static final String PUSH_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token={token}";

    static final String APP_CHAT_PUSH_URL = "https://qyapi.weixin.qq.com/cgi-bin/appchat/send?access_token={token}";

    static final String TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpId}&corpsecret={secret}";

    static final String WEBHOOK = "webhook";

    static final String WEBHOOK_TEMPLATE = "{\"msgtype\":\"{showType}\",\"{showType}\":{\"content\":\"{msg} \"}}";

    static final String KEYWORD = "keyword";

    static final String AT_ALL = "isAtAll";

    static final String CORP_ID = "corpId";

    static final String SECRET = "secret";

    static final String TEAM_SEND_MSG = "teamSendMsg";

    static final String USER_SEND_MSG = "{\"touser\":\"{toUser}\",\"agentid\":{agentId},\"msgtype\":\"{showType}\",\"{showType}\":{\"content\":\"{msg}\"}}";

    static final String AGENT_ID = "agentId";

    static final String USERS = "users";

    static final String SEND_TYPE = "sendType";

    static final String SHOW_TYPE = "showType";

}
