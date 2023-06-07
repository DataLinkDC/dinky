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

import java.util.HashMap;
import java.util.Map;

import com.zdpx.coder.graph.InputPortObject;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.TemplateUtils;

import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
public class MysqlSinkOperator extends MysqlTable {

    public static final String INPUT_0 = "input_0";

    @Override
    protected void initialize() {
        getInputPorts().put(INPUT_0, new InputPortObject<>(this, INPUT_0));
    }

    @Override
    protected void execute() {
        Map<String, Object> dataModel = getDataModel();
        String sqlStr = TemplateUtils.format("sink", dataModel, MysqlTable.TEMPLATE);

        this.getSchemaUtil().getGenerateResult().generate(sqlStr);

        String sql =
                "INSERT INTO ${tableName} (<#list tableInfo.columns as column>${column.name}<#sep>,</#sep></#list>) "
                        + "SELECT <#list tableInfo.columns as column>${column.name}<#sep>, </#list> "
                        + "FROM ${tableInfo.name}";

        @SuppressWarnings("unchecked")
        TableInfo pseudoData =
                ((InputPortObject<TableInfo>) getInputPorts().get(INPUT_0)).getOutputPseudoData();
        if (pseudoData == null) {
            log.warn("{} input table info empty error.", getName());
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("tableName", dataModel.get("tableName"));
        data.put("tableInfo", pseudoData);
        String insertSqlStr = TemplateUtils.format("insert", data, sql);
        this.getSchemaUtil().getGenerateResult().generate(insertSqlStr);
    }
}
