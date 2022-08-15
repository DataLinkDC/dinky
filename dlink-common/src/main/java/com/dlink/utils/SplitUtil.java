package com.dlink.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ZackYoung
 * @version 1.0
 * @date 2022/8/11
 */
public class SplitUtil {
    // add 切分类，这边有2个，因为互相调用不了的原因


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
                Pattern pattern = Pattern.compile(matchNumberRegex);
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    String num = matcher.group(0);
                    int splitNum = Integer.parseInt(num.replaceFirst("_", ""));
                    int maxMatchValue = Integer.parseInt(splitConfig.get("max_match_value"));
                    if (splitNum <= maxMatchValue) {
                        return value.substring(0, value.lastIndexOf(num));
                    }
                }

            } catch (Exception ignored) {

            }
        }
        return value;
    }
    public static boolean isEnabled(Map<String, String> split){
        return Boolean.parseBoolean(split.get("enable"));
    }
}
