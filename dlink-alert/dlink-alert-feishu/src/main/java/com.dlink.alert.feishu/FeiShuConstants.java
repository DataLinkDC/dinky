package com.dlink.alert.feishu;

/**
 * @Author: zhumingye
 * @date: 2022/4/2
 * @Description: 参数常量
 */
public final class FeiShuConstants {
    static final String TYPE = "FeiShu";
    static final String MARKDOWN_QUOTE = "> ";
    static final String MARKDOWN_ENTER = "\n";
    static final String WEB_HOOK = "webhook";
    static final String KEY_WORD = "keyword";
    static final String SECRET = "secret";
    static final String FEI_SHU_PROXY_ENABLE = "isEnableProxy";
    static final String FEI_SHU_PROXY = "proxy";
    static final String FEI_SHU_PORT = "port";
    static final String FEI_SHU_USER = "user";
    static final String FEI_SHU_PASSWORD = "password";
    static final String MSG_TYPE = "msgtype";
    static final String AT_ALL = "isAtAll";
    static final String AT_USERS = "users";
    static final String FEI_SHU_TEXT_TEMPLATE = "{\"msg_type\":\"{msg_type}\",\"content\":{\"{msg_type}\":\"{msg} {users} \" }}";
    static final String FEI_SHU_POST_TEMPLATE ="{\"msg_type\":\"{msg_type}\",\"content\":{\"{msg_type}\":{\"zh_cn\":{\"title\":\"{keyword}\",\"content\":[[{\"tag\":\"text\",\"text\":\"{msg}\"},{users}]]}}}}";

    private FeiShuConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
