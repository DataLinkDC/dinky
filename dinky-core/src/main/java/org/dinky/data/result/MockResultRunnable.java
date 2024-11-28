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
import org.dinky.constant.FlinkConstant;
import org.dinky.utils.JsonUtils;

import org.apache.flink.api.common.typeutils.base.MapSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.apache.flink.types.RowKind;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import lombok.extern.slf4j.Slf4j;

/**
 * ResultRunnable
 *
 * @since 2021/7/1 22:50
 */
@Slf4j
public class MockResultRunnable implements Runnable {

    private static final String nullColumn = "";
    private final TableResult tableResult;
    private final String id;
    private final Integer maxRowNum;
    private final boolean isChangeLog;
    private final boolean isAutoCancel;
    private BiConsumer<String, SelectResult> callback;
    private final String MOCK_RESULT_TABLE_IDENTIFIER = "dinkySinkResultTableIdentifier";
    private final String MOCK_RESULT_COLUMN_IDENTIFIER = "dinkySinkResultColumnIdentifier";
    private final MapSerializer<String, String> mapSerializer =
            new MapSerializer<>(new StringSerializer(), new StringSerializer());

    public MockResultRunnable(
            TableResult tableResult, String id, Integer maxRowNum, boolean isChangeLog, boolean isAutoCancel) {
        this.tableResult = tableResult;
        this.id = id;
        this.maxRowNum = maxRowNum;
        this.isChangeLog = isChangeLog;
        this.isAutoCancel = isAutoCancel;
    }

