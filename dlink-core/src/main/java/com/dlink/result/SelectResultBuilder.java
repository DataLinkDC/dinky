package com.dlink.result;

import org.apache.flink.table.api.TableResult;

/**
 * SelectBuilder
 *
 * @author wenmo
 * @since 2021/5/25 16:03
 **/
public class SelectResultBuilder implements ResultBuilder {

    private Integer maxRowNum;
    private boolean isChangeLog;
    private boolean isAutoCancel;

    public SelectResultBuilder(Integer maxRowNum, boolean isChangeLog, boolean isAutoCancel) {
        this.maxRowNum = maxRowNum;
        this.isChangeLog = isChangeLog;
        this.isAutoCancel = isAutoCancel;
    }

    @Override
    public IResult getResult(TableResult tableResult) {
        if (tableResult.getJobClient().isPresent()) {
            String jobId = tableResult.getJobClient().get().getJobID().toHexString();
            ResultRunnable runnable = new ResultRunnable(tableResult, maxRowNum, isChangeLog, isAutoCancel);
            Thread thread = new Thread(runnable, jobId);
            thread.start();
            return SelectResult.buildSuccess(jobId);
        } else {
            return SelectResult.buildFailed();
        }
    }

}
