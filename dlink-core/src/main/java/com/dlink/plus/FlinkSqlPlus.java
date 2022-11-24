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

package com.dlink.plus;

import com.dlink.executor.Executor;
import com.dlink.explainer.Explainer;
import com.dlink.explainer.ca.ColumnCAResult;
import com.dlink.explainer.ca.TableCAResult;
import com.dlink.model.LineageRel;
import com.dlink.result.SqlExplainResult;

import org.apache.flink.runtime.rest.messages.JobPlanInfo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * FlinkSqlPlus
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class FlinkSqlPlus {

    private Executor executor;
    private Explainer explainer;
    private boolean statementSet;

    public FlinkSqlPlus(Executor executor) {
        this.executor = executor;
        this.explainer = new Explainer(executor);
    }

    public FlinkSqlPlus(Executor executor, boolean statementSet) {
        this.executor = executor;
        this.explainer = new Explainer(executor, statementSet);
    }

    public static FlinkSqlPlus build() {
        return build(true);
    }

    public static FlinkSqlPlus build(boolean statementSet) {
        return new FlinkSqlPlus(Executor.build(), statementSet);
    }

    public List<SqlResult> executeSql(String sql) {
        if (sql == null || "".equals(sql)) {
            return new ArrayList<>();
        }
        String[] sqls = sql.split(";");
        List<SqlResult> sqlResults = new ArrayList<>();
        try {
            for (int i = 0; i < sqls.length; i++) {
                sqlResults.add(new SqlResult(executor.executeSql(sqls[i])));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sqlResults.add(new SqlResult(false, e.getMessage()));
            return sqlResults;
        }
        return sqlResults;
    }

    public SqlResult execute(String sql) {
        if (sql == null || "".equals(sql)) {
            return SqlResult.NULL;
        }
        try {
            return new SqlResult(executor.executeSql(sql));
        } catch (Exception e) {
            return new SqlResult(false, e.getMessage());
        }
    }

    public List<SqlExplainResult> explainSqlRecord(String statement) {
        return explainer.explainSqlResult(statement);
    }

    public List<TableCAResult> explainSqlTableColumnCA(String statement) {
        return explainer.explainSqlTableColumnCA(statement);
    }

    public List<TableCAResult> generateTableCA(String statement) {
        return explainer.generateTableCA(statement);
    }

    @Deprecated
    public List<ColumnCAResult> explainSqlColumnCA(String statement) {
        return explainer.explainSqlColumnCA(statement);
    }

    public ObjectNode getStreamGraph(String statement) {
        return executor.getStreamGraph(statement);
    }

    public JobPlanInfo getJobPlanInfo(String statement) {
        return explainer.getJobPlanInfo(statement);
    }

    public List<LineageRel> getLineage(String statement) {
        return explainer.getLineage(statement);
    }
}
