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

import com.zdpx.coder.Specifications;
import com.zdpx.coder.graph.InputPortObject;
import com.zdpx.coder.graph.OutputPortObject;
import com.zdpx.coder.operator.Column;
import com.zdpx.coder.operator.FieldFunction;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.OperatorUtil;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.NameHelper;
import com.zdpx.coder.utils.TemplateUtils;

import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
public class JoinOperator extends Operator {
    public static final String TEMPLATE =
            String.format(
                    "<#import \"%s\" as e>CREATE VIEW ${tableName} AS "
                            + "SELECT <@e.fieldsProcess fieldFunctions/> "
                            + "FROM ${inputTableName} "
                            + "${joinType?upper_case} JOIN ${anotherTableName} "
                            + "<#if systemTimeColumn??>FOR SYSTEM_TIME AS OF ${systemTimeColumn}</#if> "
                            + "<#if where??>WHERE ${where}</#if> "
                            + "<#if onLeftColumn??>ON ${onLeftColumn} = ${onRightColumn}</#if>",
                    Specifications.TEMPLATE_FILE);

    private InputPortObject<TableInfo> primaryInput;
    private InputPortObject<TableInfo> secondInput;
    private OutputPortObject<TableInfo> outputPort;

    @Override
    protected void initialize() {
        primaryInput = registerInputObjectPort("primaryInput");
        secondInput = registerInputObjectPort("secondInput");
        outputPort = registerOutputObjectPort("output_0");
    }

    @Override
    protected Map<String, String> declareUdfFunction() {
        return new HashMap<>();
    }

    @Override
    protected boolean applies() {
        return true;
    }

    @Override
    protected void execute() {
        if (getOutputPorts().isEmpty() || this.nodeWrapper == null) {
            log.error("JoinOperator information err.");
            return;
        }

        Map<String, Object> parameters = getFirstParameterMap();
        String joinType = Operator.getNestValue(parameters, "/join/type").textValue();
        String forSystemTime = Operator.getNestValue(parameters, "/systemTimeColumn").textValue();
        String onLeftColumn = Operator.getNestValue(parameters, "/on/leftColumn").textValue();
        String onRightColumn = Operator.getNestValue(parameters, "/on/rightColumn").textValue();

        String outputTableName = NameHelper.generateVariableName("JoinOperator");
        String primaryTableName = primaryInput.getOutputPseudoData().getName();
        String secondTableName = secondInput.getOutputPseudoData().getName();
        List<FieldFunction> ffsPrimary =
                Operator.getFieldFunctions(
                        primaryTableName, Operator.getNestMapValue(parameters, "/primaryInput"));
        List<FieldFunction> ffsSecond =
                Operator.getFieldFunctions(
                        secondTableName, Operator.getNestMapValue(parameters, "/secondInput"));
        ffsPrimary.addAll(ffsSecond);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableName", outputTableName);
        dataModel.put("inputTableName", primaryTableName);
        dataModel.put("anotherTableName", secondTableName);
        dataModel.put(Operator.FIELD_FUNCTIONS, ffsPrimary);
        dataModel.put("joinType", joinType);
        dataModel.put(
                "systemTimeColumn",
                FieldFunction.insertTableName(primaryTableName, null, forSystemTime));
        dataModel.put(
                "onLeftColumn",
                FieldFunction.insertTableName(primaryTableName, null, onLeftColumn));
        dataModel.put(
                "onRightColumn",
                FieldFunction.insertTableName(secondTableName, null, onRightColumn));

        String sqlStr = TemplateUtils.format(this.getName(), dataModel, TEMPLATE);
        registerUdfFunctions(ffsPrimary);

        List<Column> cls = Operator.getColumnFromFieldFunctions(ffsPrimary);
        generate(sqlStr);

        OperatorUtil.postTableOutput(outputPort, outputTableName, cls);
    }
}
