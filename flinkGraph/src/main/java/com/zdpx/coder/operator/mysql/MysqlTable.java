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

package com.zdpx.coder.operator.mysql;

import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.TableDataStreamConverter;

/** */
public abstract class MysqlTable extends Operator {
    public static final String TEMPLATE =
            "CREATE TABLE ${tableName} ("
                    + "<#list columns as column>${column.name} ${column.type}<#sep>, "
                    + "</#list>) "
                    + "WITH ("
                    + "<#list parameters as key, value>"
                    + "'${(key == \"tableName\")?then(\"table-name\", key)}' = '${value}'<#sep>, "
                    + "</#list>)";
    protected TableInfo tableInfo;

    protected Map<String, Object> getDataModel() {
        final String columns = "columns";
        String parameters = "parameters";

        Map<String, Object> psFirst = getParameterLists().get(0);

        Map<String, Object> result = new HashMap<>();
        result.put(parameters, new HashMap<String, Object>());
        result.put("tableName", generateTableName((String) psFirst.get("tableName")));
        for (Map.Entry<String, Object> m : psFirst.entrySet()) {
            if (m.getKey().equals(columns)) {
                result.put(columns, m.getValue());
                continue;
            }

            @SuppressWarnings("unchecked")
            HashMap<String, Object> ps = (HashMap<String, Object>) result.get(parameters);
            ps.put(m.getKey(), m.getValue());
        }
        return result;
    }

    protected String generateTableName(String tableName) {
        return tableName + "_" + this.getId().substring(this.getId().lastIndexOf('-') + 1);
    }

    @Override
    protected void handleParameters(String parameters) {
        List<Map<String, Object>> pl = getParameterLists(parameters);
        if (CollectionUtils.isEmpty(pl)) {
            return;
        }
        tableInfo = TableDataStreamConverter.getTableInfo(getParameterLists(parameters).get(0));
    }

    @Override
    protected Map<String, String> declareUdfFunction() {
        return new HashMap<>();
    }

    @Override
    protected boolean applies() {
        return true;
    }
}
