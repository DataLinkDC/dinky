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

package org.dinky.executor;

import org.dinky.assertion.Asserts;
import org.dinky.context.DinkyClassLoaderContextHolder;
import org.dinky.data.model.LineageRel;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.utils.FlinkStreamProgramWithoutPhysical;
import org.dinky.utils.LineageContext;

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
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.operations.ExplainOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;
import org.apache.flink.table.operations.command.ResetOperation;
import org.apache.flink.table.operations.command.SetOperation;
import org.apache.flink.table.planner.plan.optimize.program.FlinkChainedProgram;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * CustomTableEnvironmentImpl
 *
 * @since 2022/05/08
 */
public class CustomTableEnvironmentImpl extends AbstractCustomTableEnvironment {

    private static final Logger log = LoggerFactory.getLogger(CustomTableEnvironmentImpl.class);

    private final FlinkChainedProgram flinkChainedProgram;
    private static final ObjectMapper mapper = new ObjectMapper();

    public CustomTableEnvironmentImpl(StreamTableEnvironment streamTableEnvironment) {
        super(streamTableEnvironment);
        this.flinkChainedProgram =
                FlinkStreamProgramWithoutPhysical.buildProgram(
                        (Configuration) getStreamExecutionEnvironment().getConfiguration());
    }

    public static CustomTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment) {
        return create(
                executionEnvironment,
                EnvironmentSettings.newInstance()
                        .withClassLoader(DinkyClassLoaderContextHolder.get())
                        .build());
    }

    public static CustomTableEnvironmentImpl createBatch(
            StreamExecutionEnvironment executionEnvironment) {
        return create(
                executionEnvironment, EnvironmentSettings.newInstance().inBatchMode().build());
    }

    public static CustomTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment, EnvironmentSettings settings) {
        StreamTableEnvironment streamTableEnvironment =
                StreamTableEnvironment.create(executionEnvironment, settings);

        return new CustomTableEnvironmentImpl(streamTableEnvironment);
    }

    public ObjectNode getStreamGraph(String statement) {
        List<Operation> operations = super.getParser().parse(statement);
        if (operations.size() != 1) {
            throw new TableException(
                    "Unsupported SQL query! explainSql() only accepts a single SQL query.");
        }

        List<ModifyOperation> modifyOperations =
                operations.stream()
                        .filter(operation -> operation instanceof ModifyOperation)
                        .map(operation -> (ModifyOperation) operation)
                        .collect(Collectors.toList());

        StreamGraph streamGraph = transOperatoinsToStreamGraph(modifyOperations);
        JSONGenerator jsonGenerator = new JSONGenerator(streamGraph);
        try {
            return (ObjectNode) mapper.readTree(jsonGenerator.getJSON());
        } catch (JsonProcessingException e) {
            log.error("read streamGraph configure error: ", e);
            return mapper.createObjectNode();
        }
    }

    private StreamGraph transOperatoinsToStreamGraph(List<ModifyOperation> modifyOperations) {
        List<Transformation<?>> trans = getPlanner().translate(modifyOperations);
        final StreamExecutionEnvironment environment = getStreamExecutionEnvironment();
        trans.forEach(environment::addOperator);

        StreamGraph streamGraph = environment.getStreamGraph();
        final Configuration configuration = getConfig().getConfiguration();
        if (configuration.containsKey(PipelineOptions.NAME.key())) {
            streamGraph.setJobName(configuration.getString(PipelineOptions.NAME));
        }
        return streamGraph;
    }

    @Override
    public JobPlanInfo getJobPlanInfo(List<String> statements) {
        return new JobPlanInfo(JsonPlanGenerator.generatePlan(getJobGraphFromInserts(statements)));
    }

    public StreamGraph getStreamGraphFromInserts(List<String> statements) {
        List<ModifyOperation> modifyOperations = new ArrayList<>();
        statements.stream()
                .map(statement -> getParser().parse(statement))
                .forEach(
                        operations -> {
                            if (operations.size() != 1) {
                                throw new TableException("Only single statement is supported.");
                            }
                            Operation operation = operations.get(0);
                            if (operation instanceof ModifyOperation) {
                                modifyOperations.add((ModifyOperation) operation);
                            } else {
                                throw new TableException("Only insert statement is supported now.");
                            }
                        });

        return transOperatoinsToStreamGraph(modifyOperations);
    }

    public JobGraph getJobGraphFromInserts(List<String> statements) {
        return getStreamGraphFromInserts(statements).getJobGraph();
    }

    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        List<Operation> operations = getParser().parse(statement);
        if (operations.size() != 1) {
            throw new TableException(
                    "Unsupported SQL query! explainSql() only accepts a single SQL query.");
        }

        Operation operation = operations.get(0);
        SqlExplainResult record = new SqlExplainResult();
        record.setParseTrue(true);
        record.setExplainTrue(true);

        if (operation instanceof ModifyOperation) {
            record.setType("Modify DML");
        } else if (operation instanceof ExplainOperation) {
            record.setType("Explain DML");
        } else if (operation instanceof QueryOperation) {
            record.setType("Query DML");
        } else {
            record.setExplain(operation.asSummaryString());
            record.setType("DDL");

            // record.setExplain("DDL statement needn't comment。");
            return record;
        }

        record.setExplain(getPlanner().explain(operations, extraDetails));
        return record;
    }

    public boolean parseAndLoadConfiguration(
            String statement, StreamExecutionEnvironment environment, Map<String, Object> setMap) {
        for (Operation operation : getParser().parse(statement)) {
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

    private void callSet(
            SetOperation setOperation,
            StreamExecutionEnvironment environment,
            Map<String, Object> setMap) {
        if (!setOperation.getKey().isPresent() || !setOperation.getValue().isPresent()) {
            return;
        }

        String key = setOperation.getKey().get().trim();
        String value = setOperation.getValue().get().trim();
        if (Asserts.isNullString(key) || Asserts.isNullString(value)) {
            return;
        }
        setMap.put(key, value);

        setConfiguration(environment, Collections.singletonMap(key, value));
    }

    private void callReset(
            ResetOperation resetOperation,
            StreamExecutionEnvironment environment,
            Map<String, Object> setMap) {
        final Optional<String> keyOptional = resetOperation.getKey();
        if (!keyOptional.isPresent()) {
            setMap.clear();
            return;
        }

        String key = keyOptional.get().trim();
        if (Asserts.isNullString(key)) {
            return;
        }

        setMap.remove(key);
        setConfiguration(environment, Collections.singletonMap(key, null));
    }

    private void setConfiguration(
            StreamExecutionEnvironment environment, Map<String, String> config) {
        Configuration configuration = Configuration.fromMap(config);
        environment.getConfig().configure(configuration, null);
        environment.getCheckpointConfig().configure(configuration);
        getConfig().addConfiguration(configuration);
    }

    @Override
    public List<LineageRel> getLineage(String statement) {
        LineageContext lineageContext =
                new LineageContext(
                        flinkChainedProgram, (TableEnvironmentImpl) streamTableEnvironment);
        return lineageContext.getLineage(statement);
    }

    @Override
    public <T> void createTemporaryView(
            String s, DataStream<Row> dataStream, List<String> columnNameList) {
        createTemporaryView(s, fromChangelogStream(dataStream));
    }
}
