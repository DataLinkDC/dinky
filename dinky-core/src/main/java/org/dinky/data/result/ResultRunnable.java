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

import org.dinky.constant.FlinkConstant;
import org.dinky.utils.FlinkUtil;

import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import com.google.common.collect.Streams;

import lombok.extern.slf4j.Slf4j;

/**
 * ResultRunnable
 *
 * @since 2021/7/1 22:50
 */
@Slf4j
public class ResultRunnable implements Runnable {

    private static final String nullColumn = "";
    private final TableResult tableResult;
    private final String id;
    private final Integer maxRowNum;
    private final boolean isChangeLog;
    private final boolean isAutoCancel;
    private final String timeZone;
    private BiConsumer<String, SelectResult> callback;
    private static final DateTimeFormatter FORMATTER_CACHE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
    }

    public ResultRunnable registerCallback(BiConsumer<String, SelectResult> callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void run() {
        log.info("ResultRunnable start. Job id: {}", id);
        try {
            tableResult.getJobClient().ifPresent(jobClient -> {
                if (!ResultPool.containsKey(id)) {
                    ResultPool.put(new SelectResult(id, new ArrayList<>(), new LinkedHashSet<>()));
                }
                try {
                    if (isChangeLog) {
                        catchChangLog(ResultPool.get(id));
                    } else {
                        catchData(ResultPool.get(id));
                    }
                    if (isAutoCancel) {
                        cancelJob();
                    }
                    ResultPool.get(id).setDestroyed(Boolean.TRUE);
                    if (Objects.nonNull(callback)) {
                        callback.accept(id, ResultPool.get(id));
                    }
                } catch (Exception e) {
                    log.error(String.format(e.toString()));
                } finally {
                    ResultPool.remove(id);
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

    private void catchChangLog(SelectResult selectResult) {
        List<Map<String, Object>> rows = selectResult.getRowData();
        List<String> columns = FlinkUtil.catchColumn(tableResult);

        columns.add(0, FlinkConstant.OP);
        selectResult.setColumns(new LinkedHashSet<>(columns));
        Streams.stream(tableResult.collect()).limit(maxRowNum).forEach(row -> {
            Map<String, Object> map = getFieldMap(columns.subList(1, columns.size()), row);
            map.put(FlinkConstant.OP, row.getKind().shortString());
            rows.add(map);
        });
        log.info("Catch change log finish. Job id: {}", selectResult.getJobId());
    }

    private void catchData(SelectResult selectResult) {
        List<Map<String, Object>> rows = selectResult.getRowData();
        List<String> columns = FlinkUtil.catchColumn(tableResult);

        selectResult.setColumns(new LinkedHashSet<>(columns));
        Streams.stream(tableResult.collect()).limit(maxRowNum).forEach(row -> {
            Map<String, Object> map = getFieldMap(columns, row);
            if (RowKind.UPDATE_BEFORE == row.getKind() || RowKind.DELETE == row.getKind()) {
                rows.remove(map);
            } else {
                rows.add(map);
            }
        });
        log.info("Catch data finish. Job id: {}", selectResult.getJobId());
    }

    private Map<String, Object> getFieldMap(List<String> columns, Row row) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < row.getArity(); ++i) {
            Object field = row.getField(i);
            String column = columns.get(i);
            if (field == null) {
                map.put(column, nullColumn);
            } else if (field instanceof Instant) {
                map.put(
                        column,
                        ((Instant) field)
                                .atZone(ZoneId.of(timeZone))
                                .toLocalDateTime()
                                .format(FORMATTER_CACHE));
            } else if (field instanceof Boolean) {
                map.put(column, field.toString());
            } else if (field instanceof LocalDateTime) {
                map.put(column, ((LocalDateTime) field).format(FORMATTER_CACHE));
            } else {
                map.put(column, field);
            }
        }
        return map;
    }
}
