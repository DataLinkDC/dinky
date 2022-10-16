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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.util.StrUtil;

/**
 * Explainer
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class Explainer {

    private Executor executor;
    private boolean useStatementSet;
    private String sqlSeparator = FlinkSQLConstant.SEPARATOR;
    private ObjectMapper mapper = new ObjectMapper();

    public Explainer(Executor executor) {
        this.executor = executor;
        this.useStatementSet = true;
        init();
    }

    public Explainer(Executor executor, boolean useStatementSet) {
        this.executor = executor;
        this.useStatementSet = useStatementSet;
        init();
    }

    public Explainer(Executor executor, boolean useStatementSet, String sqlSeparator) {
        this.executor = executor;
        this.useStatementSet = useStatementSet;
        this.sqlSeparator = sqlSeparator;
    }

    public void init() {
        sqlSeparator = SystemConfiguration.getInstances().getSqlSeparator();
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
            if (operationType.equals(SqlType.INSERT) || operationType.equals(SqlType.SELECT)
                    || operationType.equals(SqlType.SHOW)
                    || operationType.equals(SqlType.DESCRIBE) || operationType.equals(SqlType.DESC)) {
                trans.add(new StatementParam(statement, operationType));
                statementList.add(statement);
                if (!useStatementSet) {
                    break;
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
        String[] sqls = SqlUtil.getStatements(statement, sqlSeparator);
        List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
        int index = 1;
        for (String item : sqls) {
            SqlExplainResult record = new SqlExplainResult();
            String sql = "";
            try {
                sql = FlinkInterceptor.pretreatStatement(executor, item);
                if (Asserts.isNullString(sql)) {
                    continue;
                }
                SqlType operationType = Operations.getOperationType(sql);
                if (operationType.equals(SqlType.INSERT) || operationType.equals(SqlType.SELECT)) {
                    record = executor.explainSqlRecord(sql);
                    if (Asserts.isNull(record)) {
                        continue;
                    }
                } else {
                    record = executor.explainSqlRecord(sql);
                    if (Asserts.isNull(record)) {
                        continue;
                    }
                    executor.executeSql(sql);
                }
            } catch (Exception e) {
                e.printStackTrace();
                record.setError(e.getMessage());
                record.setExplainTrue(false);
                record.setExplainTime(LocalDateTime.now());
                record.setSql(sql);
                record.setIndex(index);
                sqlExplainRecords.add(record);
                break;
            }
            record.setExplainTrue(true);
            record.setExplainTime(LocalDateTime.now());
            record.setSql(sql);
            record.setIndex(index++);
            sqlExplainRecords.add(record);
        }
        return sqlExplainRecords;
    }

    public ExplainResult explainSql(String statement) {
        ProcessEntity process = ProcessContextHolder.getProcess();
        process.info("Start explain FlinkSQL...");
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
        int index = 1;
        boolean correct = true;
        for (StatementParam item : jobParam.getDdl()) {
            SqlExplainResult record = new SqlExplainResult();
            try {
                record = executor.explainSqlRecord(item.getValue());
                if (Asserts.isNull(record)) {
                    continue;
                }
                executor.executeSql(item.getValue());
            } catch (Exception e) {
                String error = LogUtil.getError(e);
                record.setError(error);
                record.setExplainTrue(false);
                record.setExplainTime(LocalDateTime.now());
                record.setSql(item.getValue());
                record.setIndex(index);
                sqlExplainRecords.add(record);
                correct = false;
                process.error(error);
                break;
            }
            record.setExplainTrue(true);
            record.setExplainTime(LocalDateTime.now());
            record.setSql(item.getValue());
            record.setIndex(index++);
            sqlExplainRecords.add(record);
        }
        if (correct && jobParam.getTrans().size() > 0) {
            if (useStatementSet) {
                SqlExplainResult record = new SqlExplainResult();
                List<String> inserts = new ArrayList<>();
                for (StatementParam item : jobParam.getTrans()) {
                    if (item.getType().equals(SqlType.INSERT)) {
                        inserts.add(item.getValue());
                    }
                }
                if (inserts.size() > 0) {
                    String sqlSet = String.join(";\r\n ", inserts);
                    try {
                        record.setExplain(executor.explainStatementSet(inserts));
                        record.setParseTrue(true);
                        record.setExplainTrue(true);
                    } catch (Exception e) {
                        String error = LogUtil.getError(e);
                        record.setError(error);
                        record.setParseTrue(false);
                        record.setExplainTrue(false);
                        correct = false;
                        process.error(error);
                    } finally {
                        record.setType("Modify DML");
                        record.setExplainTime(LocalDateTime.now());
                        record.setSql(sqlSet);
                        record.setIndex(index);
                        sqlExplainRecords.add(record);
                    }
                }
            } else {
                for (StatementParam item : jobParam.getTrans()) {
                    SqlExplainResult record = new SqlExplainResult();
                    try {
                        record = executor.explainSqlRecord(item.getValue());
                        record.setParseTrue(true);
                        record.setExplainTrue(true);
                    } catch (Exception e) {
                        String error = LogUtil.getError(e);
                        record.setError(error);
                        record.setParseTrue(false);
                        record.setExplainTrue(false);
                        correct = false;
                        process.error(error);
                    } finally {
                        record.setType("Modify DML");
                        record.setExplainTime(LocalDateTime.now());
                        record.setSql(item.getValue());
                        record.setIndex(index++);
                        sqlExplainRecords.add(record);
                    }
                }
            }
        }
        for (StatementParam item : jobParam.getExecute()) {
            SqlExplainResult record = new SqlExplainResult();
            try {
                record = executor.explainSqlRecord(item.getValue());
                if (Asserts.isNull(record)) {
                    record = new SqlExplainResult();
                } else {
                    executor.executeSql(item.getValue());
                }
                record.setType("DATASTREAM");
                record.setParseTrue(true);
            } catch (Exception e) {
                String error = LogUtil.getError(e);
                record.setError(error);
                record.setExplainTrue(false);
                record.setExplainTime(LocalDateTime.now());
                record.setSql(item.getValue());
                record.setIndex(index);
                sqlExplainRecords.add(record);
                correct = false;
                process.error(error);
                break;
            }
            record.setExplainTrue(true);
            record.setExplainTime(LocalDateTime.now());
            record.setSql(item.getValue());
            record.setIndex(index++);
            sqlExplainRecords.add(record);
        }
        process.info(StrUtil.format("A total of {} FlinkSQL have been Explained.", sqlExplainRecords.size()));
        return new ExplainResult(correct, sqlExplainRecords.size(), sqlExplainRecords);
    }

    public ObjectNode getStreamGraph(String statement) {
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        if (jobParam.getDdl().size() > 0) {
            for (StatementParam statementParam : jobParam.getDdl()) {
                executor.executeSql(statementParam.getValue());
            }
        }
        if (jobParam.getTrans().size() > 0) {
            return executor.getStreamGraph(jobParam.getTransStatement());
        } else if (jobParam.getExecute().size() > 0) {
            List<String> datastreamPlans = new ArrayList<>();
            for (StatementParam item : jobParam.getExecute()) {
                datastreamPlans.add(item.getValue());
            }
            return executor.getStreamGraphFromDataStream(datastreamPlans);
        } else {
            return mapper.createObjectNode();
        }
    }

    public JobPlanInfo getJobPlanInfo(String statement) {
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
        if (jobParam.getDdl().size() > 0) {
            for (StatementParam statementParam : jobParam.getDdl()) {
                executor.executeSql(statementParam.getValue());
            }
        }
        if (jobParam.getTrans().size() > 0) {
            return executor.getJobPlanInfo(jobParam.getTransStatement());
        } else if (jobParam.getExecute().size() > 0) {
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
        for (int i = 0; i < sqlExplainRecords.size(); i++) {
            if (Asserts.isNotNull(sqlExplainRecords.get(i).getType())
                    && sqlExplainRecords.get(i).getType().contains(FlinkSQLConstant.DML)) {
                strPlans.add(sqlExplainRecords.get(i).getSql());
            }
        }
        List<TableCAResult> results = new ArrayList<>();
        for (int i = 0; i < strPlans.size(); i++) {
            List<Trans> trans = translateTrans(translateObjectNode(strPlans.get(i)));
            TableCAGenerator generator = TableCAGenerator.build(trans);
            if (onlyTable) {
                generator.translateOnlyTable();
            } else {
                generator.translate();
            }
            results.add(generator.getResult());
        }
        if (results.size() > 0) {
            CatalogManager catalogManager = executor.getCatalogManager();
            for (int i = 0; i < results.size(); i++) {
                TableCA sinkTableCA = (TableCA) results.get(i).getSinkTableCA();
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
        for (int i = 0; i < sqlExplainRecords.size(); i++) {
            if (Asserts.isNotNull(sqlExplainRecords.get(i).getType())
                    && sqlExplainRecords.get(i).getType().contains("DML")) {
                strPlans.add(sqlExplainRecords.get(i).getSql());
            }
        }
        List<ColumnCAResult> results = new ArrayList<>();
        // statementsets
        if (useStatementSet) {
            List<Trans> trans = translateTrans(translateObjectNode(strPlans));
            LineageColumnGenerator generator = LineageColumnGenerator.build(trans);
            LineageTableGenerator tableGenerator = LineageTableGenerator.build(trans);
            tableGenerator.translate();
            generator.setTableCAS(tableGenerator.getTables());
            generator.translate();
            ColumnCAResult columnCAResult = new ColumnCAResult(generator);
            correctColumn(columnCAResult);
            correctSinkSets(columnCAResult);
            results.add(columnCAResult);
        } else {
            for (int i = 0; i < strPlans.size(); i++) {
                List<Trans> trans = translateTrans(translateObjectNode(strPlans.get(i)));
                LineageColumnGenerator generator = LineageColumnGenerator.build(trans);
                LineageTableGenerator tableGenerator = LineageTableGenerator.build(trans);
                tableGenerator.translate();
                generator.setTableCAS(tableGenerator.getTables());
                generator.translate();
                ColumnCAResult columnCAResult = new ColumnCAResult(generator);
                correctColumn(columnCAResult);
                results.add(columnCAResult);
            }
        }
        return results;
    }

    private void correctColumn(ColumnCAResult columnCAResult) {
        for (TableCA tableCA : columnCAResult.getTableCAS()) {
            CatalogManager catalogManager = executor.getCatalogManager();
            List<String> columnList = FlinkUtil.getFieldNamesFromCatalogManager(catalogManager, tableCA.getCatalog(),
                    tableCA.getDatabase(), tableCA.getTable());
            List<String> fields = tableCA.getFields();
            List<String> oldFields = new ArrayList<>();
            oldFields.addAll(fields);
            if (tableCA.getType().equals("Data Sink")) {
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
            List<String> columnList = FlinkUtil.getFieldNamesFromCatalogManager(catalogManager, tableCA.getCatalog(),
                    tableCA.getDatabase(), tableCA.getTable());
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
            if (tableCA.getType().equals("Data Sink")) {
                for (Map.Entry<Integer, ColumnCA> item : columnCAResult.getColumnCASMaps().entrySet()) {
                    if (item.getValue().getTableId().equals(tableCA.getId())) {
                        List<NodeRel> addNodeRels = new ArrayList<>();
                        List<NodeRel> delNodeRels = new ArrayList<>();
                        for (NodeRel nodeRel : columnCAResult.getColumnCASRelChain()) {
                            if (nodeRel.getPreId().equals(item.getValue().getId())) {
                                for (NodeRel nodeRel2 : columnCAResult.getColumnCASRelChain()) {
                                    if (columnCAResult.getColumnCASMaps().containsKey(nodeRel2.getSufId())
                                            && columnCAResult.getColumnCASMaps().containsKey(nodeRel2.getPreId())
                                            && columnCAResult.getColumnCASMaps().containsKey(nodeRel.getSufId())
                                            && columnCAResult.getColumnCASMaps().get(nodeRel2.getSufId()).getTableId()
                                                    .equals(columnCAResult.getColumnCASMaps().get(nodeRel.getSufId())
                                                            .getTableId())
                                            && columnCAResult.getColumnCASMaps().get(nodeRel2.getSufId()).getName()
                                                    .equals(columnCAResult.getColumnCASMaps().get(nodeRel.getSufId())
                                                            .getName())
                                            && !columnCAResult.getColumnCASMaps().get(nodeRel2.getPreId()).getType()
                                                    .equals("Data Sink")) {
                                        addNodeRels.add(new NodeRel(nodeRel2.getPreId(), nodeRel.getPreId()));
                                    }
                                }
                                delNodeRels.add(nodeRel);
                            }
                        }
                        for (NodeRel nodeRel : addNodeRels) {
                            columnCAResult.getColumnCASRelChain().add(nodeRel);
                        }
                        for (NodeRel nodeRel : delNodeRels) {
                            columnCAResult.getColumnCASRelChain().remove(nodeRel);
                        }
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
        String[] sqls = SqlUtil.getStatements(statement, sqlSeparator);
        List<LineageRel> lineageRelList = new ArrayList<>();
        for (String item : sqls) {
            String sql = "";
            try {
                sql = FlinkInterceptor.pretreatStatement(executor, item);
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
                e.printStackTrace();
                break;
            }
        }
        return lineageRelList;
    }
}
