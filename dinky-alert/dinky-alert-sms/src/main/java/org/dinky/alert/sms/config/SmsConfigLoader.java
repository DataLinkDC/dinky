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

package org.dinky.alert.sms.config;

import static java.util.Objects.requireNonNull;

import org.dinky.alert.sms.SmsConstants;
import org.dinky.utils.JsonUtils;

import java.util.List;
import java.util.Map;

import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.aliyun.config.AlibabaFactory;
import org.dromara.sms4j.cloopen.config.CloopenConfig;
import org.dromara.sms4j.cloopen.config.CloopenFactory;
import org.dromara.sms4j.comm.constant.SupplierConstant;
import org.dromara.sms4j.ctyun.config.CtyunConfig;
import org.dromara.sms4j.ctyun.config.CtyunFactory;
import org.dromara.sms4j.emay.config.EmayConfig;
import org.dromara.sms4j.emay.config.EmayFactory;
import org.dromara.sms4j.huawei.config.HuaweiConfig;
import org.dromara.sms4j.huawei.config.HuaweiFactory;
import org.dromara.sms4j.jdcloud.config.JdCloudConfig;
import org.dromara.sms4j.jdcloud.config.JdCloudFactory;
import org.dromara.sms4j.provider.config.BaseConfig;
import org.dromara.sms4j.provider.factory.BaseProviderFactory;
import org.dromara.sms4j.tencent.config.TencentConfig;
import org.dromara.sms4j.tencent.config.TencentFactory;
import org.dromara.sms4j.unisms.config.UniConfig;
import org.dromara.sms4j.unisms.config.UniFactory;
import org.dromara.sms4j.yunpian.config.YunPianFactory;
import org.dromara.sms4j.yunpian.config.YunpianConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * The type Sms config loader.
 */
@Slf4j
public class SmsConfigLoader {

    /**
     * 获取 phone number list
     */
    public static List<String> getPhoneNumberList(Map<String, Object> params) {
        List<String> phoneNumbers = (List<String>) params.get(SmsConstants.PHONE_NUMBERS);
        requireNonNull(phoneNumbers, "phoneNumbers is null");
        return phoneNumbers;
    }

    /**
     * Gets config supplier config.
     *
     * @param params the config params
     * @return the config supplier config {@link BaseConfig }
     */
    public static BaseConfig getConfigSupplierConfig(Map<String, Object> params) {
        Object suppliersId = params.get(SmsConstants.SUPPLIERS);
        requireNonNull(suppliersId, "suppliers is null");

        switch (suppliersId.toString()) {
            case SupplierConstant.ALIBABA:
                return JsonUtils.toBean(params, AlibabaConfig.class);
            case SupplierConstant.TENCENT:
                return JsonUtils.toBean(params, TencentConfig.class);
            case SupplierConstant.HUAWEI:
                return JsonUtils.toBean(params, HuaweiConfig.class);
            case SupplierConstant.YUNPIAN:
                return JsonUtils.toBean(params, YunpianConfig.class);
            case SupplierConstant.UNISMS:
                return JsonUtils.toBean(params, UniConfig.class);
            case SupplierConstant.JDCLOUD:
                return JsonUtils.toBean(params, JdCloudConfig.class);
            case SupplierConstant.CLOOPEN:
                return JsonUtils.toBean(params, CloopenConfig.class);
            case SupplierConstant.EMAY:
                return JsonUtils.toBean(params, EmayConfig.class);
            case SupplierConstant.CTYUN:
                return JsonUtils.toBean(params, CtyunConfig.class);
            default:
                throw new IllegalArgumentException(String.format("Unsupported supplier type: [%s]", suppliersId));
        }
    }

    /**
     * 获取 BaseProviderFactory 实例
     */
    public static BaseProviderFactory getBaseProviderFactory(String supplier) {
        switch (supplier) {
            case SupplierConstant.ALIBABA:
                return AlibabaFactory.instance();
            case SupplierConstant.HUAWEI:
                return HuaweiFactory.instance();
            case SupplierConstant.YUNPIAN:
                return YunPianFactory.instance();
            case SupplierConstant.TENCENT:
                return TencentFactory.instance();
            case SupplierConstant.JDCLOUD:
                return JdCloudFactory.instance();
            case SupplierConstant.CLOOPEN:
                return CloopenFactory.instance();
            case SupplierConstant.EMAY:
                return EmayFactory.instance();
            case SupplierConstant.CTYUN:
                return CtyunFactory.instance();
            case SupplierConstant.UNISMS:
                return UniFactory.instance();
            default:
                throw new IllegalArgumentException(String.format("Unsupported supplier type: [%s]", supplier));
        }
    }
}
