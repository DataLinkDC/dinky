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

package com.zdpx.coder.operator.operators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zdpx.coder.Specifications;
import com.zdpx.coder.graph.InputPortObject;
import com.zdpx.coder.graph.OutputPortObject;
import com.zdpx.coder.graph.Scene;
import com.zdpx.coder.operator.FieldFunction;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.OperatorUtil;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.NameHelper;
import com.zdpx.coder.utils.TemplateUtils;

/**
 * 单表通用函数处理类, 根据配置定义的对每个字段的函数处理方式, 生成相应的sql语句
 *
 * @author Licho Sun
 */
public class CommSelectFunctionOperator extends Operator {
    public static final String TEMPLATE =
            String.format(
                    "<#import \"%s\" as e>CREATE VIEW ${tableName} AS SELECT <@e.fieldsProcess fieldFunctions/> FROM ${inputTableName} <#if where??>WHERE ${where}</#if>",
                    Specifications.TEMPLATE_FILE);

    /** 函数字段名常量 */
    public static final String WHERE = "where";

    private InputPortObject<TableInfo> inputPortObject;
    private OutputPortObject<TableInfo> outputPortObject;

    @Override
    protected void initialize() {
        inputPortObject = registerInputObjectPort("input_0");
        outputPortObject = registerOutputObjectPort("output_0");
    }

    @Override
    protected Map<String, String> declareUdfFunction() {
        Map<String, Object> parameters = getParameterLists().get(0);
        List<FieldFunction> ffs = Operator.getFieldFunctions(null, parameters);
        List<String> functions =
                ffs.stream().map(FieldFunction::getFunctionName).collect(Collectors.toList());
        return Scene.getUserDefinedFunctionMaps().entrySet().stream()
                .filter(k -> functions.contains(k.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    protected boolean applies() {
        return true;
    }

    @Override
    protected void execute() {
        Map<String, Object> parameters = getFirstParameterMap();
        String outputTableName = NameHelper.generateVariableName("CommSelectFunctionResult");

        final String tableName = inputPortObject.getOutputPseudoData().getName();
        List<FieldFunction> ffs = Operator.getFieldFunctions(tableName, parameters);
        Map<String, Object> p = new HashMap<>();
        p.put("tableName", outputTableName);
        p.put(Operator.FIELD_FUNCTIONS, ffs);
        p.put("inputTableName", tableName);
        p.put(WHERE, parameters.get(WHERE));
        String sqlStr = TemplateUtils.format("CommSelectFunction", p, TEMPLATE);

        this.getSchemaUtil().getGenerateResult().generate(sqlStr);

        OperatorUtil.postTableOutput(
                outputPortObject,
                outputTableName,
                Specifications.convertFieldFunctionToColumns(ffs));
    }
}
