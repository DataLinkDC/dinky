package com.dlink.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author wenmo
 * @since 2021/6/13 21:09
 */
public class SqlExtractUtil {

    private String[] KEY_WORDS = {
            "AGG",
            "AGGTABLE",
            "AGGTABLES",
            "AND",
            "AS",
            "BY",
            "CREATE",
            "DROP",
            "FRAGMENT",
            "FRAGMENTS",
            "FROM",
            "GROUP",
            "LIMIT",
            "OR",
            "ORDER",
            "SELECT",
            "SHOW",
            "TABLE",
            "TABLES",
            "VIEW",
            "VIEWS",
            "WHERE"
    };

    /**
     * 获取固定分割符之间的内容 如 select a.name ,b.name from 中select和from之间的内容
     *
     * @param original
     * @param upperStr
     * @param start
     * @param end
     * @return
     */
    public static String getSubStringPara(String original, String upperStr, String beforeStr, String start, String end, String secondEnd, boolean isStopTrim) {
        int beforeIndex = upperStr.indexOf(" " + beforeStr + " ");
        int startIndex = upperStr.indexOf(" " + start + " ", beforeIndex);
        if (startIndex < 0) {
            return "";
        }
        if (end.length() == 0) {
            String resWitgout = original.substring(start.length() + startIndex + 1);
            return isStopTrim ?  resWitgout: resWitgout.trim();
        }
        int endIndex = upperStr.indexOf(" " + end + " ", startIndex);
        if (endIndex < 0) {
            endIndex = upperStr.indexOf(" " + secondEnd + " ", startIndex);
        }
        if (endIndex < 0) {
            return "";
        }
        String res = original.substring(start.length() + startIndex + 1, endIndex);
        return isStopTrim ?  res: res.trim();
    }


    /**
     * 处理一类固定分隔符的工具  例如条件语句 s1.name = s2.name and s1.age = s2.age And s1.sex=s2.sex
     * 转换成 list = ["s1.name = s2.name", "and s1.age = s2", "And s1.sex=s2.sex"]
     *
     * @param original
     * @param upperStr
     * @param start
     * @param end
     * @param secondLayer
     * @return
     */
    public static List<String> getSubStringList(String original, String upperStr, String start, String end, String secondLayer) {
        String subStringPara = getSubStringPara(original, upperStr, start, start, end, "", false);
        List<String> list = new ArrayList<>();
        if (subStringPara.length() == 0) {
            return list;
        }
        String str[] = subStringPara.split(" " + secondLayer.toUpperCase() + " ");
        for (int i = 0; i < str.length; i++) {
            String[] split = str[i].split(" " + secondLayer.toLowerCase() + " ");
            for (int j = 0; j < split.length; j++) {
                if (split[j].replace(" ", "").length() > 0) {
                    list.add(split[j]);
                }
            }
        }
        return list;
    }
}
