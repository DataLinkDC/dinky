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

package org.dinky.alert.wechat;

import org.dinky.alert.AlertBaseConstant;

/** WeChatConstants */
public class WeChatConstants extends AlertBaseConstant {
    public static final String TYPE = "WeChat";
    /** WeChat alert baseconstant */
    public static final String WECHAT_PUSH_URL = "%s/message/send?access_token=%s";

    public static final String WECHAT_TOKEN_URL = "%s/gettoken?corpid=%s&corpsecret=%s";
    public static final String CORP_ID = "corpId";
    public static final String TEAM_SEND_MSG = "teamSendMsg";
    public static final String AGENT_ID = "agentId";
    public static final String SEND_TYPE = "sendType";
    public static final String ACCESS_TOKEN = "access_token";

    public static final String ALERT_TEMPLATE_TITLE = "title";
    public static final String ALERT_TEMPLATE_CONTENT = "content";
    public static final String ALERT_TEMPLATE_AGENT_ID = "agentId";
    public static final String ALERT_TEMPLATE_AT_USERS = "atUsers";
}
