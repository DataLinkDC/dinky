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

import cn.hutool.core.util.StrUtil;

public class StringUtil {
    /**
     * 驼峰 -> 蛇形命名
     *
     * @param content      内容
     * @param isFirstUpper 是否首字母大写
     * @param isFirstLower 是否首字母小写
     * @return 转换后的内容
     */
    public static String toSnakeCase(String content, boolean isFirstUpper, boolean isFirstLower) {
        if (StrUtil.isEmpty(content)) {
            return content;
        }
        StringBuilder sb = new StringBuilder(content.length());
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (isFirstUpper && i == 0) {
                sb.append(Character.toUpperCase(c));
            } else if (isFirstLower && i == 0) {
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 蛇形命名转换 -> 小驼峰
     *
     * @param content      内容
     * @param isFirstUpper 是否首字母大写
     * @return 转换后的内容
     */
    public static String toSnakeCase(String content, boolean isFirstUpper) {
        return toSnakeCase(content, isFirstUpper, false);
    }

    /**
     * 蛇形命名转换 -> 小驼峰
     *
     * @param isFirstLower 是否首字母小写
     * @param content      内容
     * @return 转换后的内容
     */
    public static String toSnakeCase(boolean isFirstLower, String content) {
        return toSnakeCase(content, false, isFirstLower);
    }
}
