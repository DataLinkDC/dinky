package com.dlink.parser;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SingleSqlParserFactory
 *
 * @author wenmo
 * @since 2021/6/14 16:49
 */
public class SingleSqlParserFactory {

    public static Map<String,List<String>> generateParser(String sql) {
        BaseSingleSqlParser tmp = null;
//        sql = sql.replace("\n"," ").replaceAll("\\s{1,}", " ") +" ENDOFSQL";
        sql = sql.replace("\r\n"," ").replace("\n"," ") +" ENDOFSQL";
        if (contains(sql, "(insert\\s+into)(.+)(select)(.+)(from)(.+)")) {
            tmp = new InsertSelectSqlParser(sql);
        } else if (contains(sql, "(create\\s+aggtable)(.+)(as\\s+select)(.+)")) {
            tmp = new CreateAggTableSelectSqlParser(sql);
        } else if (contains(sql, "(select)(.+)(from)(.+)")) {
            tmp = new SelectSqlParser(sql);
        } else if (contains(sql, "(delete\\s+from)(.+)")) {
            tmp = new DeleteSqlParser(sql);
        } else if (contains(sql, "(update)(.+)(set)(.+)")) {
            tmp = new UpdateSqlParser(sql);
        } else if (contains(sql, "(insert\\s+into)(.+)(values)(.+)")) {
            tmp = new InsertSqlParser(sql);
        } else if (contains(sql, "(create\\s+table)(.+)")) {
        } else if (contains(sql, "(create\\s+database)(.+)")) {
        } else if (contains(sql, "(show\\s+databases)")) {
        } else if (contains(sql, "(use)(.+)")) {
        } else {
        }
        return tmp.splitSql2Segment();
    }

    /**
     * 看word是否在lineText中存在，支持正则表达式
     *
     * @param sql:要解析的sql语句
     * @param regExp:正则表达式
     * @return
     **/
    private static boolean contains(String sql, String regExp) {
        Pattern pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        return matcher.find();
    }
}

