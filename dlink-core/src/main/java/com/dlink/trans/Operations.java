package com.dlink.trans;

import com.dlink.constant.FlinkSQLConstant;
import com.dlink.trans.ddl.CreateAggTableOperation;

/**
 * Operations
 *
 * @author wenmo
 * @since 2021/5/25 15:50
 **/
public class Operations {

    private static Operation[] operations = {
      new CreateAggTableOperation()
    };

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

    public static Operation buildOperation(String statement){
        statement = statement.replace("\n"," ").replaceAll("\\s{1,}", " ").trim();
        String sql = statement.toUpperCase();
        for (int i = 0; i < operations.length; i++) {
            if(sql.startsWith(operations[i].getHandle())){
                return operations[i].create(statement);
            }
        }
        return null;
    }
}
