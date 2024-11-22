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

import org.dinky.data.exception.DinkyException;
import org.dinky.data.job.JobStatement;
import org.dinky.data.job.SqlType;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.parser.CustomParserImpl;
import org.dinky.utils.SqlUtil;

import org.apache.calcite.sql.SqlNode;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.JSONGenerator;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.ExplainFormat;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.operations.CollectModifyOperation;
import org.apache.flink.table.operations.CreateTableASOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.text.StrFormatter;

/**
 * CustomTableEnvironmentImpl
 *
 * @since 2022/05/08
 */
public class CustomTableEnvironmentImpl extends AbstractCustomTableEnvironment {

    private static final Logger log = LoggerFactory.getLogger(CustomTableEnvironmentImpl.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public CustomTableEnvironmentImpl(StreamTableEnvironment streamTableEnvironment) {
        super(streamTableEnvironment);
        injectParser(new CustomParserImpl(getPlanner().getParser()));
        injectExtendedExecutor(new CustomExtendedOperationExecutorImpl(this));
    }

    public static CustomTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment, ClassLoader classLoader) {
        return create(
                executionEnvironment,
                EnvironmentSettings.newInstance().withClassLoader(classLoader).build());
    }

    public static CustomTableEnvironmentImpl createBatch(
            StreamExecutionEnvironment executionEnvironment, ClassLoader classLoader) {
        return create(
                executionEnvironment,
                EnvironmentSettings.newInstance()
                        .withClassLoader(classLoader)
                        .inBatchMode()
                        .build());
    }

    public static CustomTableEnvironmentImpl create(
            StreamExecutionEnvironment executionEnvironment, EnvironmentSettings settings) {
        StreamTableEnvironment streamTableEnvironment = StreamTableEnvironment.create(executionEnvironment, settings);

        return new CustomTableEnvironmentImpl(streamTableEnvironment);
    }

    @Override
    public ObjectNode getStreamGraph(String statement) {
        List<Operation> operations = super.getParser().parse(statement);
        if (operations.size() != 1) {
            throw new TableException("Unsupported SQL query! explainSql() only accepts a single SQL query.");
        }

        List<ModifyOperation> modifyOperations = operations.stream()
                .filter(ModifyOperation.class::isInstance)
                .map(ModifyOperation.class::cast)
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
    public JobPlanInfo getJobPlanInfo(List<JobStatement> statements) {
        return new JobPlanInfo(JsonPlanGenerator.generatePlan(getJobGraphFromInserts(statements)));
    }

    @Override
    public StreamGraph getStreamGraphFromInserts(List<JobStatement> statements) {
        statements.removeIf(statement -> statement.getSqlType().equals(SqlType.RTAS));
        List<ModifyOperation> modifyOperations = new ArrayList<>();
        statements.stream()
                .map(statement -> getParser().parse(statement.getStatement()))
                .forEach(operations -> {
                    if (operations.size() != 1) {
                        throw new TableException("Only single statement is supported.");
                    }
                    Operation operation = operations.get(0);
                    if (operation instanceof ModifyOperation) {
                        if (operation instanceof CreateTableASOperation) {
                            CreateTableASOperation createTableASOperation = (CreateTableASOperation) operation;
                            executeInternal(createTableASOperation.getCreateTableOperation());
                            modifyOperations.add(createTableASOperation.toSinkModifyOperation(getCatalogManager()));
                        } else {
                            modifyOperations.add((ModifyOperation) operation);
                        }
                    } else if (operation instanceof QueryOperation) {
                        modifyOperations.add(new CollectModifyOperation((QueryOperation) operation));
                    } else {
                        log.info("Only insert statement or select is supported now. The statement is skipped: "
                                + operation.asSummaryString());
                    }
                });
        if (modifyOperations.isEmpty()) {
            throw new TableException("Only insert statement is supported now. None operation to execute.");
        }
        return transOperatoinsToStreamGraph(modifyOperations);
    }

    @Override
    public SqlNode parseSql(String sql) {
        return ((ExtendedParser) getParser()).parseSql(sql);
    }

    @Override
    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        List<Operation> operations = getParser().parse(statement);
        if (operations.size() != 1) {
            throw new TableException("Unsupported SQL query! explainSql() only accepts a single SQL query.");
        }

        Operation operation = operations.get(0);
        SqlExplainResult data = new SqlExplainResult();
        data.setParseTrue(true);
        data.setExplainTrue(true);
        if (operation instanceof ModifyOperation) {
            if (operation instanceof CreateTableASOperation) {
                CreateTableASOperation createTableASOperation = (CreateTableASOperation) operation;
                executeInternal(createTableASOperation.getCreateTableOperation());
                operation = createTableASOperation.toSinkModifyOperation(getCatalogManager());
            }
            data.setType("DML");
        } else if (operation instanceof QueryOperation) {
            data.setType("DQL");
        } else {
            data.setExplain(operation.asSummaryString());
            data.setType("DDL");
            return data;
        }

        data.setExplain(getPlanner().explain(Collections.singletonList(operation), ExplainFormat.TEXT, extraDetails));
        return data;
    }

