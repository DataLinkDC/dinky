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
import org.dinky.utils.JsonUtils;

import org.apache.flink.api.common.typeutils.base.MapSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.ResolvedSchema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import alluxio.shaded.client.com.google.common.collect.Lists;
import cn.hutool.core.collection.ListUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockResultBuilder extends AbstractResultBuilder implements ResultBuilder {
    private final Integer maxRowNum;
    private final boolean isAutoCancel;
    private final String MOCK_RESULT_TABLE_IDENTIFIER = "dinkySinkResultTableIdentifier";
    private final String MOCK_RESULT_COLUMN_IDENTIFIER = "dinkySinkResultColumnIdentifier";

    public MockResultBuilder(String id, Integer maxRowNum, boolean isAutoCancel) {
        this.id = id;
        this.maxRowNum = maxRowNum;
        this.isAutoCancel = isAutoCancel;
    }

    @Override
    public IResult getResult(TableResult tableResult) {
        Optional<JobClient> Optional = tableResult.getJobClient();
        if (Optional.isPresent()) {
            JobClient jobClient = Optional.get();
            // get table identifiers
            ResolvedSchema resolvedSchema = tableResult.getResolvedSchema();
            List<String> tableIdentifierList = resolvedSchema.getColumnNames();
            // update row data map
            Map<String, List<Map<String, String>>> rowDataMap = new HashMap<>();
            if (tableResult.getJobClient().isPresent()) {
                while (!isAllSinkFinished(maxRowNum, rowDataMap, tableIdentifierList)) {
                    try {
                        Map<String, Object> accumulatorMap =
                                jobClient.getAccumulators().get();
                        for (String tableIdentifier : tableIdentifierList) {
                            Object accumulatorObject = accumulatorMap.get(tableIdentifier);
                            if (accumulatorObject instanceof List) {
                                List<?> list = (List<?>) accumulatorObject;
                                for (Object obj : list) {
                                    // deserialize data from accumulator
                                    Map<String, String> deserialize = deserializeObjFromBytes((byte[]) obj);
                                    // update row data map
                                    List<Map<String, String>> rowDataList =
                                            rowDataMap.getOrDefault(tableIdentifier, new ArrayList<>());
                                    rowDataList.add(deserialize);
                                    rowDataMap.put(tableIdentifier, ListUtil.sub(rowDataList, 0, maxRowNum));
                                }
                            }
                        }
                    } catch (Exception e) {
                        // do nothing
                    }
                }
            }
            if (isAutoCancel) {
                try {
                    jobClient.cancel();
                } catch (Exception e) {
                    log.error("Cancel job failed, jobId: {}", id);
                }
            }
            return new MockSinkResult(id, rowDataMap);
        } else {
            return MockSinkResult.buildFailed();
        }
    }

    @Override
    public IResult getResultWithPersistence(TableResult tableResult, JobHandler jobHandler) {
        if (Objects.isNull(tableResult)) {
            return MockSinkResult.buildFailed();
        }
        MockSinkResult mockSinkResult = (MockSinkResult) getResult(tableResult);
        // MockSinkResult -> SelectResult
        SelectResult selectResult = new SelectResult(
                id,
                convertSinkRowData2SelectRowData(mockSinkResult.getTableRowData()),
                generateMockResultColumns(mockSinkResult.getTableRowData()));
        selectResult.setMockSinkResult(true);
        selectResult.setDestroyed(Boolean.TRUE);
        try {
            ResultPool.put(selectResult);
            jobHandler.persistResultData(Lists.newArrayList(this.id));
        } finally {
            ResultPool.remove(id);
        }
        return selectResult;
    }

    /**
     * convert row data of mocked sink result to the type of select result
     *
     * @param tableRowData row data of {@link MockSinkResult}
     * @return row data of {@link SelectResult}
     */
    private List<Map<String, Object>> convertSinkRowData2SelectRowData(
            Map<String, List<Map<String, String>>> tableRowData) {
        List<Map<String, Object>> resultRowData = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, String>>> entry : tableRowData.entrySet()) {
            String tableIdentifier = entry.getKey();
            List<Map<String, String>> rowDataList = entry.getValue();
            for (Map<String, String> rowDataElement : rowDataList) {
                Map<String, Object> selectRowDataElement = new HashMap<>();
                // table name identifier
                selectRowDataElement.put(MOCK_RESULT_TABLE_IDENTIFIER, tableIdentifier);
                // row data
                selectRowDataElement.putAll(rowDataElement);
                resultRowData.add(selectRowDataElement);
            }
        }
        return resultRowData;
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

    /**
     * generate mock result column sets
     *
     * @param tableRowData row data of (@Link MockSinkResult}
     * @return column sets {@link SelectResult}
     */
    private LinkedHashSet<String> generateMockResultColumns(Map<String, List<Map<String, String>>> tableRowData) {
        LinkedHashSet<String> resultColumn = new LinkedHashSet<>();
        for (Map.Entry<String, List<Map<String, String>>> entry : tableRowData.entrySet()) {
            String tableIdentifier = entry.getKey();
            List<Map<String, String>> rowDataList = entry.getValue();
            Set<String> columns = rowDataList.get(0).keySet();
            Map<String, Object> columnElement = new HashMap<>();
            columnElement.put(MOCK_RESULT_TABLE_IDENTIFIER, tableIdentifier);
            columnElement.put(MOCK_RESULT_COLUMN_IDENTIFIER, columns);
            resultColumn.add(JsonUtils.toJsonString(columnElement));
        }
        return resultColumn;
    }

    private static Map<String, String> deserializeObjFromBytes(byte[] byteArr) throws IOException {
        MapSerializer<String, String> mapSerializer =
                new MapSerializer<>(new StringSerializer(), new StringSerializer());
        return mapSerializer.deserialize(new DataInputViewStreamWrapper(new ByteArrayInputStream(byteArr)));
    }
}
