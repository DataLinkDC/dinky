package com.dlink.trans;

import com.dlink.constant.FlinkSQLConstant;

/**
 * SqlUtil
 *
 * @author wenmo
 * @since 2021/5/25 15:50
 **/
public class Operations {
    /**
     * 获取操作类型
     *
     * @param sql
     * @return
     */
    public static String getOperationType(String sql) {
        String sqlTrim = sql.replaceAll("[\\s\\t\\n\\r]", "").toUpperCase();
        if (sqlTrim.startsWith(FlinkSQLConstant.CREATE)) {
            return FlinkSQLConstant.CREATE;
        }
        if (sqlTrim.startsWith(FlinkSQLConstant.ALTER)) {
            return FlinkSQLConstant.ALTER;
        }
        if (sqlTrim.startsWith(FlinkSQLConstant.INSERT)) {
            return FlinkSQLConstant.INSERT;
        }
        if (sqlTrim.startsWith(FlinkSQLConstant.DROP)) {
            return FlinkSQLConstant.INSERT;
        }
        if (sqlTrim.startsWith(FlinkSQLConstant.SELECT)) {
            return FlinkSQLConstant.SELECT;
        }
        if (sqlTrim.startsWith(FlinkSQLConstant.SHOW)) {
            return FlinkSQLConstant.SHOW;
        }
        return FlinkSQLConstant.UNKNOWN_TYPE;
    }
}
