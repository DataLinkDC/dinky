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

package org.dinky.alert.sms;

import static java.util.Objects.requireNonNull;

import org.dinky.alert.AlertResult;
import org.dinky.alert.sms.config.SmsConfigLoader;
import org.dinky.alert.sms.enums.ManuFacturers;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.universal.SupplierConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.json.JSONUtil;

/** SmsSender todo: https://wind.kim/doc/start/springboot.html */
public class SmsSender {

    private static final Logger logger = LoggerFactory.getLogger(SmsSender.class);
    private static SupplierConfig configLoader = null;
    private static SmsBlend smsSendFactory = null;

    /** manufacturers of sms */
    private Integer manufacturers;

    SmsSender(String config) {
        int manufacturersId =
                Integer.parseInt(JSONUtil.parseObj(config).getStr(SmsConstants.MANU_FACTURERS));
        this.manufacturers = manufacturersId;
        requireNonNull(manufacturers, "manufacturers is null");
        configLoader = SmsConfigLoader.getConfigLoader(config, manufacturers);
        smsSendFactory = SmsConfigLoader.getSmsFactory(manufacturersId);
        logger.info("you choose {} manufacturers", ManuFacturers.getManuFacturers(manufacturersId));
    }

    public AlertResult send(String title, String content) {
        AlertResult alertResult = new AlertResult();
        logger.info("send sms, title: {}, content: {}", title, content);
        // todo: 1. support multi sms manufacturers send
        // 使用自定义模板群发短信 || use custom template mass texting
        smsSendFactory.massTexting(Arrays.asList("17722226666"), "110", new LinkedHashMap<>());
        // todo: 2. validate sms send result
        return alertResult;
    }
}
