/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.plus;

import org.apache.flink.table.api.TableResult;

/**
 * SqlResult
 *
 * @author wenmo
 * @since 2021/6/22
 */
public class SqlResult {

    private TableResult tableResult;
    private boolean isSuccess = true;
    private String errorMsg;

    public static SqlResult NULL = new SqlResult(false, "未检测到有效的Sql");

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
