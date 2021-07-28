package com.dlink.plus;

import org.apache.flink.table.api.TableResult;

/**
 * SqlResult
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class SqlResult {
    private TableResult tableResult;
    private boolean isSuccess = true;
    private String errorMsg;

    public static SqlResult NULL = new SqlResult(false,"未检测到有效的Sql");

    public SqlResult(TableResult tableResult) {
        this.tableResult = tableResult;
    }

    public SqlResult(boolean isSuccess, String errorMsg) {
        this.isSuccess = isSuccess;
        this.errorMsg = errorMsg;
    }

    public TableResult getTableResult() {
        return tableResult;
    }

    public void setTableResult(TableResult tableResult) {
        this.tableResult = tableResult;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
