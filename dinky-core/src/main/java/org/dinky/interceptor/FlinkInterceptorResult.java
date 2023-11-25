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

package org.dinky.interceptor;

import org.apache.flink.table.api.TableResult;

/**
 * FlinkInterceptorResult
 *
 * @since 2022/2/17 16:36
 */
public class FlinkInterceptorResult {

    private boolean noExecute;
    private TableResult tableResult;

    public FlinkInterceptorResult() {}

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
