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

package com.zdpx.coder.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zdpx.coder.operator.Column;
import com.zdpx.coder.operator.TableInfo;

/** */
public class TableDataStreamConverter {
    private TableDataStreamConverter() {}

    public static TableInfo getTableInfo(Map<String, Object> dataModel) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> columns = (List<Map<String, String>>) dataModel.get("columns");
        List<Column> cs = new ArrayList<>();
        for (Map<String, String> dm : columns) {
            cs.add(new Column(dm.get("name"), dm.get("type")));
        }

        return TableInfo.newBuilder().name((String) dataModel.get("tableName")).columns(cs).build();
    }
}
