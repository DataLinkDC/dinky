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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * MapParseUtils
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class MapParseUtils {

    /**
     * 数组是否嵌套
     *
     * @param inStr
     * @return
     */
    public static Boolean getStrIsNest(String inStr) {
        if (inStr == null || inStr.isEmpty()) {
            return false;
        }
        Deque<Integer> stack = new LinkedList<>();
        for (int i = 0; i < inStr.length(); i++) {
            if (inStr.charAt(i) == '[') {
                stack.push(i);
            }
            if (inStr.charAt(i) == ']') {
                stack.pop();
                if (stack.size() != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取嵌套最外层的下标对  table=[[default_catalog, default_database, score, project=[sid, cls, score]]], fields=[sid, cls, score]
     * ^(下标x)                                                              ^(下标y)   ^(下标z)         ^(下标n)
     * List<Integer>   [x, y, z, n]
     *
     * @param inStr
     * @return
     */
    public static List<Integer> getNestList(String inStr) {
        Stack nestIndexList = new Stack();
        if (inStr == null || inStr.isEmpty()) {
            return nestIndexList;
        }
        Deque<Integer> stack = new LinkedList<>();
        for (int i = 0; i < inStr.length(); i++) {
            if (inStr.charAt(i) == '[') {
                if (stack.isEmpty()) {
                    nestIndexList.add(i);
                }
                stack.push(i);
            }
            if (inStr.charAt(i) == ']') {
                stack.pop();
                if (stack.size() == 0) {
                    nestIndexList.add(i);
                }
            }
        }
        return nestIndexList;
    }

    /**
     * 获取最外层括号下标     table=[((f.SERIAL_NO || f.PRESC_NO) || f.ITEM_NO) AS EXPR$0, ((f.DATE || f.TIME) || f.ITEM_NO) AS EXPR$2]
     * ^(下标x)                                 ^(下标y)      ^(下标z)                         ^(下标n)
     * List<Integer>   [x, y, z, n]
     *
     * @param inStr
     * @return
     */
    public static List<Integer> getBracketsList(String inStr) {
        Stack nestIndexList = new Stack();
        if (inStr == null || inStr.isEmpty()) {
            return nestIndexList;
        }
        Deque<Integer> stack = new LinkedList<>();
        for (int i = 0; i < inStr.length(); i++) {
            if (inStr.charAt(i) == '(') {
                if (stack.isEmpty()) {
                    nestIndexList.add(i);
                }
                stack.push(i);
            }
            if (inStr.charAt(i) == ')') {
                stack.pop();
                if (stack.size() == 0) {
                    nestIndexList.add(i);
                }
            }
        }
        return nestIndexList;
    }

    public static List<String> getSelectList(String inStr) {
        List<String> selects = new ArrayList<>();
        if (inStr == null || inStr.isEmpty()) {
            return selects;
        }
        int startIndex = -1;
        // lineage only need select or field
        if (inStr.contains("select=[")) {
            startIndex = inStr.indexOf("select=[") + 8;
        } else if (inStr.contains("field=[")) {
            startIndex = inStr.indexOf("field=[") + 7;
        }
        if (startIndex < 0) {
            return selects;
        }
        Deque<Integer> stack = new LinkedList<>();
        for (int i = startIndex; i < inStr.length(); i++) {
            if (inStr.charAt(i) == ']' && stack.size() == 0) {
                selects.add(inStr.substring(startIndex, i));
                return selects;
            }
            if (inStr.charAt(i) == ',' && stack.size() == 0) {
                selects.add(inStr.substring(startIndex, i));
                startIndex = i + 1;
            }
            if (inStr.charAt(i) == '(') {
                stack.push(i);
            }
            if (inStr.charAt(i) == ')') {
                stack.pop();
            }
        }
        if (startIndex < inStr.length()) {
            selects.add(inStr.substring(startIndex, inStr.length() - 1));
        }
        return selects;
    }

    private static Map<String, List<String>> getKeyAndValues(String inStr) {
        Map<String, List<String>> map = new HashMap<>();
        if (inStr == null || inStr.isEmpty()) {
            return map;
        }
        Deque<Integer> stack = new LinkedList<>();
        int startIndex = 0;
        String key = null;
        for (int i = 0; i < inStr.length(); i++) {
            char currentChar = inStr.charAt(i);
            if (stack.size() == 0 && currentChar == '[') {
                key = inStr.substring(startIndex, i - 1).trim();
                map.put(key, new ArrayList<>());
                startIndex = i + 1;
                continue;
            }
            if (stack.size() == 0 && currentChar == ']') {
                map.get(key).add(inStr.substring(startIndex, i).trim());
                startIndex = i + 2;
                key = null;
                continue;
            }
            if (key != null && stack.size() == 0 && currentChar == ',') {
                map.get(key).add(inStr.substring(startIndex, i).trim());
                startIndex = i + 1;
                continue;
            }
            if (currentChar == '(') {
                stack.push(i);
                continue;
            }
            if (currentChar == ')') {
                stack.pop();
            }
        }
        return map;
    }

    public static boolean hasField(String fragement, String field) {
        if (field.startsWith("$")) {
            field = field.substring(1, field.length());
        }
        String sign = "([^a-zA-Z0-9_])";
        Pattern p = Pattern.compile(sign + field + sign);
        Matcher m = p.matcher(" " + fragement + " ");
        while (m.find()) {
            return true;
        }
        return false;
    }

    public static String replaceField(String operation, String field, String fragement) {
        String newOperation = operation;
        String sign = "([^a-zA-Z0-9_])";
        Pattern p = Pattern.compile(sign + field + sign);
        Matcher m = p.matcher(operation);
        while (m.find()) {
            newOperation = newOperation.substring(0, m.start(1) + 1) + fragement + newOperation.substring(m.end(1) + 1, newOperation.length());
        }
        return newOperation;
    }

    /**
     * 转换map
     *
     * @param inStr
     * @return
     */
    public static Map parse(String inStr, String... blackKeys) {
        if (getStrIsNest(inStr)) {
            return parseForNest(inStr, blackKeys);
        } else {
            return parseForNotNest(inStr);
        }
    }

    /**
     * 嵌套解析
     *
     * @param inStr
     * @return
     */
    public static Map parseForNest(String inStr, String... blackKeys) {
        Map map = new HashMap();
        List<Integer> nestList = getNestList(inStr);
        int num = nestList.size() / 2;
        for (int i = 0; i < num; i++) {
            if (i == 0) {
                String substring = inStr.substring(0, nestList.get(i + 1) + 1);
                String key = getMapKey(substring);
                boolean isNext = true;
                for (int j = 0; j < blackKeys.length; j++) {
                    if (key.equals(blackKeys[j])) {
                        isNext = false;
                    }
                }
                if (isNext) {
                    if (getStrIsNest(substring)) {
                        map.put(key, getMapListNest(substring));
                    } else {
                        map.put(key, getMapList(substring));
                    }
                } else {
                    map.put(key, getTextValue(substring));
                }
            } else {
                String substring = inStr.substring(nestList.get(2 * i - 1) + 2, nestList.get(2 * i + 1) + 1);
                String key = getMapKey(substring);
                boolean isNext = true;
                for (int j = 0; j < blackKeys.length; j++) {
                    if (key.equals(blackKeys[j])) {
                        isNext = false;
                    }
                }
                if (isNext) {
                    if (getStrIsNest(substring)) {
                        map.put(key, getMapListNest(substring));
                    } else {
                        map.put(key, getMapList(substring));
                    }
                } else {
                    map.put(key, getTextValue(substring));
                }
            }
        }
        return map;
    }

    /**
     * @return java.util.Map
     * @author lewnn
     * @operate
     * @date 2021/8/20 15:03
     */
    public static Map parseForSelect(String inStr) {
        return getKeyAndValues(inStr);
    }

    /**
     * 非嵌套解析
     *
     * @param inStr
     * @return
     */
    public static Map parseForNotNest(String inStr) {
        String[] split = inStr.split("], ");
        Map map = new HashMap();
        for (int i = 0; i < split.length; i++) {
            if (i == split.length - 1) {
                map.put(getMapKey(split[i]), getMapList(split[i]));
            } else {
                map.put(getMapKey(split[i] + "]"), getMapList(split[i] + "]"));
            }
        }
        return map;
    }

    /**
     * 获取主键 例子where=[(sid = sid0)]  =[ 前即key
     *
     * @param splitStr
     * @return
     */
    public static String getMapKey(String splitStr) {
        if (splitStr == null || splitStr.indexOf("=[") == -1) {
            return "";
        }
        return splitStr.substring(0, splitStr.indexOf("=[")).replace(" ", "");
    }

    public static String getMapKeyOnlySelectOrField(String splitStr) {
        if (splitStr == null || splitStr.indexOf("=[") == -1) {
            return "";
        }
        if (splitStr.contains("select=[")) {
            return "select";
        } else if (splitStr.contains("field=[")) {
            return "field";
        }
        return "";
    }

    /**
     * 获取主键对应的集合值  例子where=[(sid = sid0)]  []中内容为集合内容
     *
     * @param splitStr
     * @return
     */
    public static List getMapList(String splitStr) {
        if (splitStr == null || splitStr.indexOf("[") == -1 || splitStr.indexOf("]") == -1) {
            return new ArrayList();
        }
        return Arrays.stream(splitStr.substring(splitStr.indexOf("[") + 1, splitStr.lastIndexOf("]")).split(", ")).collect(Collectors.toList());
    }

    /**
     * 获取嵌套主键对应的集合值  例子table=[[default_catalog, default_database, score, project=[sid, cls, score]]]  []中内容为集合内容
     *
     * @param splitStr
     * @return
     */
    public static List getMapListNest(String splitStr) {
        List list = new ArrayList();
        if (splitStr == null || splitStr.indexOf("[") == -1 || splitStr.indexOf("]") == -1) {
            return new ArrayList();
        }
        String substring = splitStr.substring(splitStr.indexOf("[") + 1, splitStr.lastIndexOf("]")).trim();
        //样例 [default_catalog, default_database, score, project=[sid, cls, score]]
        if (substring.startsWith("[")) {
            //还是一个集合
            list.add(getMapListNest(substring));
        } else {
            //不是一个集合 而是元素时  default_catalog, default_database, score, project=[sid, cls, score], course=[en, ds, as]
            //嵌套所以  还会有[]
            List<Integer> nestList = getNestList(substring);
            int num = nestList.size() / 2;
            String[] str = new String[num];
            for (int i = 0; i < num; i++) {
                str[i] = substring.substring(nestList.get(2 * i), nestList.get(2 * i + 1) + 1);
            }
            //倒叙替换 去除集合内容干扰
            for (int i = num - 1; i >= 0; i--) {
                substring = substring.substring(0, nestList.get(2 * i)) + "_str" + i + "_" + substring.substring(nestList.get(2 * i + 1) + 1);
            }
            //去除干扰后   default_catalog, default_database, score, project=_str0_, course=_str1_
            // _str0_ = [sid, cls, score]
            // _str1_ = [en, ds, as]
            String[] split = substring.split(", ");
            int index = 0;
            for (String s : split) {
                if (s.startsWith("[")) {
                    list.add(getMapListNest(splitStr));
                } else if (s.indexOf("_str") != -1) {
                    // project=_str0_  还原集合干扰 project=[sid, cls, score]
                    list.add(parseForNest(s.replace("_str" + index + "_", str[index])));
                    index++;
                } else {
                    list.add(s);
                }
            }
        }
        return list;
    }

    private static String getTextValue(String splitStr) {
        return splitStr.substring(splitStr.indexOf("[") + 1, splitStr.lastIndexOf("]"));
    }
}
