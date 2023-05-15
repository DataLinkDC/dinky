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

    private InputPortObject<TableInfo> inputPortObject;

    @Override
    protected void initialize() {
        inputPortObject = new InputPortObject<>(this, "input_0");
        getInputPorts().put("input_0", inputPortObject);
    }

    @Override
    protected void execute() {
        Map<String, Object> dataModel = getDataModel();
        String sqlStr = TemplateUtils.format("sink", dataModel, MysqlTable.TEMPLATE);

        this.getSchemaUtil().getGenerateResult().generate(sqlStr);

        String sql =
                String.format(
                        "INSERT INTO ${tableName} (<#list tableInfo.columns as column>${column.name}<#sep>,</#sep></#list>) SELECT <#list tableInfo.columns as column>${column.name}<#sep>, </#list> FROM ${tableInfo.name}");

        TableInfo pseudoData = inputPortObject.getOutputPseudoData();
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

    @Override
    protected String propertySchemaDefinition() {
        return "{\n" +
                "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"array\",\n" +
                "      \"items\": {\n" +
                "        \"type\": \"object\",\n" +
                "        \"properties\": {\n" +
                "          \"tableName\": {\n" +
                "            \"type\": \"string\"\n" +
                "          },\n" +
                "          \"connector\": {\n" +
                "            \"type\": \"string\"\n" +
                "          },\n" +
                "          \"columns\": {\n" +
                "            \"type\": \"array\",\n" +
                "            \"items\": {\n" +
                "              \"type\": \"object\",\n" +
                "              \"properties\": {\n" +
                "                \"name\": {\n" +
                "                  \"type\": \"string\"\n" +
                "                },\n" +
                "                \"type\": {\n" +
                "                  \"type\": \"string\"\n" +
                "                }\n" +
                "              },\n" +
                "              \"required\": [\n" +
                "                \"name\",\n" +
                "                \"type\"\n" +
                "              ]\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"required\": [\n" +
                "          \"tableName\",\n" +
                "          \"connector\",\n" +
                "          \"columns\"\n" +
                "        ]\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"required\": [\n" +
                "    \"parameters\"\n" +
                "  ]\n" +
                "}";
    }
}
