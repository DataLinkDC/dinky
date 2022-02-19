package com.dlink.interceptor;

import org.apache.flink.table.api.TableResult;

/**
 * FlinkInterceptorResult
 *
 * @author wenmo
 * @since 2022/2/17 16:36
 **/
public class FlinkInterceptorResult {

    private boolean noExecute;
    private TableResult tableResult;

    public FlinkInterceptorResult() {
    }

    public FlinkInterceptorResult(boolean noExecute, TableResult tableResult) {
        this.noExecute = noExecute;
        this.tableResult = tableResult;
    }

    public boolean isNoExecute() {
        return noExecute;
    }

    public void setNoExecute(boolean noExecute) {
        this.noExecute = noExecute;
    }

    public TableResult getTableResult() {
        return tableResult;
    }

    public void setTableResult(TableResult tableResult) {
        this.tableResult = tableResult;
    }

    public static FlinkInterceptorResult buildResult(TableResult tableResult) {
        return new FlinkInterceptorResult(false, tableResult);
    }

    public static FlinkInterceptorResult build(boolean noExecute, TableResult tableResult) {
        return new FlinkInterceptorResult(noExecute, tableResult);
    }
}
