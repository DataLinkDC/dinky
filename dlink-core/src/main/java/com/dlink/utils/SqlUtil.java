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

    public static String[] getStatements(String sql){
        if(Asserts.isNullString(sql)){
            return new String[0];
        }
        return sql.split(FlinkSQLConstant.SEPARATOR);
    }
}
