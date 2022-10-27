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

package com.dlink.explainer;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.Executor;
import com.dlink.explainer.ca.ColumnCA;
import com.dlink.explainer.ca.ColumnCAResult;
import com.dlink.explainer.ca.NodeRel;
import com.dlink.explainer.ca.TableCA;
import com.dlink.explainer.ca.TableCAGenerator;
import com.dlink.explainer.ca.TableCAResult;
import com.dlink.explainer.lineage.LineageColumnGenerator;
import com.dlink.explainer.lineage.LineageTableGenerator;
import com.dlink.explainer.trans.Trans;
import com.dlink.explainer.trans.TransGenerator;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.job.JobParam;
import com.dlink.job.StatementParam;
import com.dlink.model.LineageRel;
import com.dlink.model.SystemConfiguration;
import com.dlink.parser.SqlType;
import com.dlink.process.context.ProcessContextHolder;
import com.dlink.process.model.ProcessEntity;
import com.dlink.result.ExplainResult;
import com.dlink.result.SqlExplainResult;
import com.dlink.trans.Operations;
import com.dlink.utils.FlinkUtil;
import com.dlink.utils.LogUtil;
import com.dlink.utils.SqlUtil;

import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.table.catalog.CatalogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * Explainer
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class Explainer {
    private static final Logger log = LoggerFactory.getLogger(Explainer.class);
    public static final String DATA_SINK = "Data Sink";

    private final Executor executor;
    private final boolean useStatementSet;
    private String sqlSeparator = FlinkSQLConstant.SEPARATOR;
    private final ObjectMapper mapper = new ObjectMapper();

    public Explainer(Executor executor) {
        this(executor, true, SystemConfiguration.getInstances().getSqlSeparator());
    }

    public Explainer(Executor executor, boolean useStatementSet) {
        this(executor, useStatementSet, SystemConfiguration.getInstances().getSqlSeparator());

    }

    public Explainer(Executor executor, boolean useStatementSet, String sqlSeparator) {
        this.executor = executor;
        this.useStatementSet = useStatementSet;
        this.sqlSeparator = sqlSeparator;
    }

    public static Explainer build(Executor executor) {
        return new Explainer(executor, false, ";");
    }

    public static Explainer build(Executor executor, boolean useStatementSet, String sqlSeparator) {
        return new Explainer(executor, useStatementSet, sqlSeparator);
    }

    public JobParam pretreatStatements(String[] statements) {
        List<StatementParam> ddl = new ArrayList<>();
        List<StatementParam> trans = new ArrayList<>();
        List<StatementParam> execute = new ArrayList<>();
        List<String> statementList = new ArrayList<>();

        for (String item : statements) {
            String statement = executor.pretreatStatement(item);
            if (statement.isEmpty()) {
                continue;
            }

            SqlType operationType = Operations.getOperationType(statement);
            if (operationType.equals(SqlType.INSERT)
                || operationType.equals(SqlType.SELECT)
                || operationType.equals(SqlType.SHOW)
                || operationType.equals(SqlType.DESCRIBE)
                || operationType.equals(SqlType.DESC)) {
                trans.add(new StatementParam(statement, operationType));
                statementList.add(statement);
                if (!useStatementSet) {
                    return new JobParam(statementList, ddl, trans, execute);
                }
            } else if (operationType.equals(SqlType.EXECUTE)) {
                execute.add(new StatementParam(statement, operationType));
            } else {
                ddl.add(new StatementParam(statement, operationType));
                statementList.add(statement);
            }
        }
        return new JobParam(statementList, ddl, trans, execute);
    }

    public List<SqlExplainResult> explainSqlResult(String statement) {
        List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
        String[] statements = SqlUtil.getStatements(statement, sqlSeparator);
        for (String item : statements) {
            SqlExplainResult explainResult = new SqlExplainResult();
            try {
                String sql = FlinkInterceptor.pretreatStatement(executor, item);
                explainResult.setSql(sql);

                if (Asserts.isNullString(sql)) {
                    continue;
                }

                explainResult = executor.explainSqlRecord(sql);
                if (Asserts.isNull(explainResult)) {
                    explainResult = new SqlExplainResult();
                    continue;
                }

                SqlType operationType = Operations.getOperationType(sql);
                if (!(operationType.equals(SqlType.INSERT) || operationType.equals(SqlType.SELECT))) {
                    executor.executeSql(sql);
                }
                explainResult.setExplainTrue(true);
            } catch (Exception e) {
                log.error(e.getMessage());
                explainResult.setError(e.getMessage());
                explainResult.setExplainTrue(false);
                return sqlExplainRecords;
            } finally {
                explainResult.setExplainTimeNow();
                sqlExplainRecords.add(explainResult);
                explainResult.setIndex(sqlExplainRecords.size());
            }
        }

        return sqlExplainRecords;
    }

    public ExplainResult explainSql(String statement) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Start explain FlinkSQL...");

        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));

        List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
        boolean correct = true;
        for (StatementParam statementParam : jobParam.getDdl()) {
            String sql = statementParam.getValue();

            SqlExplainResult explainResult = new SqlExplainResult();
            explainResult.setSql(sql);

            try {
                explainResult = executor.explainSqlRecord(sql);
                if (Asserts.isNull(explainResult)) {
                    continue;
                }

                executor.executeSql(sql);
                explainResult.setExplainTrue(true);
            } catch (Exception e) {
                correct = false;
                explainResult.setExplainTrue(false);
                String error = LogUtil.getError(e);
                explainResult.setError(error);
                process.error(error);
                break;
            } finally {
                explainResult.setExplainTimeNow();
                sqlExplainRecords.add(explainResult);
                explainResult.setIndex(sqlExplainRecords.size());
            }
        }

        if (correct && !jobParam.getTrans().isEmpty()) {
            if (useStatementSet) {
                List<String> inserts = jobParam.getTrans().stream()
                    .filter(item -> item.getType().equals(SqlType.INSERT))
                    .map(StatementParam::getValue)
                    .collect(Collectors.toList());

                SqlExplainResult explainResult = new SqlExplainResult();
                if (!inserts.isEmpty()) {
                    try {
                        explainResult.setExplain(executor.explainStatementSet(inserts));
                        explainResult.setParseTrue(true);
                        explainResult.setExplainTrue(true);
                    } catch (Exception e) {
                        String error = LogUtil.getError(e);
                        explainResult.setError(error);
                        explainResult.setParseTrue(false);
                        explainResult.setExplainTrue(false);
                        correct = false;
                        process.error(error);
                    }

                    explainResult.setType("Modify DML");
                    explainResult.setExplainTimeNow();
                    explainResult.setSql(String.join(";\r\n ", inserts));
                    sqlExplainRecords.add(explainResult);
                    explainResult.setIndex(sqlExplainRecords.size());
                }
            } else {
                for (StatementParam item : jobParam.getTrans()) {
                    String sql = item.getValue();
                    SqlExplainResult explainResult = new SqlExplainResult();
                    try {
                        explainResult = executor.explainSqlRecord(sql);
                        explainResult.setParseTrue(true);
                        explainResult.setExplainTrue(true);
                    } catch (Exception e) {
                        String error = LogUtil.getError(e);
                        explainResult.setError(error);
                        explainResult.setParseTrue(false);
                        explainResult.setExplainTrue(false);
                        correct = false;
                        process.error(error);
                    }

                    explainResult.setType("Modify DML");
                    explainResult.setExplainTimeNow();
                    explainResult.setSql(sql);
                    sqlExplainRecords.add(explainResult);
                    explainResult.setIndex(sqlExplainRecords.size());
                }
            }
        }

        for (StatementParam item : jobParam.getExecute()) {
            SqlExplainResult explainResult = new SqlExplainResult();
            try {
                explainResult = executor.explainSqlRecord(item.getValue());
                if (Asserts.isNull(explainResult)) {
                    explainResult = new SqlExplainResult();
                    continue;
                }

                executor.executeSql(item.getValue());

                explainResult.setParseTrue(true);
                explainResult.setExplainTrue(true);
            } catch (Exception e) {
                String error = LogUtil.getError(e);
                explainResult.setError(error);
                explainResult.setExplainTrue(false);
                correct = false;
                process.error(error);
                break;
            } finally {
                explainResult.setType("DATASTREAM");
                explainResult.setExplainTimeNow();
                explainResult.setSql(item.getValue());
                sqlExplainRecords.add(explainResult);
                explainResult.setIndex(sqlExplainRecords.size());
            }
        }

        process.info(CharSequenceUtil.format("A total of {} FlinkSQL have been Explained.", sqlExplainRecords.size()));
        return new ExplainResult(correct, sqlExplainRecords.size(), sqlExplainRecords);
    }

    public ObjectNode getStreamGraph(String statement) {
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        if (!jobParam.getDdl().isEmpty()) {
            for (StatementParam statementParam : jobParam.getDdl()) {
                executor.executeSql(statementParam.getValue());
            }
        }

        if (!jobParam.getTrans().isEmpty()) {
            return executor.getStreamGraph(jobParam.getTransStatement());
        }

        if (jobParam.getExecute().isEmpty()) {
            return mapper.createObjectNode();
        }

        List<String> datastreamPlans = new ArrayList<>();
        for (StatementParam item : jobParam.getExecute()) {
            datastreamPlans.add(item.getValue());
        }

        return executor.getStreamGraphFromDataStream(datastreamPlans);
    }

    public JobPlanInfo getJobPlanInfo(String statement) {
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        if (!jobParam.getDdl().isEmpty()) {
            for (StatementParam statementParam : jobParam.getDdl()) {
                executor.executeSql(statementParam.getValue());
            }
        }

        if (!jobParam.getTrans().isEmpty()) {
            return executor.getJobPlanInfo(jobParam.getTransStatement());
        } else if (!jobParam.getExecute().isEmpty()) {
            List<String> datastreamPlans = new ArrayList<>();
            for (StatementParam item : jobParam.getExecute()) {
                datastreamPlans.add(item.getValue());
            }
            return executor.getJobPlanInfoFromDataStream(datastreamPlans);
        } else {
            throw new RuntimeException("Creating job plan fails because this job doesn't contain an insert statement.");
        }
    }

    private List<TableCAResult> generateTableCA(String statement, boolean onlyTable) {
        List<SqlExplainResult> sqlExplainRecords = explainSqlResult(statement);
        List<String> strPlans = new ArrayList<>();
        for (SqlExplainResult sqlExplainRecord : sqlExplainRecords) {
            if (Asserts.isNotNull(sqlExplainRecord.getType())
                && sqlExplainRecord.getType().contains(FlinkSQLConstant.DML)) {
                strPlans.add(sqlExplainRecord.getSql());
            }
        }

        List<TableCAResult> results = new ArrayList<>();
        for (String strPlan : strPlans) {
            List<Trans> trans = translateTrans(translateObjectNode(strPlan));
            TableCAGenerator generator = TableCAGenerator.build(trans);
            if (onlyTable) {
                generator.translateOnlyTable();
            } else {
                generator.translate();
            }
            results.add(generator.getResult());
        }

        if (!results.isEmpty()) {
            CatalogManager catalogManager = executor.getCatalogManager();
            for (TableCAResult result : results) {
                TableCA sinkTableCA = (TableCA) result.getSinkTableCA();
                if (Asserts.isNotNull(sinkTableCA)) {
                    sinkTableCA.setFields(FlinkUtil.getFieldNamesFromCatalogManager(catalogManager,
                        sinkTableCA.getCatalog(), sinkTableCA.getDatabase(), sinkTableCA.getTable()));
                }
            }
        }
        return results;
    }

    public List<TableCAResult> generateTableCA(String statement) {
        return generateTableCA(statement, true);
    }

    public List<TableCAResult> explainSqlTableColumnCA(String statement) {
        return generateTableCA(statement, false);
    }

    public List<ColumnCAResult> explainSqlColumnCA(String statement) {
        List<SqlExplainResult> sqlExplainRecords = explainSqlResult(statement);
        List<String> strPlans = new ArrayList<>();
        for (SqlExplainResult sqlExplainRecord : sqlExplainRecords) {
            if (Asserts.isNotNull(sqlExplainRecord.getType())
                && sqlExplainRecord.getType().contains("DML")) {
                strPlans.add(sqlExplainRecord.getSql());
            }
        }

        List<ColumnCAResult> results = new ArrayList<>();
        // statementsets
        if (useStatementSet) {
            ColumnCAResult columnCAResult = getColumnCAResult(translateObjectNode(strPlans));
            correctSinkSets(columnCAResult);
            results.add(columnCAResult);
        } else {
            for (String strPlan : strPlans) {
                ColumnCAResult columnCAResult = getColumnCAResult(translateObjectNode(strPlan));
                results.add(columnCAResult);
            }
        }
        return results;
    }

    private ColumnCAResult getColumnCAResult(ObjectNode strPlans) {
        List<Trans> trans = translateTrans(strPlans);
        LineageColumnGenerator generator = LineageColumnGenerator.build(trans);
        LineageTableGenerator tableGenerator = LineageTableGenerator.build(trans);
        tableGenerator.translate();
        generator.setTableCAS(tableGenerator.getTables());
        generator.translate();
        ColumnCAResult columnCAResult = new ColumnCAResult(generator);
        correctColumn(columnCAResult);
        return columnCAResult;
    }

    private void correctColumn(ColumnCAResult columnCAResult) {
        for (TableCA tableCA : columnCAResult.getTableCAS()) {
            if (tableCA.getType().equals(DATA_SINK)) {
                List<String> fields = tableCA.getFields();
                List<String> oldFields = new ArrayList<>(fields);
                CatalogManager catalogManager = executor.getCatalogManager();
                List<String> columnList = FlinkUtil.getFieldNamesFromCatalogManager(catalogManager, tableCA.getCatalog(), tableCA.getDatabase(), tableCA.getTable());

                for (int i = 0; i < columnList.size(); i++) {
                    String sinkColumnName = columnList.get(i);
                    if (!sinkColumnName.equals(oldFields.get(i))) {
                        for (Map.Entry<Integer, ColumnCA> item : columnCAResult.getColumnCASMaps().entrySet()) {
                            ColumnCA columnCA = item.getValue();
                            if (columnCA.getTableId().equals(tableCA.getId())
                                && columnCA.getName().equals(oldFields.get(i))) {
                                columnCA.setName(sinkColumnName);
                                fields.set(i, sinkColumnName);
                            }
                        }
                    }
                }
            }
        }

        for (TableCA tableCA : columnCAResult.getTableCAS()) {
            CatalogManager catalogManager = executor.getCatalogManager();
            List<String> columnList = FlinkUtil.getFieldNamesFromCatalogManager(catalogManager,
                tableCA.getCatalog(), tableCA.getDatabase(), tableCA.getTable());
            List<String> fields = tableCA.getFields();
            int i = 0;
            List<Integer> idList = new ArrayList<>();
            while (i < fields.size()) {
                if (!columnList.contains(fields.get(i))) {
                    for (Map.Entry<Integer, ColumnCA> item : columnCAResult.getColumnCASMaps().entrySet()) {
                        if (item.getValue().getName().equals(fields.get(i))
                            && item.getValue().getTableId().equals(tableCA.getId())) {
                            idList.add(item.getValue().getId());
                            break;
                        }
                    }
                    fields.remove(i);
                } else {
                    i++;
                }
            }

            for (Integer id : idList) {
                for (NodeRel nodeRel : columnCAResult.getColumnCASRelChain()) {
                    if (nodeRel.getPreId().equals(id)) {
                        columnCAResult.getColumnCASMaps().remove(id);
                        columnCAResult.getColumnCASRelChain().remove(nodeRel);
                        break;
                    }
                }
            }
        }
    }

    private void correctSinkSets(ColumnCAResult columnCAResult) {
        for (TableCA tableCA : columnCAResult.getTableCAS()) {
            if (tableCA.getType().equals(DATA_SINK)) {
                final Map<Integer, ColumnCA> columnCASMaps = columnCAResult.getColumnCASMaps();
                for (Map.Entry<Integer, ColumnCA> item : columnCASMaps.entrySet()) {
                    if (item.getValue().getTableId().equals(tableCA.getId())) {
                        List<NodeRel> addNodeRels = new ArrayList<>();
                        List<NodeRel> delNodeRels = new ArrayList<>();
                        final Set<NodeRel> columnCASRelChain = columnCAResult.getColumnCASRelChain();
                        for (NodeRel nodeRel : columnCASRelChain) {
                            if (nodeRel.getPreId().equals(item.getValue().getId())) {
                                for (NodeRel nodeRel2 : columnCASRelChain) {
                                    final Integer nodeRel2SufId = nodeRel2.getSufId();
                                    final Integer nodeRel2PreId = nodeRel2.getPreId();
                                    final Integer nodeRelSufId = nodeRel.getSufId();

                                    if (columnCASMaps.containsKey(nodeRel2SufId) && columnCASMaps.containsKey(nodeRel2PreId) && columnCASMaps.containsKey(nodeRelSufId) &&
                                        columnCASMaps.get(nodeRel2SufId).getTableId().equals(columnCASMaps.get(nodeRelSufId).getTableId()) &&
                                        columnCASMaps.get(nodeRel2SufId).getName().equals(columnCASMaps.get(nodeRelSufId).getName()) && !columnCASMaps.get(nodeRel2PreId).getType().equals(DATA_SINK)) {
                                        addNodeRels.add(new NodeRel(nodeRel2PreId, nodeRel.getPreId()));
                                    }
                                }

                                delNodeRels.add(nodeRel);
                            }
                        }

                        columnCASRelChain.addAll(addNodeRels);
                        columnCASRelChain.removeAll(delNodeRels);
                    }
                }
            }
        }
    }

    private ObjectNode translateObjectNode(String statement) {
        return executor.getStreamGraph(statement);
    }

    private ObjectNode translateObjectNode(List<String> statement) {
        return executor.getStreamGraph(statement);
    }

    private List<Trans> translateTrans(ObjectNode plan) {
        return new TransGenerator(plan).translateTrans();
    }

    public List<LineageRel> getLineage(String statement) {
        List<LineageRel> lineageRelList = new ArrayList<>();
        for (String item : SqlUtil.getStatements(statement, sqlSeparator)) {
            try {
                String sql = FlinkInterceptor.pretreatStatement(executor, item);
                if (Asserts.isNullString(sql)) {
                    continue;
                }

                SqlType operationType = Operations.getOperationType(sql);
                if (operationType.equals(SqlType.INSERT)) {
                    lineageRelList.addAll(executor.getLineage(sql));
                } else {
                    executor.executeSql(sql);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return lineageRelList;
    }
}
