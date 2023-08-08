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

package org.dinky.crypto;

import org.apache.parquet.Strings;

import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class CryptoComponent {

    private final boolean enabled;
    private final AES256TextEncryptor textEncryptor;

    public CryptoComponent(CryptoProperties cryptoProperties) {
        this.enabled = cryptoProperties.getEnabled();
        this.textEncryptor = new AES256TextEncryptor();
        if (!Strings.isNullOrEmpty(cryptoProperties.getEncryptionPassword())) {
            this.textEncryptor.setPassword(cryptoProperties.getEncryptionPassword());
        }
    }

    public String encryptText(String result) {
        if (!enabled) {
            return result;
        }

        return textEncryptor.encrypt(result);
    }

    public String decryptText(String result) {
        if (!enabled) {
            return result;
        }

        if (result == null) {
            return null;
        }
        return textEncryptor.decrypt(result);
    }
}
