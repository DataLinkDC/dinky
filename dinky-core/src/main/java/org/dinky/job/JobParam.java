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

package org.dinky.job;

import org.dinky.function.data.model.UDF;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;

/**
 * JobParam
 *
 * @since 2021/11/16
 */
@Builder
@Data
public class JobParam {

    private List<String> statements;
    private List<StatementParam> ddl;
    private List<StatementParam> trans;
    private List<StatementParam> execute;
    private List<UDF> udfList;
    private String parsedSql;

    public void setStatements(List<String> statements) {
        this.statements = statements;
    }

    public void setDdl(List<StatementParam> ddl) {
        this.ddl = ddl;
    }

    public List<String> getTransStatement() {
        return trans.stream().map(StatementParam::getValue).collect(Collectors.toList());
    }

    public void setTrans(List<StatementParam> trans) {
        this.trans = trans;
    }

    public void setExecute(List<StatementParam> execute) {
        this.execute = execute;
    }
}
