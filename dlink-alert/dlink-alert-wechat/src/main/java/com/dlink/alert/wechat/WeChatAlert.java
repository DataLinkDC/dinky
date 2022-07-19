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
