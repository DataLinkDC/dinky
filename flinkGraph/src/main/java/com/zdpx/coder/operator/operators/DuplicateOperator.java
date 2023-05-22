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

import com.zdpx.coder.graph.OutputPortObject;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.TableInfo;

/** 用于端口数据复制, 转为多路输出 */
public class DuplicateOperator extends Operator {

    @Override
    protected void initialize() {
        registerInputObjectPort("input_0");
    }

    @Override
    protected void handleParameters(String parameters) {
        if (getOutputPorts().isEmpty() && this.nodeWrapper != null) {
            List<Map<String, Object>> outputInfo = Operator.getParameterLists(parameters);

            for (Map<String, Object> oi : outputInfo) {
                OutputPortObject<TableInfo> opi =
                        registerOutputObjectPort(oi.get("outputName").toString());
                getOutputPorts().put(opi.getName(), opi);
            }
        }
    }

    @Override
    protected Map<String, String> declareUdfFunction() {
        return new HashMap<>();
    }

    @Override
    protected boolean applies() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute() {
        final TableInfo pseudoData =
                getInputPorts().values().stream()
                        .map(t -> (TableInfo) t.getConnection().getFromPort().getPseudoData())
                        .findAny()
                        .orElse(null);
        getOutputPorts()
                .values()
                .forEach(t -> ((OutputPortObject<TableInfo>) t).setPseudoData(pseudoData));
    }
}
