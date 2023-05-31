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

import org.dinky.alert.sms.SmsConstants;
import org.dinky.alert.sms.enums.ManuFacturers;

import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.universal.SupplierConfig;
import org.dromara.sms4j.cloopen.config.CloopenConfig;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.ctyun.config.CtyunConfig;
import org.dromara.sms4j.emay.config.EmayConfig;
import org.dromara.sms4j.huawei.config.HuaweiConfig;
import org.dromara.sms4j.jdcloud.config.JdCloudConfig;
import org.dromara.sms4j.provider.enumerate.SupplierType;
import org.dromara.sms4j.tencent.config.TencentConfig;
import org.dromara.sms4j.unisms.config.UniConfig;
import org.dromara.sms4j.yunpian.config.YunpianConfig;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/** The type Sms config loader. */
@Slf4j
public class SmsConfigLoader {

    public static SupplierConfig getConfigLoader(String config, Integer manufacturersType) {
        JSONObject fullConfigParams = JSONUtil.parseObj(config);
        switch (manufacturersType) {
            case 1:
                AlibabaConfig alibabaConfig =
                        AlibabaConfig.builder()
                                .accessKeyId(fullConfigParams.getStr(SmsConstants.ACCESS_KEY_ID))
                                .accessKeySecret(
                                        fullConfigParams.getStr(SmsConstants.ACCESS_KEY_SECRET))
                                .signature(fullConfigParams.getStr(SmsConstants.SIGNATURE))
                                .templateId(fullConfigParams.getStr(SmsConstants.TEMPLATE_ID))
                                .templateName(fullConfigParams.getStr(SmsConstants.TEMPLATE_NAME))
                                .requestUrl(fullConfigParams.getStr(SmsConstants.REQUEST_URL))
                                .action(fullConfigParams.getStr(SmsConstants.ACTION))
                                .version(fullConfigParams.getStr(SmsConstants.VERSION))
                                .regionId(fullConfigParams.getStr(SmsConstants.REGION_ID))
                                .build();
                return alibabaConfig;
            case 2:
                HuaweiConfig huaweiConfig =
                        HuaweiConfig.builder()
                                .appKey(fullConfigParams.getStr(SmsConstants.APP_KEY))
                                .appSecret(fullConfigParams.getStr(SmsConstants.APP_SECRET))
                                .signature(fullConfigParams.getStr(SmsConstants.SIGNATURE))
                                .sender(fullConfigParams.getStr(SmsConstants.SENDER))
                                .templateId(fullConfigParams.getStr(SmsConstants.TEMPLATE_ID))
                                .statusCallBack(
                                        fullConfigParams.getStr(SmsConstants.STATUS_CALLBACK))
                                .url(fullConfigParams.getStr(SmsConstants.URL))
                                .build();
                return huaweiConfig;
            case 3:
                YunpianConfig yunpianConfig =
                        YunpianConfig.builder()
                                .accessKeyId(fullConfigParams.getStr(SmsConstants.ACCESS_KEY_ID))
                                .accessKeySecret(
                                        fullConfigParams.getStr(SmsConstants.ACCESS_KEY_SECRET))
                                .signature(fullConfigParams.getStr(SmsConstants.TEMPLATE_ID))
                                .templateId(fullConfigParams.getStr(SmsConstants.TEMPLATE_NAME))
                                .callbackUrl(fullConfigParams.getStr(SmsConstants.STATUS_CALLBACK))
                                .templateName(fullConfigParams.getStr(SmsConstants.TEMPLATE_NAME))
                                .build();
                return yunpianConfig;
            case 4:
                TencentConfig tencentConfig =
                        TencentConfig.builder()
                                .accessKeyId(fullConfigParams.getStr(SmsConstants.ACCESS_KEY_ID))
                                .accessKeySecret(
                                        fullConfigParams.getStr(SmsConstants.ACCESS_KEY_SECRET))
                                .signature(fullConfigParams.getStr(SmsConstants.SIGNATURE))
                                .templateId(fullConfigParams.getStr(SmsConstants.TEMPLATE_ID))
                                .sdkAppId(fullConfigParams.getStr(SmsConstants.SDK_APP_ID))
                                .territory(fullConfigParams.getStr(SmsConstants.TERRITORY))
                                .connTimeout(fullConfigParams.getInt(SmsConstants.CONN_TIMEOUT))
                                .requestUrl(fullConfigParams.getStr(SmsConstants.REQUEST_URL))
                                .action(fullConfigParams.getStr(SmsConstants.ACTION))
                                .version(fullConfigParams.getStr(SmsConstants.VERSION))
                                .build();
                return tencentConfig;
            case 5:
                UniConfig uniConfig =
                        UniConfig.builder()
                                .accessKeyId(fullConfigParams.getStr(SmsConstants.ACCESS_KEY_ID))
                                .accessKeySecret(
                                        fullConfigParams.getStr(SmsConstants.ACCESS_KEY_SECRET))
                                .isSimple(fullConfigParams.getBool(SmsConstants.IS_SIMPLE))
                                .signature(fullConfigParams.getStr(SmsConstants.SIGNATURE))
                                .templateId(fullConfigParams.getStr(SmsConstants.TEMPLATE_ID))
                                .templateName(fullConfigParams.getStr(SmsConstants.TEMPLATE_NAME))
                                .build();
                return uniConfig;
            case 6:
                JdCloudConfig jdCloudConfig =
                        JdCloudConfig.builder()
                                .accessKeyId(fullConfigParams.getStr(SmsConstants.ACCESS_KEY_ID))
                                .accessKeySecret(
                                        fullConfigParams.getStr(SmsConstants.ACCESS_KEY_SECRET))
                                .signature(fullConfigParams.getStr(SmsConstants.SIGNATURE))
                                .templateId(fullConfigParams.getStr(SmsConstants.TEMPLATE_ID))
                                .region(fullConfigParams.getStr(SmsConstants.REGION))
                                .build();
                return jdCloudConfig;
            case 7:
                CloopenConfig cloopenConfig =
                        CloopenConfig.builder()
                                .accessKeyId(fullConfigParams.getStr(SmsConstants.ACCESS_KEY_ID))
                                .accessKeySecret(
                                        fullConfigParams.getStr(SmsConstants.ACCESS_KEY_SECRET))
                                .templateId(fullConfigParams.getStr(SmsConstants.TEMPLATE_ID))
                                .appId(fullConfigParams.getStr(SmsConstants.APP_ID))
                                .baseUrl(fullConfigParams.getStr(SmsConstants.BASE_URL))
                                .build();
                return cloopenConfig;
            case 8:
                EmayConfig emayConfig =
                        EmayConfig.builder()
                                .appId(fullConfigParams.getStr(SmsConstants.APP_ID))
                                .secretKey(fullConfigParams.getStr(SmsConstants.SECRET_KEY))
                                .requestUrl(fullConfigParams.getStr(SmsConstants.REQUEST_URL))
                                .build();
                return emayConfig;
            case 9:
                CtyunConfig ctyunConfig =
                        CtyunConfig.builder()
                                .accessKeyId(fullConfigParams.getStr(SmsConstants.ACCESS_KEY_ID))
                                .accessKeySecret(
                                        fullConfigParams.getStr(SmsConstants.ACCESS_KEY_SECRET))
                                .signature(fullConfigParams.getStr(SmsConstants.SIGNATURE))
                                .templateId(fullConfigParams.getStr(SmsConstants.TEMPLATE_ID))
                                .templateName(fullConfigParams.getStr(SmsConstants.TEMPLATE_NAME))
                                .requestUrl(fullConfigParams.getStr(SmsConstants.REQUEST_URL))
                                .action(fullConfigParams.getStr(SmsConstants.ACTION))
                                .build();
                return ctyunConfig;
            default:
                throw new IllegalArgumentException(
                        "Unsupported manufacturers type: "
                                + ManuFacturers.getManuFacturers(manufacturersType));
        }
    }

    public static SmsBlend getSmsFactory(Integer manufacturersType) {
        switch (manufacturersType) {
            case 1:
                return SmsFactory.createSmsBlend(SupplierType.ALIBABA);
            case 2:
                return SmsFactory.createSmsBlend(SupplierType.HUAWEI);
            case 3:
                return SmsFactory.createSmsBlend(SupplierType.YUNPIAN);
            case 4:
                return SmsFactory.createSmsBlend(SupplierType.TENCENT);
            case 5:
                return SmsFactory.createSmsBlend(SupplierType.UNI_SMS);
            case 6:
                return SmsFactory.createSmsBlend(SupplierType.JD_CLOUD);
            case 7:
                return SmsFactory.createSmsBlend(SupplierType.CLOOPEN);
            case 8:
                return SmsFactory.createSmsBlend(SupplierType.EMAY);
            case 9:
                return SmsFactory.createSmsBlend(SupplierType.CTYUN);
            default:
                throw new IllegalArgumentException(
                        "Unsupported manufacturers type: "
                                + ManuFacturers.getManuFacturers(manufacturersType));
        }
    }
}
