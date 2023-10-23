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

package org.dinky.utils;

public class FragmentVariableUtils {

    /** 隐藏占位符 */
    public static final String HIDDEN_CONTENT = "******";

    private static final String[] SENSITIVE_KEYS =
            new String[] {"password", "secret", "fs.azure.account.key", "apikey"};

    /**
     * 判断变量是否敏感
     *
     * @param key 变量名
     * @return 敏感返回<code>true</code>
     */
    public static boolean isSensitive(String key) {
        if (key == null) {
            return false;
        }
        final String keyInLower = key.toLowerCase();
        for (String hideKey : SENSITIVE_KEYS) {
            if (keyInLower.length() >= hideKey.length() && keyInLower.contains(hideKey)) {
                return true;
            }
        }
        return false;
    }
}
