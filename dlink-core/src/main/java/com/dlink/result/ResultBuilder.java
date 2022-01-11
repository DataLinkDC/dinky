package com.dlink.result;

import com.dlink.parser.SqlType;
import org.apache.flink.table.api.TableResult;

/**
 * ResultBuilder
 *
 * @author wenmo
 * @since 2021/5/25 15:59
 **/
public interface ResultBuilder {

    static ResultBuilder build(SqlType operationType, Integer maxRowNum, boolean isChangeLog, boolean isAutoCancel){
        switch (operationType){
            case SELECT:
                return new SelectResultBuilder(maxRowNum,isChangeLog,isAutoCancel);
            case SHOW:
            case DESCRIBE:
                return new ShowResultBuilder(false);
            case INSERT:
                return new InsertResultBuilder();
            default:
                return new DDLResultBuilder();
        }
    }

    IResult getResult(TableResult tableResult);
}
