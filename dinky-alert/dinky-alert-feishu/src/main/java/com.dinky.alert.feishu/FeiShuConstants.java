/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.alert.feishu;

/**
 * @Author: zhumingye
 * @date: 2022/4/2
 * @Description: 参数常量
 */
public final class FeiShuConstants {
    static final String TYPE = "FeiShu";
    static final String MARKDOWN_QUOTE = "> ";
    static final String MARKDOWN_ENTER = "/n";
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
    static final String FEI_SHU_POST_TEMPLATE = "{\"msg_type\":\"{msg_type}\",\"content\":{\"{msg_type}\":{\"zh_cn\":{\"title\":\"{keyword}\","
            + "\"content\":[[{\"tag\":\"text\",\"un_escape\": true,\"text\":\"{msg}\"},{users}]]}}}}";

    private FeiShuConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
