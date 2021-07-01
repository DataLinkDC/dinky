package com.dlink.result;

import com.dlink.constant.FlinkSQLConstant;
import org.apache.flink.table.api.TableResult;

/**
 * ResultBuilder
 *
 * @author wenmo
 * @since 2021/5/25 15:59
 **/
public interface ResultBuilder {

    static ResultBuilder build(String operationType, Integer maxRowNum, String nullColumn, boolean printRowKind){
        switch (operationType.toUpperCase()){
            case SelectResultBuilder.OPERATION_TYPE:
                return new SelectResultBuilder(maxRowNum,nullColumn,printRowKind);
            case FlinkSQLConstant.SHOW:
            case FlinkSQLConstant.DESCRIBE:
                return new ShowResultBuilder(nullColumn,printRowKind);
            case InsertResultBuilder.OPERATION_TYPE:
                return new InsertResultBuilder();
            default:
                return new DDLResultBuilder();
        }
    }

    IResult getResult(TableResult tableResult);
}
