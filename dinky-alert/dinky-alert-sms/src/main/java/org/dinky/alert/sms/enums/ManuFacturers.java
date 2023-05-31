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

package org.dinky.alert.sms.enums;

public enum ManuFacturers {
    /** 阿里云 */
    ALIBABA(1, "阿里云短信"),
    /** 华为云 */
    HUAWEI(2, "华为云短信"),
    /** 云片 */
    YUNPIAN(3, "云片短信"),
    /** 腾讯云 */
    TENCENT(4, "腾讯云短信"),
    /** 合一短信 */
    UNI_SMS(5, "合一短信"),
    /** 京东云短信 */
    JD_CLOUD(6, "京东云短信"),
    /** 容联云短信 */
    CLOOPEN(7, "容联云短信"),
    /** 亿美软通短信 */
    EMAY(8, "亿美软通短信"),
    /** 天翼云短信 */
    CTYUN(9, "天翼云短信"),
    ;

    private final Integer code;
    private final String type;

    ManuFacturers(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getType() {
        return this.type;
    }

    public static ManuFacturers getManuFacturers(Integer code) {
        for (ManuFacturers manuFacturers : ManuFacturers.values()) {
            if (manuFacturers.getCode().equals(code)) {
                return manuFacturers;
            }
        }
        return null;
    }
}
