package com.dlink.utils;

import com.dlink.assertion.Asserts;

/**
 * SqlUtil
 *
 * @author wenmo
 * @since 2021/7/14 21:57
 */
public class SqlUtil {

    private static final String SEMICOLON = ";";

    public static String[] getStatements(String sql, String sqlSeparator) {
        if (Asserts.isNullString(sql)) {
            return new String[0];
        }

        String[] splits = sql.split(sqlSeparator);
        String lastStatement = splits[splits.length - 1].trim();
        if (lastStatement.endsWith(SEMICOLON)){
            splits[splits.length - 1] = lastStatement.substring(0,lastStatement.length()-1);
        }

        return splits;
    }

    public static String removeNote(String sql) {
        if (Asserts.isNotNullString(sql)) {
            sql = sql.replaceAll("\u00A0", " ").replaceAll("--([^'\r\n]{0,}('[^'\r\n]{0,}'){0,1}[^'\r\n]{0,}){0,}", "").replaceAll("[\r\n]+", "\r\n").trim();
        }
        return sql;
    }

    public static String replaceAllParam(String sql, String name, String value) {
        return sql.replaceAll("\\$\\{" + name + "\\}", value);
    }
}
