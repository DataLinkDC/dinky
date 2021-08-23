package com.dlink.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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
        Map map = new HashMap();
        List<Integer> bracketsList = getBracketsList(inStr);
        String mapKey = getMapKey(inStr);
        List<String> list = new ArrayList<>();
        int size = bracketsList.size();
        if (size % 2 != 0) {
            // 此处若size部位偶数 则返回空   可能会存在问题
            return map;
        } else {
            int numSize = size / 2;//括号对数
            for (int i = 0; i < numSize; i++) {
                String msgStr = "";
                if (2 * i + 2 >= size) {
                    msgStr = inStr.substring(bracketsList.get(2 * i), inStr.lastIndexOf("]"));
                } else {
                    msgStr = inStr.substring(bracketsList.get(2 * i), bracketsList.get(2 * i + 2));
                    msgStr = msgStr.substring(0, msgStr.lastIndexOf(",") > 0 ? msgStr.lastIndexOf(",") : msgStr.length());
                }
                list.add(msgStr);
            }
        }
        map.put(mapKey, list);
        return map;
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
