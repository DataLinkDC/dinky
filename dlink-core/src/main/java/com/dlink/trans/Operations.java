package com.dlink.trans;

import com.dlink.constant.FlinkSQLConstant;
import com.dlink.parser.SqlType;
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

    public static SqlType getSqlTypeFromStatements(String statement){
        String[] statements = statement.split(";");
        SqlType sqlType = SqlType.UNKNOWN;
        for (String item : statements) {
            if (item.trim().isEmpty()) {
                continue;
            }
            sqlType = Operations.getOperationType(item);
            if(FlinkSQLConstant.INSERT.equals(sqlType)||FlinkSQLConstant.SELECT.equals(sqlType)){
                return sqlType;
            }
        }
        return sqlType;
    }

    public static SqlType getOperationType(String sql) {
        String sqlTrim = sql.replaceAll("[\\s\\t\\n\\r]", "").toUpperCase();
        SqlType type = SqlType.UNKNOWN;
        for (SqlType sqlType : SqlType.values()) {
            if (sqlTrim.startsWith(sqlType.getType())) {
                type = sqlType;
                break;
            }
        }
        return type;
        /*if (sqlTrim.startsWith(FlinkSQLConstant.CREATE)) {
            return FlinkSQLConstant.CREATE;
        }
        if (sqlTrim.startsWith(FlinkSQLConstant.ALTER)) {
            return FlinkSQLConstant.ALTER;
        }
        if (sqlTrim.startsWith(FlinkSQLConstant.INSERT)) {
            return FlinkSQLConstant.INSERT;
        }
        if (sqlTrim.startsWith(FlinkSQLConstant.DROP)) {
            return FlinkSQLConstant.DROP;
        }
        if (sqlTrim.startsWith(FlinkSQLConstant.SELECT)) {
            return FlinkSQLConstant.SELECT;
        }
        if (sqlTrim.startsWith(FlinkSQLConstant.SHOW)) {
            return FlinkSQLConstant.SHOW;
        }
        return FlinkSQLConstant.UNKNOWN;*/
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
