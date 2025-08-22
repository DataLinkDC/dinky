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

import org.dinky.sandbox.Sandbox;
import org.dinky.sandbox.SandboxFactory;
import org.dinky.sandbox.metadata.TableId;
import org.dinky.sandbox.metadata.TableType;
import org.dinky.utils.FlinkUtil;

import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.Column;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.collect.Streams;

import lombok.extern.slf4j.Slf4j;

/**
 * ResultRunnable
 *
 * @since 2021/7/1 22:50
 */
@Slf4j
public class ResultRunnable implements Runnable {

    private final TableResult tableResult;
    private final String id;
    private final Integer maxRowNum;
    private final boolean isChangeLog;
    private final boolean isAutoCancel;
    private final String timeZone;
    private Consumer<String> callback;

    private final Sandbox sandbox;

    public ResultRunnable(
            TableResult tableResult,
            String id,
            Integer maxRowNum,
            boolean isChangeLog,
            boolean isAutoCancel,
            String timeZone) {
        this.tableResult = tableResult;
        this.id = id;
        this.maxRowNum = maxRowNum;
        this.isChangeLog = isChangeLog;
        this.isAutoCancel = isAutoCancel;
        this.timeZone = timeZone;
        this.sandbox = SandboxFactory.getDefaultSandbox();
    }

    public ResultRunnable registerCallback(Consumer<String> callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void run() {
        log.info("ResultRunnable start. Job id: {}", id);
        try {
            tableResult.getJobClient().ifPresent(jobClient -> {
                try {
                    if (isChangeLog) {
                        catchRow(TableType.CHANGE_LOG);
                    } else {
                        catchRow(TableType.PRIMARY_KEY_TABLE);
                    }
                    if (isAutoCancel) {
                        cancelJob();
                    }
                    if (Objects.nonNull(callback)) {
                        sandbox.handleFinished(id, callback);
                    }
                } catch (Exception e) {
                    log.error(String.format(e.toString()));
                }
            });
        } catch (Exception e) {
            // Nothing to do
        }
    }

    private void cancelJob() {
        try {
            tableResult.getJobClient().ifPresent(JobClient::cancel);
            log.info("Auto cancel job. Job id: {}", id);
        } catch (Exception e) {
            // It is normal to encounter an exception
            // when trying to close a batch task that is already closed.
            log.warn("Auto cancel job failed. Job id: {}", id, e);
        }
    }

    private void catchRow(TableType tableType) {
        TableId tableId = TableId.withPrivate(id);
        List<Column> columns = FlinkUtil.getColumns(tableResult);
        int[] primaryKeyIndexes = FlinkUtil.getPrimaryKeyIndexes(tableResult);
        sandbox.registerTable(tableId, tableType, columns, primaryKeyIndexes);
        Streams.stream(tableResult.collect()).limit(maxRowNum).forEach(row -> {
            sandbox.writeRowData(tableId, row, timeZone);
        });
    }
}
