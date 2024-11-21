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

package org.dinky.data.result;

import org.dinky.assertion.Asserts;
import org.dinky.job.JobHandler;

import org.apache.flink.table.api.TableResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockResultBuilder extends AbstractResultBuilder implements ResultBuilder {
    private final Integer maxRowNum;
    private final boolean isChangeLog;
    private final boolean isAutoCancel;

    public MockResultBuilder(String id, Integer maxRowNum, boolean isChangeLog, boolean isAutoCancel) {
        this.id = id;
        this.maxRowNum = maxRowNum;
        this.isChangeLog = isChangeLog;
        this.isAutoCancel = isAutoCancel;
    }

    @Override
    public IResult getResult(TableResult tableResult) {
        if (tableResult.getJobClient().isPresent()) {
            MockResultRunnable runnable = new MockResultRunnable(tableResult, id, maxRowNum, isChangeLog, isAutoCancel);
            threadPoolExecutor.execute(runnable);
            return SelectResult.buildSuccess(
                    tableResult.getJobClient().get().getJobID().toHexString());
        } else {
            return SelectResult.buildFailed();
        }
    }

    @Override
    public IResult getResultWithPersistence(TableResult tableResult, JobHandler jobHandler) {
        if (Asserts.isNull(tableResult)) {
            return SelectResult.buildFailed();
        }
        if (tableResult.getJobClient().isPresent()) {
            MockResultRunnable runnable = new MockResultRunnable(tableResult, id, maxRowNum, isChangeLog, isAutoCancel);
            runnable.registerCallback((s, selectResult) -> {
                jobHandler.persistResultData(com.google.common.collect.Lists.newArrayList(s));
            });
            threadPoolExecutor.execute(runnable);
            return SelectResult.buildMockedResult(id);
        } else {
            return SelectResult.buildFailed();
        }
    }
}
