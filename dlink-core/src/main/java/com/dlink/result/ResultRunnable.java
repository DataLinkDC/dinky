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

import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dlink.constant.FlinkConstant;
import com.dlink.utils.FlinkUtil;

/**
 * ResultRunnable
 *
 * @author wenmo
 * @since 2021/7/1 22:50
 */
public class ResultRunnable implements Runnable {

    private TableResult tableResult;
    private Integer maxRowNum;
    private boolean isChangeLog;
    private boolean isAutoCancel;
    private String timeZone = ZoneId.systemDefault().getId();
    private String nullColumn = "";

    public ResultRunnable(TableResult tableResult, Integer maxRowNum, boolean isChangeLog, boolean isAutoCancel, String timeZone) {
        this.tableResult = tableResult;
        this.maxRowNum = maxRowNum;
        this.isChangeLog = isChangeLog;
        this.isAutoCancel = isAutoCancel;
        this.timeZone = timeZone;
    }

    @Override
    public void run() {
        if (tableResult.getJobClient().isPresent()) {
            String jobId = tableResult.getJobClient().get().getJobID().toHexString();
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

            }
        }
    }

    private void catchChangLog(SelectResult selectResult) {
        List<String> columns = FlinkUtil.catchColumn(tableResult);
        columns.add(0, FlinkConstant.OP);
        Set<String> column = new LinkedHashSet(columns);
        selectResult.setColumns(column);
        List<Map<String, Object>> rows = selectResult.getRowData();
        Iterator<Row> it = tableResult.collect();
        while (it.hasNext()) {
            if (rows.size() >= maxRowNum) {
                if (isAutoCancel && tableResult.getJobClient().isPresent()) {
                    tableResult.getJobClient().get().cancel();
                }
                break;
            }
            Map<String, Object> map = new LinkedHashMap<>();
            Row row = it.next();
            map.put(columns.get(0), row.getKind().shortString());
            for (int i = 0; i < row.getArity(); ++i) {
                Object field = row.getField(i);
                if (field == null) {
                    map.put(columns.get(i + 1), nullColumn);
                } else {
                    if (field instanceof Instant) {
                        map.put(columns.get(i + 1), ((Instant) field).atZone(ZoneId.of(timeZone)).toLocalDateTime().toString());
                    } else {
                        map.put(columns.get(i + 1), field);
                    }
                }
            }
            rows.add(map);
        }
    }

    private void catchData(SelectResult selectResult) {
        List<String> columns = FlinkUtil.catchColumn(tableResult);
        Set<String> column = new LinkedHashSet(columns);
        selectResult.setColumns(column);
        List<Map<String, Object>> rows = selectResult.getRowData();
        Iterator<Row> it = tableResult.collect();
        while (it.hasNext()) {
            if (rows.size() >= maxRowNum) {
                break;
            }
            Map<String, Object> map = new LinkedHashMap<>();
            Row row = it.next();
            for (int i = 0; i < row.getArity(); ++i) {
                Object field = row.getField(i);
                if (field == null) {
                    map.put(columns.get(i), nullColumn);
                } else {
                    if (field instanceof Instant) {
                        map.put(columns.get(i), ((Instant) field).atZone(ZoneId.of(timeZone)).toLocalDateTime().toString());
                    } else {
                        map.put(columns.get(i), field);
                    }
                }
            }
            if (RowKind.UPDATE_BEFORE == row.getKind() || RowKind.DELETE == row.getKind()) {
                rows.remove(map);
            } else {
                rows.add(map);
            }
        }
    }
}
