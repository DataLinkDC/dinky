package org.apache.flink.table.test.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Author: lwjhn
 * Date: 2024/5/30 11:30
 * Description:
 */
public class SqlUtil {

    private static final String SEMICOLON = ";";
    private static final String SQL_SEPARATOR = ";\\s*(?:\\n|--.*)";

    private SqlUtil() {}

    public static String[] getStatements(String sql) {
        return getStatements(sql, SQL_SEPARATOR);
    }

    public static String[] getStatements(String sql, String sqlSeparator) {
        if (StringUtils.isBlank(sql)) {
            return new String[0];
        }

        final String localSqlSeparator = ";\\s*(?:\\n|--.*)";
        String[] splits = sql.replace("\r\n", "\n").split(localSqlSeparator);
        String lastStatement = splits[splits.length - 1].trim();
        if (lastStatement.endsWith(SEMICOLON)) {
            splits[splits.length - 1] = lastStatement.substring(0, lastStatement.length() - 1);
        }

        return splits;
    }

    public static String removeNote(String sql) {

        if (StringUtils.isNotBlank(sql)) {
            // Remove the special-space characters
            sql = sql.replaceAll("\u00A0", " ").replaceAll("[\r\n]+", "\n");
            // Remove annotations Support '--aa' , '/**aaa*/' , '//aa' , '#aaa'
            Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*[^+].*?\\*/|");
            String presult = p.matcher(sql).replaceAll("$1");
            return presult.trim();
        }
        return sql;
    }

    public static String[] preparedStatement(String sql) {
        return getStatements(removeNote(sql));
    }

    /*
    public static String replaceAllParam(String sql, String name, String value) {
        return sql.replaceAll("#\\{" + name + "\\}", value);
    }*/

    public static String replaceAllParam(String sql, Map<String, String> values) {
        if (StringUtils.isBlank(sql)) {
            return "";
        }
        for (Map.Entry<String, String> entry : values.entrySet()) {
            sql = replaceAllParam(sql, entry.getKey(), entry.getValue());
        }
        return sql;
    }

    public static String replaceAllParam(String sql, String name, String value) {
        return sql.replaceAll("\\$\\{" + name + "}", value);
    }


    public static String addLineNumber(String input) {
        String[] lines = input.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append(String.format("%-4d", i + 1));
            sb.append("  ");
            sb.append(lines[i]);
            sb.append("\n");
        }
        return sb.toString();
    }
}
