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

import java.util.Objects;

import com.google.common.collect.Lists;

/**
 * SelectBuilder
 *
 * @since 2021/5/25 16:03
 */
public class SelectResultBuilder extends AbstractResultBuilder implements ResultBuilder {

    private final Integer maxRowNum;
    private final boolean isChangeLog;
    private final boolean isAutoCancel;
    private final String timeZone;

    public SelectResultBuilder(
            String id, Integer maxRowNum, boolean isChangeLog, boolean isAutoCancel, String timeZone) {
        this.id = id;
        this.maxRowNum = Asserts.isNotNull(maxRowNum) ? maxRowNum : 100;
        this.isChangeLog = isChangeLog;
        this.isAutoCancel = isAutoCancel;
        this.timeZone = timeZone;
    }

    @Override
    public IResult getResult(TableResult tableResult) {
        if (tableResult.getJobClient().isPresent()) {
            String jobId = tableResult.getJobClient().get().getJobID().toHexString();
            ResultRunnable runnable =
                    new ResultRunnable(tableResult, id, maxRowNum, isChangeLog, isAutoCancel, timeZone);
            threadPoolExecutor.execute(runnable);
            return SelectResult.buildSuccess(jobId);
        } else {
            return SelectResult.buildFailed();
        }
    }

    /**
     * Get the results and store them persistently.
     *
     * @param tableResult table result
     * @param jobHandler  job handler
     * @return IResult
     */
    @Override
    public IResult getResultWithPersistence(TableResult tableResult, JobHandler jobHandler) {
        if (Objects.isNull(tableResult)) {
            return SelectResult.buildFailed();
        }
        if (tableResult.getJobClient().isPresent()) {
            String jobId = tableResult.getJobClient().get().getJobID().toHexString();
            ResultRunnable runnable =
                    new ResultRunnable(tableResult, id, maxRowNum, isChangeLog, isAutoCancel, timeZone);
            runnable.registerCallback((s, selectResult) -> {
                jobHandler.persistResultData(Lists.newArrayList(s));
            });
            threadPoolExecutor.execute(runnable);
            return SelectResult.buildSuccess(jobId);
        } else {
            return SelectResult.buildFailed();
        }
    }
}
