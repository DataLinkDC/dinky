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

package com.dlink.result;

import com.dlink.constant.FlinkConstant;
import com.dlink.utils.FlinkUtil;

import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Streams;

import lombok.extern.slf4j.Slf4j;

/**
 * ResultRunnable
 *
 * @author wenmo
 * @since 2021/7/1 22:50
 */
@Slf4j
public class ResultRunnable implements Runnable {

    private static final String nullColumn = "";
    private final TableResult tableResult;
    private final Integer maxRowNum;
    private final boolean isChangeLog;
    private final boolean isAutoCancel;
    private final String timeZone;

    public ResultRunnable(TableResult tableResult, Integer maxRowNum, boolean isChangeLog, boolean isAutoCancel,
            String timeZone) {
        this.tableResult = tableResult;
        this.maxRowNum = maxRowNum;
        this.isChangeLog = isChangeLog;
        this.isAutoCancel = isAutoCancel;
        this.timeZone = timeZone;
    }

    @Override
    public void run() {
        try {
            tableResult.getJobClient().ifPresent(jobClient -> {
                String jobId = jobClient.getJobID().toHexString();
                if (!ResultPool.containsKey(jobId)) {
                    ResultPool.put(new SelectResult(jobId, new ArrayList<>(), new LinkedHashSet<>()));
                }

                try {
                    if (isChangeLog) {
                        catchChangLog(ResultPool.get(jobId));
                    } else {
                        catchData(ResultPool.get(jobId));
                    }
                } catch (Exception e) {
                    log.error(String.format(e.toString()));
                }
            });
        } catch (Exception e) {
            // Nothing to do
        }
    }

    private void catchChangLog(SelectResult selectResult) {
        List<Map<String, Object>> rows = selectResult.getRowData();
        List<String> columns = FlinkUtil.catchColumn(tableResult);

        Streams.stream(tableResult.collect()).limit(maxRowNum).forEach(row -> {
            Map<String, Object> map = getFieldMap(columns, row);
            map.put(FlinkConstant.OP, row.getKind().shortString());
            rows.add(map);
        });

        columns.add(0, FlinkConstant.OP);
        selectResult.setColumns(new LinkedHashSet<>(columns));

        if (isAutoCancel) {
            tableResult.getJobClient().ifPresent(JobClient::cancel);
        }
    }

    private void catchData(SelectResult selectResult) {
        List<Map<String, Object>> rows = selectResult.getRowData();
        List<String> columns = FlinkUtil.catchColumn(tableResult);

        Streams.stream(tableResult.collect()).limit(maxRowNum).forEach(row -> {
            Map<String, Object> map = getFieldMap(columns, row);
            if (RowKind.UPDATE_BEFORE == row.getKind() || RowKind.DELETE == row.getKind()) {
                rows.remove(map);
            } else {
                rows.add(map);
            }
        });

        selectResult.setColumns(new LinkedHashSet<>(columns));
    }

    private Map<String, Object> getFieldMap(List<String> columns, Row row) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < row.getArity(); ++i) {
            Object field = row.getField(i);
            String column = columns.get(i);
            if (field == null) {
                map.put(column, nullColumn);
            } else if (field instanceof Instant) {
                map.put(column, ((Instant) field).atZone(ZoneId.of(timeZone)).toLocalDateTime().toString());
            } else {
                map.put(column, field);
            }
        }
        return map;
    }
}
