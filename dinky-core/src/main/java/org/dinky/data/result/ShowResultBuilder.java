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

import org.dinky.job.JobHandler;
import org.dinky.utils.FlinkUtil;

import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * ShowResultBuilder
 *
 * @since 2021/7/1 23:57
 */
public class ShowResultBuilder extends AbstractResultBuilder implements ResultBuilder {

    private String nullColumn = "";

    public ShowResultBuilder(String id) {
        this.id = id;
    }

    @Override
    public IResult getResult(TableResult tableResult) {
        List<String> columns = FlinkUtil.catchColumn(tableResult);
        Set<String> column = new LinkedHashSet(columns);
        List<Map<String, Object>> rows = new ArrayList<>();
        Iterator<Row> it = tableResult.collect();
        while (it.hasNext()) {
            Map<String, Object> map = new LinkedHashMap<>();
            Row row = it.next();
            for (int i = 0; i < row.getArity(); ++i) {
                Object field = row.getField(i);
                if (field == null) {
                    map.put(columns.get(i), nullColumn);
                } else {
                    map.put(columns.get(i), field.toString());
                }
            }
            rows.add(map);
        }
        return new org.dinky.data.result.DDLResult(rows, rows.size(), column);
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
        DDLResult ddlResult = (DDLResult) getResult(tableResult);
        // ddl show: DDLResult -> SelectResult
        SelectResult selectResult =
                new SelectResult(id, ddlResult.getRowData(), Sets.newLinkedHashSet(ddlResult.getColumns()));
        selectResult.setDestroyed(Boolean.TRUE);
        try {
            ResultPool.put(selectResult);
            jobHandler.persistResultData(Lists.newArrayList(this.id));
        } finally {
            ResultPool.remove(id);
        }
        return selectResult;
    }
}
