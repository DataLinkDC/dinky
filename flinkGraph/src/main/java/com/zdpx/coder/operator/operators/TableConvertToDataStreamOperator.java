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
import java.util.Map;

import com.zdpx.coder.Specifications;
import com.zdpx.coder.code.CodeJavaBuilder;
import com.zdpx.coder.graph.OutputPortObject;
import com.zdpx.coder.graph.PseudoData;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.Parameter;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.NameHelper;

/** */
public class TableConvertToDataStreamOperator extends Operator {

    private OutputPortObject<TableInfo> outputPortObject;

    @Override
    protected void initialize() {
        parameters.getParameterList().add(new Parameter(Specifications.TABLE_NAME));
        outputPortObject = registerOutputObjectPort("output_0)");
        registerInputObjectPort("input_0");
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
        String tn =
                NameHelper.generateVariableName(
                        parameters.getParameterByName(Specifications.TABLE_NAME));
        if (!(this.getSchemaUtil().getGenerateResult() instanceof CodeJavaBuilder)) {
            return;
        }

        CodeJavaBuilder gjr = (CodeJavaBuilder) this.getSchemaUtil().getGenerateResult();
        gjr.getCodeContext()
                .getMain()
                .addStatement(
                        "DataStream<Row> $2L = $1L.toDataStream($1L.sqlQuery(\"select * from $2L\"))",
                        Specifications.TABLE_ENV,
                        tn)
                .addCode(System.lineSeparator());

        PseudoData pseudoData =
                getInputPorts().values().stream()
                        .map(t -> t.getConnection().getFromPort().getPseudoData())
                        .findAny()
                        .orElse(null);

        outputPortObject.setPseudoData((TableInfo) pseudoData);
    }
}
