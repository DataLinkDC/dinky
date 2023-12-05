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

/** SmsConstants */
public class SmsConstants {
    public static final String TYPE = "Sms";
    public static final String SUPPLIERS = "suppliers";
    public static final String ALERT_TEMPLATE_TITLE = "title";
    public static final String ALERT_TEMPLATE_CONTENT = "content";

    //  ==================== common config ====================
    public static final String ACCESS_KEY_ID = "accessKeyId";
    public static final String SDK_APP_ID = "sdkAppId";
    public static final String ACCESS_KEY_SECRET = "accessKeySecret";
    public static final String SIGNATURE = "signature";
    public static final String TEMPLATE_ID = "templateId";
    public static final String WEIGHT = "weight";
    public static final String CONFIG_ID = "configId";
    public static final String RETRY_INTERVAL = "retryInterval";
    public static final String MAX_RETRY = "maxRetries";

    // =================== aliyun config ====================
    public static final String TEMPLATE_NAME = "templateName"; // EXTEND YUM PIAN
    public static final String REQUEST_URL = "requestUrl"; // extend tencent
    public static final String REGION_ID = "regionId";
    public static final String ACTION = "action"; // extend tencent
    public static final String VERSION = "version"; // extend tencent

    // =================== huawei config ====================
    public static final String SENDER = "sender";
    public static final String STATUS_CALLBACK = "statusCallBack";
    public static final String URL = "url";

    // =================== yun pian config ====================
    public static final String CALLBACK_URL = "callbackUrl";

    // ===================  tencent config ====================
    public static final String TERRITORY = "territory";
    public static final String CONN_TIMEOUT = "connTimeout";
    // service
    public static final String SERVICE = "service";

    // ===================  uni config ====================
    public static final String IS_SIMPLE = "isSimple";

    // ===================  Cloopen config ====================
    public static final String BASE_URL = "baseUrl";

    // ===================  other config ====================

    public static final String REGION = "region";
    public static final String PHONE_NUMBERS = "phoneNumbers";

    // =============== variables ==================
    public static final String ALIYUNM_SMS_TEMPLATE_VARIABLES = "content";
    public static final String TENCENT_SMS_TEMPLATE_VARIABLES_1 = "1";
}
