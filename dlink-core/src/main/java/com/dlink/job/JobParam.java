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

package com.dlink.job;

import com.dlink.function.data.model.UDF;

import java.util.ArrayList;
import java.util.List;

/**
 * JobParam
 *
 * @author wenmo
 * @since 2021/11/16
 */
public class JobParam {

    private List<String> statements;
    private List<StatementParam> ddl;
    private List<StatementParam> trans;
    private List<StatementParam> execute;
    private List<UDF> udfList;

    public JobParam(List<StatementParam> ddl, List<StatementParam> trans) {
        this.ddl = ddl;
        this.trans = trans;
    }

    public JobParam(List<String> statements, List<StatementParam> ddl, List<StatementParam> trans,
            List<StatementParam> execute, List<UDF> udfList) {
        this.statements = statements;
        this.ddl = ddl;
        this.trans = trans;
        this.execute = execute;
        this.udfList = udfList;
    }

    public List<String> getStatements() {
        return statements;
    }

    public void setStatements(List<String> statements) {
        this.statements = statements;
    }

    public List<StatementParam> getDdl() {
        return ddl;
    }

    public void setDdl(List<StatementParam> ddl) {
        this.ddl = ddl;
    }

    public List<StatementParam> getTrans() {
        return trans;
    }

    public List<String> getTransStatement() {
        List<String> statementList = new ArrayList<>();
        for (StatementParam statementParam : trans) {
            statementList.add(statementParam.getValue());
        }
        return statementList;
    }

    public void setTrans(List<StatementParam> trans) {
        this.trans = trans;
    }

    public List<StatementParam> getExecute() {
        return execute;
    }

    public void setExecute(List<StatementParam> execute) {
        this.execute = execute;
    }

    public List<UDF> getUdfList() {
        return udfList;
    }
}