    @Override
    public SqlExplainResult explainStatementSet(List<JobStatement> statements, ExplainDetail... extraDetails) {
        SqlExplainResult.Builder resultBuilder = SqlExplainResult.Builder.newBuilder();
        List<Operation> operations = new ArrayList<>();
        for (JobStatement statement : statements) {
            if (statement.getSqlType().equals(SqlType.RTAS)) {
                resultBuilder
                        .sql(statement.getStatement())
                        .type(statement.getSqlType().getType())
                        .error("RTAS is not supported in Apache Flink 1.17.")
                        .parseTrue(false)
                        .explainTrue(false)
                        .explainTime(LocalDateTime.now());
                return resultBuilder.build();
            }
            try {
                List<Operation> itemOperations = getParser().parse(statement.getStatement());
                if (!itemOperations.isEmpty()) {
                    for (Operation operation : itemOperations) {
                        if (operation instanceof CreateTableASOperation) {
                            CreateTableASOperation createTableASOperation = (CreateTableASOperation) operation;
                            executeInternal(createTableASOperation.getCreateTableOperation());
                            operations.add(createTableASOperation.toSinkModifyOperation(getCatalogManager()));
                        } else {
                            operations.add(operation);
                        }
                    }
                }
            } catch (Exception e) {
                String error = StrFormatter.format(
                        "Exception in explaining FlinkSQL:\n{}\n{}",
                        SqlUtil.addLineNumber(statement.getStatement()),
                        e.getMessage());
                resultBuilder
                        .sql(statement.getStatement())
                        .type(SqlType.INSERT.getType())
                        .error(error)
                        .parseTrue(false)
                        .explainTrue(false)
                        .explainTime(LocalDateTime.now());
                log.error(error);
                return resultBuilder.build();
            }
        }
        if (operations.isEmpty()) {
            throw new DinkyException("None of the job in the statement set.");
        }
        resultBuilder.parseTrue(true);
        resultBuilder.explain(getPlanner().explain(operations, ExplainFormat.TEXT, extraDetails));
        return resultBuilder
                .explainTrue(true)
                .explainTime(LocalDateTime.now())
                .type(SqlType.INSERT.getType())
                .build();
    }

    @Override
    public TableResult executeStatementSet(List<JobStatement> statements) {
        statements.removeIf(statement -> statement.getSqlType().equals(SqlType.RTAS));
        statements.removeIf(statement -> !statement.getSqlType().isSinkyModify());
        List<ModifyOperation> modifyOperations = statements.stream()
                .map(statement -> getModifyOperationFromInsert(statement.getStatement()))
                .collect(Collectors.toList());
        return executeInternal(modifyOperations);
    }

    public ModifyOperation getModifyOperationFromInsert(String statement) {
        List<Operation> operations = getParser().parse(statement);
        if (operations.isEmpty()) {
            throw new TableException("None of the statement is parsed.");
        }
        if (operations.size() > 1) {
            throw new TableException("Only single statement is supported.");
        }
        Operation operation = operations.get(0);
        if (operation instanceof ModifyOperation) {
            if (operation instanceof CreateTableASOperation) {
                CreateTableASOperation createTableASOperation = (CreateTableASOperation) operation;
                executeInternal(createTableASOperation.getCreateTableOperation());
                return createTableASOperation.toSinkModifyOperation(getCatalogManager());
            } else {
                return (ModifyOperation) operation;
            }
        } else if (operation instanceof QueryOperation) {
            return new CollectModifyOperation((QueryOperation) operation);
        } else {
            log.info("Only insert statement or select is supported now. The statement is skipped: "
                    + operation.asSummaryString());
            return null;
        }
    }
}
