package com.dlink.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * 分库分表的工具类
 *
 * @author ZackYoung
 * @version 1.0
 * @since 2022/9/2
 */
@Slf4j
public class SplitUtil {

    public static boolean contains(String regex, String sourceData) {
        return Pattern.matches(regex, sourceData);
    }

    public static boolean isSplit(String value, Map<String, String> splitConfig) {
        String matchNumberRegex = splitConfig.get("match_number_regex");
        Pattern pattern = Pattern.compile(matchNumberRegex);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            int splitNum = Integer.parseInt(matcher.group(0).replaceFirst("_", ""));
            int maxMatchValue = Integer.parseInt(splitConfig.get("max_match_value"));
            return splitNum <= maxMatchValue;
        }
        return false;
    }

    public static String getReValue(String value, Map<String, String> splitConfig) {
        if (isEnabled(splitConfig)) {
            try {
                String matchNumberRegex = splitConfig.get("match_number_regex");
                String matchWay = splitConfig.get("match_way");
                Pattern pattern = Pattern.compile(matchNumberRegex);
                Matcher matcher = pattern.matcher(value);
                // Determine whether it is a prefix or a suffix
                if ("prefix".equalsIgnoreCase(matchWay)) {
                    if (matcher.find()) {
                        String num = matcher.group(0);
                        int splitNum = Integer.parseInt(num.replaceFirst("_", ""));
                        int maxMatchValue = Integer.parseInt(splitConfig.get("max_match_value"));
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
                    int splitNum = Integer.parseInt(num.replaceFirst("_", ""));
                    int maxMatchValue = Integer.parseInt(splitConfig.get("max_match_value"));
                    if (splitNum <= maxMatchValue) {
                        return value.substring(0, value.lastIndexOf(num));
                    }
                }

            } catch (Exception ignored) {
                log.warn("Unable to determine sub-database sub-table");
            }
        }
        return value;
    }

    public static boolean isEnabled(Map<String, String> split) {
        return Boolean.parseBoolean(split.get("enable"));
    }

}
