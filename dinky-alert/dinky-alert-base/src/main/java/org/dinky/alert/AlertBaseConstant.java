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

package org.dinky.alert;

/** AlertBaseConstant */
public class AlertBaseConstant {

    /** base constant */
    public static final String MARKDOWN_QUOTE_MIDDLE_LINE = "- ";

    public static final String TAB = "\t";
    public static final String ENTER_LINE = "\n";
    public static final String MARKDOWN_QUOTE_RIGHT_TAG = "> ";
    public static final String MARKDOWN_QUOTE_RIGHT_TAG_WITH_SPACE = ">";
    public static final String MARKDOWN_ENTER_BACK_SLASH = "/n";
    public static final String CHARSET = "UTF-8";
    public static final String PROXY_ENABLE = "isEnableProxy";
    public static final String WEB_HOOK = "webhook";
    public static final String KEYWORD = "keyword";
    public static final String SECRET = "secret";
    public static final String MSG_TYPE = "msgtype";
    public static final String AT_MOBILES = "atMobiles";
    public static final String AT_USERIDS = "atUserIds";
    public static final String AT_ALL = "isAtAll";
    public static final String PROXY = "proxy";
    public static final String PORT = "port";
    public static final String USER = "user";
    public static final String AT_USERS = "users";
    public static final String PASSWORD = "password";

    public AlertBaseConstant() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
