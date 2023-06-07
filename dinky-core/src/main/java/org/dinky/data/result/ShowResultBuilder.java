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

import org.dinky.utils.FlinkUtil;

import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ShowResultBuilder
 *
 * @since 2021/7/1 23:57
 */
public class ShowResultBuilder implements ResultBuilder {

    private String nullColumn = "";

    public ShowResultBuilder() {}

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
}
