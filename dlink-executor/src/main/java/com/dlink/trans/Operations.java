package com.dlink.trans;

import com.dlink.parser.SqlType;
import com.dlink.trans.ddl.CreateAggTableOperation;
import com.dlink.trans.ddl.SetOperation;

/**
 * Operations
 *
 * @author wenmo
 * @since 2021/5/25 15:50
 **/
public class Operations {

    private static Operation[] operations = {
      new CreateAggTableOperation()
//            , new SetOperation()
    };

    public static SqlType getSqlTypeFromStatements(String statement){
        String[] statements = statement.split(";");
        SqlType sqlType = SqlType.UNKNOWN;
        for (String item : statements) {
            if (item.trim().isEmpty()) {
                continue;
            }
            sqlType = Operations.getOperationType(item);
            if(sqlType == SqlType.INSERT ||sqlType == SqlType.SELECT){
                return sqlType;
            }
        }
        return sqlType;
    }

    public static SqlType getOperationType(String sql) {
        String sqlTrim = sql.replaceAll("[\\s\\t\\n\\r]", "").trim().toUpperCase();
        SqlType type = SqlType.UNKNOWN;
        for (SqlType sqlType : SqlType.values()) {
            if (sqlTrim.startsWith(sqlType.getType())) {
                type = sqlType;
                break;
            }
        }
        return type;
    }

    public static Operation buildOperation(String statement){
        String sql = statement.replace("\n"," ").replaceAll("\\s{1,}", " ").trim().toUpperCase();
        for (int i = 0; i < operations.length; i++) {
            if(sql.startsWith(operations[i].getHandle())){
                return operations[i].create(statement);
            }
        }
        return null;
    }
}