    public MockResultRunnable registerCallback(BiConsumer<String, SelectResult> callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void run() {
        log.info("MockResultRunnable start. Job id: {}", id);
        try {
            tableResult.getJobClient().ifPresent(jobClient -> {
                if (!ResultPool.containsKey(id)) {
                    ResultPool.put(SelectResult.buildMockedResult(id));
                }
                try {
                    if (isChangeLog) {
                        catchChangLog(ResultPool.get(id));
                    } else {
                        catchData(ResultPool.get(id));
                    }
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

    private void catchChangLog(SelectResult selectResult) {
        List<Map<String, Object>> rows = selectResult.getRowData();
        LinkedHashSet<String> columns = selectResult.getColumns();
        // get table identifiers
        ResolvedSchema resolvedSchema = tableResult.getResolvedSchema();
        List<String> tableIdentifierList = resolvedSchema.getColumnNames();
        // update row data map
        Map<String, List<Map<String, String>>> rowDataMap = new HashMap<>();
        Map<String, Integer> tableIdentifierIndexMap = new HashMap<>();
        JobClient jobClient = tableResult.getJobClient().get();
        while (!isAllSinkFinished(maxRowNum, rowDataMap, tableIdentifierList)) {
            try {
                boolean allSinkFinished = false;
                Map<String, Object> accumulatorMap = jobClient.getAccumulators().get();
                for (String tableIdentifier : tableIdentifierList) {
                    if (!tableIdentifierIndexMap.containsKey(tableIdentifier)) {
                        tableIdentifierIndexMap.put(tableIdentifier, 0);
                    } else if (tableIdentifierIndexMap.get(tableIdentifier) >= maxRowNum - 1) {
                        allSinkFinished = true;
                        continue;
                    }
                    Object accumulatorObject = accumulatorMap.get(tableIdentifier);
                    if (accumulatorObject instanceof List) {
                        List<?> serializerRowDataList = (List<?>) accumulatorObject;
                        for (int i = tableIdentifierIndexMap.get(tableIdentifier);
                                i < serializerRowDataList.size();
                                i++) {
                            Map<String, Object> rowDataWithTableIdentifier = new HashMap<>();
                            rowDataWithTableIdentifier.put(MOCK_RESULT_TABLE_IDENTIFIER, tableIdentifier);
                            // deserialize data from accumulator
                            Map<String, String> deserializeRowData =
                                    deserializeObjFromBytes((byte[]) serializerRowDataList.get(i));
                            if (tableIdentifierIndexMap.get(tableIdentifier) == 0) {
                                columns.add(generateResultColumns(tableIdentifier, deserializeRowData));
                            }
                            rowDataWithTableIdentifier.putAll(deserializeRowData);
                            // update row data map
                            rows.add(rowDataWithTableIdentifier);
                            tableIdentifierIndexMap.put(
                                    tableIdentifier, tableIdentifierIndexMap.get(tableIdentifier) + 1);
                            if (tableIdentifierIndexMap.get(tableIdentifier) >= maxRowNum) {
                                break;
                            }
                        }
                        log.info(
                                "Catch change log: table-{}: size-{},",
                                tableIdentifier,
                                tableIdentifierIndexMap.get(tableIdentifier));
                    }
                }
                if (jobClient.getJobStatus().get().isTerminalState()) {
                    log.info(
                            "JobClient status:{}",
                            jobClient.getJobStatus().get().toString());
                    break;
                }
                if (allSinkFinished && isAutoCancel) {
                    jobClient.cancel();
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Deserialize change log from accumulator failed, jobId: {}: \nError: {}", id, e);
                break;
            }
        }
        log.info("Catch change log finish. Job id: {}", selectResult.getJobId());
    }

    private void catchData(SelectResult selectResult) {
        List<Map<String, Object>> rows = selectResult.getRowData();
        LinkedHashSet<String> columns = selectResult.getColumns();
        // get table identifiers
        ResolvedSchema resolvedSchema = tableResult.getResolvedSchema();
        List<String> tableIdentifierList = resolvedSchema.getColumnNames();
        // update row data map
        Map<String, List<Map<String, String>>> rowDataMap = new HashMap<>();
        Map<String, Integer> tableIdentifierIndexMap = new HashMap<>();
        JobClient jobClient = tableResult.getJobClient().get();
        while (!isAllSinkFinished(maxRowNum, rowDataMap, tableIdentifierList)) {
            try {
                boolean allSinkFinished = false;
                Map<String, Object> accumulatorMap = jobClient.getAccumulators().get();
                for (String tableIdentifier : tableIdentifierList) {
                    if (!tableIdentifierIndexMap.containsKey(tableIdentifier)) {
                        tableIdentifierIndexMap.put(tableIdentifier, 0);
                    } else if (tableIdentifierIndexMap.get(tableIdentifier) >= maxRowNum - 1) {
                        allSinkFinished = true;
                        continue;
                    }
                    Object accumulatorObject = accumulatorMap.get(tableIdentifier);
                    if (accumulatorObject instanceof List) {
                        List<?> serializerRowDataList = (List<?>) accumulatorObject;
                        for (int i = tableIdentifierIndexMap.get(tableIdentifier);
                                i < serializerRowDataList.size();
                                i++) {
                            Map<String, Object> rowDataWithTableIdentifier = new HashMap<>();
                            rowDataWithTableIdentifier.put(MOCK_RESULT_TABLE_IDENTIFIER, tableIdentifier);
                            // deserialize data from accumulator
                            Map<String, String> deserializeRowData =
                                    deserializeObjFromBytes((byte[]) serializerRowDataList.get(i));
                            String op = deserializeRowData.get(FlinkConstant.OP);
                            deserializeRowData.remove(FlinkConstant.OP);
                            if (tableIdentifierIndexMap.get(tableIdentifier) == 0) {
                                columns.add(generateResultColumns(tableIdentifier, deserializeRowData));
                            }
                            rowDataWithTableIdentifier.putAll(deserializeRowData);
                            if (RowKind.UPDATE_BEFORE.shortString().equals(op)
                                    || RowKind.DELETE.shortString().equals(op)) {
                                rows.remove(rowDataWithTableIdentifier);
                            } else {
                                rows.add(rowDataWithTableIdentifier);
                            }
                            tableIdentifierIndexMap.put(
                                    tableIdentifier, tableIdentifierIndexMap.get(tableIdentifier) + 1);
                            if (tableIdentifierIndexMap.get(tableIdentifier) >= maxRowNum) {
                                break;
                            }
                        }
                        log.info(
                                "Catch Data: table-{}: size-{},",
                                tableIdentifier,
                                tableIdentifierIndexMap.get(tableIdentifier));
                    }
                }
                if (jobClient.getJobStatus().get().isTerminalState()) {
                    log.info(
                            "JobClient status:{}",
                            jobClient.getJobStatus().get().toString());
                    break;
                }
                if (allSinkFinished && isAutoCancel) {
                    jobClient.cancel();
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Deserialize data from accumulator failed, jobId: {}: \nError: {}", id, e);
                break;
            }
        }
        log.info("Catch data finish. Job id: {}", selectResult.getJobId());
    }

    /**
     * check if all sink has finished
     *
     * @param maxRowNum           maximum row num of each table
     * @param rowData             row data map, key: table name, value: row data
     * @param tableIdentifierList table identifier
     * @return true if all tables has caught enough rows
     */
    private boolean isAllSinkFinished(
            int maxRowNum, Map<String, List<Map<String, String>>> rowData, List<String> tableIdentifierList) {
        if (tableIdentifierList.size() > rowData.size()) {
            return false;
        }
        for (List<Map<String, String>> rowDataList : rowData.values()) {
            if (Asserts.isNotNull(rowDataList) && rowDataList.size() < maxRowNum) {
                return false;
            }
        }
        return true;
    }

    private Map<String, String> deserializeObjFromBytes(byte[] byteArr) throws IOException {
        return mapSerializer.deserialize(new DataInputViewStreamWrapper(new ByteArrayInputStream(byteArr)));
    }

    private String generateResultColumns(String tableIdentifier, Map<String, String> rowData) {
        // __op__ is first column.
        Set<String> columns = new LinkedHashSet<>();
        if (rowData.containsKey(FlinkConstant.OP)) {
            columns.add(FlinkConstant.OP);
        }
        Set<String> columnsFromRowData = rowData.keySet();
        columnsFromRowData.remove(FlinkConstant.OP);
        columns.addAll(columnsFromRowData);
        Map<String, Object> columnElement = new HashMap<>();
        columnElement.put(MOCK_RESULT_TABLE_IDENTIFIER, tableIdentifier);
        columnElement.put(MOCK_RESULT_COLUMN_IDENTIFIER, columns);
        return JsonUtils.toJsonString(columnElement);
    }
}
