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

import java.util.Map;

import com.zdpx.coder.graph.OutputPortObject;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.TableDataStreamConverter;
import com.zdpx.coder.utils.TemplateUtils;

/** */
public class MysqlSourceOperator extends MysqlTable {

    private OutputPortObject<TableInfo> outputPortObject;

    @Override
    protected void initialize() {
        outputPortObject = new OutputPortObject<>(this, "output_0");
        getOutputPorts().put("output_0", outputPortObject);
    }

    @Override
    protected void execute() {

        String sqlStr = TemplateUtils.format("Source", getDataModel(), TEMPLATE);
        this.getSchemaUtil().getGenerateResult().generate(sqlStr);

        Map<String, Object> parameters = getParameterLists().get(0);
        final TableInfo ti = TableDataStreamConverter.getTableInfo(parameters);
        ti.setName(generateTableName(ti.getName()));
        outputPortObject.setPseudoData(ti);
    }

}
