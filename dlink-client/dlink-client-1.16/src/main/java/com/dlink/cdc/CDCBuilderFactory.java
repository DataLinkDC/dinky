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

package com.dlink.cdc;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.mysql.MysqlCDCBuilder;
import com.dlink.exception.FlinkClientException;
import com.dlink.model.FlinkCDCConfig;

/**
 * CDCBuilderFactory
 *
 * @author wenmo
 * @since 2022/11/04
 **/
public class CDCBuilderFactory {

    private static CDCBuilder[] cdcBuilders = {
        new MysqlCDCBuilder()
    };

    public static CDCBuilder buildCDCBuilder(FlinkCDCConfig config) {
        if (Asserts.isNull(config) || Asserts.isNullString(config.getType())) {
            throw new FlinkClientException("请指定 CDC Source 类型。");
        }
        for (int i = 0; i < cdcBuilders.length; i++) {
            if (config.getType().equals(cdcBuilders[i].getHandle())) {
                return cdcBuilders[i].create(config);
            }
        }
        throw new FlinkClientException("未匹配到对应 CDC Source 类型的【" + config.getType() + "】。");
    }
}
