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

package com.zdpx.coder.operator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zdpx.coder.graph.InputPortObject;
import com.zdpx.coder.graph.OutputPortObject;
import com.zdpx.coder.graph.PseudoData;

/** 用于端口数据复制, 转为多路输出 */
public class DuplicateOperator extends Operator {

    @Override
    protected void initialize() {
        final InputPortObject<TableInfo> inputPortInfo = new InputPortObject<>(this, "input_0");
        inputPorts.put("input_0", inputPortInfo);
    }

    @Override
    protected void handleParameters(String parameters) {
        if (outputPorts.isEmpty() && this.nodeWrapper != null) {
            List<Map<String, Object>> outputInfo = Operator.getParameterLists(parameters);

            for (Map<String, Object> oi : outputInfo) {
                OutputPortObject<TableInfo> opi =
                        new OutputPortObject<>(this, oi.get("outputName").toString());
                outputPorts.put(opi.getName(),opi);
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

    @Override
    protected void execute() {

        PseudoData pseudoData =
                inputPorts.values().stream()
                        .map(t -> t.getConnection().getFromPort().getPseudoData())
                        .findAny()
                        .orElse(null);
        outputPorts.values().forEach(t -> t.setPseudoData(pseudoData));
    }
}
