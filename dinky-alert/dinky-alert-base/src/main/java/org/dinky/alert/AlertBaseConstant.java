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
    public static final String ENTER_LINE = "\n";

    public static final String CHARSET = "UTF-8";
    public static final String PROXY_ENABLE = "isEnableProxy";
    public static final String WEB_HOOK = "webhook";
    public static final String KEYWORD = "keyword";
    public static final String SECRET = "secret";
    public static final String MSG_TYPE = "msgtype";
    public static final String AT_MOBILES = "atMobiles";
    public static final String AT_ALL = "isAtAll";
    public static final String PROXY = "proxy";
    public static final String PORT = "port";
    public static final String USER = "user";
    public static final String AT_USERS = "atUsers";
    public static final String PASSWORD = "password";

    public static final String ALERT_TEMPLATE_TITLE = "Dinky Job Alert Test Title";
    public static final String ALERT_TEMPLATE_MSG = "\n- **Job Name:** <font color='gray'>Dinky Test Job</font>\n"
            + "- **Job Status:** <font color='red'>FAILED</font>\n"
            + "- **Alert Time:** 2023-01-01 12:00:00\n"
            + "- **Start Time:** 2023-01-01 12:00:00\n"
            + "- **End Time:** 2023-01-01 12:00:00\n"
            + "- **<font color='red'>The test exception, your job exception will pass here</font>**\n"
            + "[Go to Task Web](https://github.com/DataLinkDC/dinky)";

    public AlertBaseConstant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
