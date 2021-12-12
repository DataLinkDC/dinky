package com.dlink.utils;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;

/**
 * SqlUtil
 *
 * @author wenmo
 * @since 2021/7/14 21:57
 */
public class SqlUtil {

    public static String[] getStatements(String sql,String sqlSeparator){
        if(Asserts.isNullString(sql)){
            return new String[0];
        }
        return sql.split(sqlSeparator);
    }

    public static String removeNote(String sql){
        if(Asserts.isNotNullString(sql)) {
            sql = sql.replaceAll("\u00A0", " ").replaceAll("--([^'\r\n]{0,}('[^'\r\n]{0,}'){0,1}[^'\r\n]{0,}){0,}", "").trim();
        }
        return sql;
    }
}
