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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * 分库分表的工具类
 *
 * @version 1.0
 * @since 2022/9/2
 */
@Slf4j
public class SplitUtil {
    public static final String ENABLE = "enable";
    public static final String MATCH_NUMBER_REGEX = "match_number_regex";
    public static final String MAX_MATCH_VALUE = "max_match_value";
    public static final String MATCH_WAY = "match_way";

    public static boolean contains(String regex, String sourceData) {
        return Pattern.matches(regex, sourceData);
    }

    public static boolean isSplit(String value, Map<String, String> splitConfig) {
        String matchNumberRegex = splitConfig.get(MATCH_NUMBER_REGEX);
        Pattern pattern = Pattern.compile(matchNumberRegex);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            long splitNum = Long.parseLong(matcher.group(0).replaceFirst("_", ""));
            long maxMatchValue = Long.parseLong(splitConfig.get(MAX_MATCH_VALUE));
            return splitNum <= maxMatchValue;
        }
        return false;
    }

    public static String getReValue(String value, Map<String, String> splitConfig) {
        if (isEnabled(splitConfig)) {
            try {
                String matchNumberRegex = splitConfig.get(MATCH_NUMBER_REGEX);
                String matchWay = splitConfig.get(MATCH_WAY);
                Pattern pattern = Pattern.compile(matchNumberRegex);
                Matcher matcher = pattern.matcher(value);
                // Determine whether it is a prefix or a suffix
                if ("prefix".equalsIgnoreCase(matchWay)) {
                    if (matcher.find()) {
                        String num = matcher.group(0);
                        long splitNum = Long.parseLong(num.replaceFirst("_", ""));
                        long maxMatchValue = Long.parseLong(splitConfig.get(MAX_MATCH_VALUE));
                        if (splitNum <= maxMatchValue) {
                            return value.substring(0, value.lastIndexOf(num));
                        }
                    }
                } else {
                    String num = null;
                    while (matcher.find()) {
                        num = matcher.group(0);
                    }
                    if (num == null) {
                        return value;
                    }
                    long splitNum = Long.parseLong(num.replaceFirst("_", ""));
                    long maxMatchValue = Long.parseLong(splitConfig.get(MAX_MATCH_VALUE));
                    if (splitNum <= maxMatchValue) {
                        return value.substring(0, value.lastIndexOf(num));
                    }
                }

            } catch (Exception exception) {
                log.warn("Unable to determine sub-database sub-table,reason is {}", exception.getMessage());
            }
        }
        return value;
    }

    public static boolean isEnabled(Map<String, String> split) {
        return Boolean.parseBoolean(split.get(ENABLE));
    }
}
