package com.dlink.result;

import com.dlink.constant.FlinkSQLConstant;
import org.apache.flink.table.api.TableColumn;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 * SelectBuilder
 *
 * @author wenmo
 * @since 2021/5/25 16:03
 **/
public class SelectResultBuilder implements ResultBuilder {

    private Integer maxRowNum;
    private boolean printRowKind;
    private String nullColumn;

    public SelectResultBuilder(Integer maxRowNum, String nullColumn, boolean printRowKind) {
        this.maxRowNum = maxRowNum;
        this.printRowKind = printRowKind;
        this.nullColumn = nullColumn;
    }

    @Override
    public IResult getResult(TableResult tableResult) {
        if (tableResult.getJobClient().isPresent()) {
            String jobId = tableResult.getJobClient().get().getJobID().toHexString();
            ResultRunnable runnable = new ResultRunnable(tableResult, maxRowNum, printRowKind, nullColumn);
            Thread thread = new Thread(runnable, jobId);
            thread.start();
            return SelectResult.buildSuccess(jobId);
        }else{
            return SelectResult.buildFailed();
        }
    }

}
