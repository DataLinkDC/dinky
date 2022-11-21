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

package com.dlink.executor.custom;

import com.dlink.assertion.Asserts;

import org.apache.flink.api.dag.Transformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.JSONGenerator;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.delegation.Executor;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.module.ModuleManager;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.command.ResetOperation;
import org.apache.flink.table.operations.command.SetOperation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CustomTableEnvironmentImpl
 *
 * @author wenmo
 * @since 2022/05/08
 **/
public class CustomTableEnvironmentImpl extends AbstractCustomTableEnvironment {

    public CustomTableEnvironmentImpl(StreamTableEnvironment streamTableEnvironment) {
        super(streamTableEnvironment);
    }

    public CustomTableEnvironmentImpl(CatalogManager catalogManager,
                                      ModuleManager moduleManager,
                                      FunctionCatalog functionCatalog,
                                      TableConfig tableConfig,
                                      StreamExecutionEnvironment executionEnvironment,
                                      Planner planner,
                                      Executor executor,
                                      boolean isStreamingMode,
                                      ClassLoader userClassLoader) {
        super(catalogManager, moduleManager,
                functionCatalog,
                tableConfig, executionEnvironment, planner, executor, isStreamingMode, userClassLoader);
    }

    public ObjectNode getStreamGraph(String statement) {
        List<Operation> operations = getParser().parse(statement);
        if (operations.size() != 1) {
            throw new TableException("Unsupported SQL query! explainSql() only accepts a single SQL query.");
        } else {
            List<ModifyOperation> modifyOperations = new ArrayList<>();
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i) instanceof ModifyOperation) {
                    modifyOperations.add((ModifyOperation) operations.get(i));
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
            JSONGenerator jsonGenerator = new JSONGenerator(streamGraph);
            String json = jsonGenerator.getJSON();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode objectNode = mapper.createObjectNode();
            try {
                objectNode = (ObjectNode) mapper.readTree(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } finally {
                return objectNode;
            }
        }
    }

    @Override
    public Planner getPlanner() {
        return ((StreamTableEnvironmentImpl) streamTableEnvironment).getPlanner();
    }

    @Override
    public JobPlanInfo getJobPlanInfo(List<String> statements) {
        return new JobPlanInfo(JsonPlanGenerator.generatePlan(getJobGraphFromInserts(statements)));
    }

    @Override
    public StreamExecutionEnvironment getStreamExecutionEnvironment() {
        return ((StreamTableEnvironmentImpl) streamTableEnvironment).execEnv();
    }

    public JobGraph getJobGraphFromInserts(List<String> statements) {
        return getStreamGraphFromInserts(statements).getJobGraph();
    }

    public boolean parseAndLoadConfiguration(String statement, StreamExecutionEnvironment environment,
                                             Map<String, Object> setMap) {
        List<Operation> operations = getParser().parse(statement);
        for (Operation operation : operations) {
            if (operation instanceof SetOperation) {
                callSet((SetOperation) operation, environment, setMap);
                return true;
            } else if (operation instanceof ResetOperation) {
                callReset((ResetOperation) operation, environment, setMap);
                return true;
            }
        }
        return false;
    }

    private void callSet(SetOperation setOperation, StreamExecutionEnvironment environment,
                         Map<String, Object> setMap) {
        if (setOperation.getKey().isPresent() && setOperation.getValue().isPresent()) {
            String key = setOperation.getKey().get().trim();
            String value = setOperation.getValue().get().trim();
            if (Asserts.isNullString(key) || Asserts.isNullString(value)) {
                return;
            }
            Map<String, String> confMap = new HashMap<>();
            confMap.put(key, value);
            setMap.put(key, value);
            Configuration configuration = Configuration.fromMap(confMap);
            environment.getConfig().configure(configuration, null);
            getConfig().addConfiguration(configuration);
        }
    }

    private void callReset(ResetOperation resetOperation, StreamExecutionEnvironment environment,
                           Map<String, Object> setMap) {
        if (resetOperation.getKey().isPresent()) {
            String key = resetOperation.getKey().get().trim();
            if (Asserts.isNullString(key)) {
                return;
            }
            Map<String, String> confMap = new HashMap<>();
            confMap.put(key, null);
            setMap.remove(key);
            Configuration configuration = Configuration.fromMap(confMap);
            environment.getConfig().configure(configuration, null);
            getConfig().addConfiguration(configuration);
        } else {
            setMap.clear();
        }
    }

    @Override
    public <T> void createTemporaryView(String path, DataStream<T> dataStream, String fields) {
        createTemporaryView(path, fromDataStream(dataStream, fields));
    }

    @Override
    public <T> void createTemporaryView(String path, DataStream<T> dataStream, Expression... fields) {
        createTemporaryView(path, fromDataStream(dataStream, fields));
    }
}
