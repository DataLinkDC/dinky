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

package com.dlink.executor;

import com.dlink.model.LineageRel;
import com.dlink.result.SqlExplainResult;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.operations.ExplainOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CustomTableEnvironment
 *
 * @author wenmo
 * @since 2022/2/5 10:35
 */
public interface CustomTableEnvironment extends DefaultStreamTableEnvironment {

    ObjectNode getStreamGraph(String statement);

    JobPlanInfo getJobPlanInfo(List<String> statements);

    default StreamGraph getStreamGraphFromInserts(List<String> statements) {
        List<ModifyOperation> modifyOperations = new ArrayList();
        for (String statement : statements) {
            List<Operation> operations = getParser().parse(statement);
            if (operations.size() != 1) {
                throw new TableException("Only single statement is supported.");
            } else {
                Operation operation = operations.get(0);
                if (operation instanceof ModifyOperation) {
                    modifyOperations.add((ModifyOperation) operation);
                } else {
                    throw new TableException("Only insert statement is supported now.");
                }
            }
        }
        List<Transformation<?>> trans = getPlanner().translate(modifyOperations);
        for (Transformation<?> transformation : trans) {
            getStreamExecutionEnvironment().addOperator(transformation);
        }
        StreamGraph streamGraph = getStreamExecutionEnvironment().getStreamGraph();
        if (getConfig().getConfiguration().containsKey(PipelineOptions.NAME.key())) {
            streamGraph.setJobName(getConfig().getConfiguration().getString(PipelineOptions.NAME));
        }
        return streamGraph;
    }

    ;

    JobGraph getJobGraphFromInserts(List<String> statements);

    default SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        SqlExplainResult record = new SqlExplainResult();
        List<Operation> operations = getParser().parse(statement);
        record.setParseTrue(true);
        if (operations.size() != 1) {
            throw new TableException("Unsupported SQL query! explainSql() only accepts a single SQL query.");
        }
        Operation operation = operations.get(0);
        if (operation instanceof ModifyOperation) {
            record.setType("Modify DML");
        } else if (operation instanceof ExplainOperation) {
            record.setType("Explain DML");
        } else if (operation instanceof QueryOperation) {
            record.setType("Query DML");
        } else {
            record.setExplain(operation.asSummaryString());
            record.setType("DDL");
        }
        record.setExplainTrue(true);
        if ("DDL".equals(record.getType())) {
            //record.setExplain("DDL语句不进行解释。");
            return record;
        }
        record.setExplain(getPlanner().explain(operations, extraDetails));
        return record;
    }

    boolean parseAndLoadConfiguration(String statement, StreamExecutionEnvironment config, Map<String, Object> setMap);

    StreamExecutionEnvironment getStreamExecutionEnvironment();

    Planner getPlanner();

    default List<LineageRel> getLineage(String statement) {
        return null;
    }
}
