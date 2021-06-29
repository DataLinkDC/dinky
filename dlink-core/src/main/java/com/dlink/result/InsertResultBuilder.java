package com.dlink.result;

import com.dlink.constant.FlinkSQLConstant;
import org.apache.flink.table.api.TableResult;

/**
 * InsertBuilder
 *
 * @author wenmo
 * @since 2021/6/29 22:23
 */
public class InsertResultBuilder implements ResultBuilder {

    public static final String OPERATION_TYPE = FlinkSQLConstant.INSERT;

    @Override
    public IResult getResult(TableResult tableResult) {
        if(tableResult.getJobClient().isPresent()){
            String jobId = tableResult.getJobClient().get().getJobID().toHexString();
            return new InsertResult(jobId,true);
        }else{
            return new InsertResult(null,false);
        }
    }
}
