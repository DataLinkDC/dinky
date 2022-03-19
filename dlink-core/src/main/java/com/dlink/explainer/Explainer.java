package com.dlink.explainer;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.Executor;
import com.dlink.explainer.ca.*;
import com.dlink.explainer.lineage.LineageColumnGenerator;
import com.dlink.explainer.lineage.LineageTableGenerator;
import com.dlink.explainer.trans.Trans;
import com.dlink.explainer.trans.TransGenerator;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.job.JobParam;
import com.dlink.job.StatementParam;
import com.dlink.model.SystemConfiguration;
import com.dlink.parser.SqlType;
import com.dlink.result.ExplainResult;
import com.dlink.result.SqlExplainResult;
import com.dlink.trans.Operations;
import com.dlink.utils.FlinkUtil;
import com.dlink.utils.SqlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.table.catalog.CatalogManager;

import java.time.LocalDateTime;
import java.util.*;

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
        for (String item : statements) {
            String statement = executor.pretreatStatement(item);
            if (statement.isEmpty()) {
                continue;
            }
            SqlType operationType = Operations.getOperationType(statement);
            if (operationType.equals(SqlType.INSERT) || operationType.equals(SqlType.SELECT) || operationType.equals(SqlType.SHOW)
                    || operationType.equals(SqlType.DESCRIBE) || operationType.equals(SqlType.DESC)) {
                trans.add(new StatementParam(statement, operationType));
                if (!useStatementSet) {
                    break;
                }
            } else if (operationType.equals(SqlType.EXECUTE)) {
                execute.add(new StatementParam(statement, operationType));
            } else {
                ddl.add(new StatementParam(statement, operationType));
            }
        }
        return new JobParam(ddl, trans, execute);
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
                SqlType operationType = Operations.getOperationType(item);
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
                e.printStackTrace();
                record.setError(e.getMessage());
                record.setExplainTrue(false);
                record.setExplainTime(LocalDateTime.now());
                record.setSql(item.getValue());
                record.setIndex(index);
                sqlExplainRecords.add(record);
                correct = false;
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
                        e.printStackTrace();
                        record.setError(e.getMessage());
                        record.setParseTrue(false);
                        record.setExplainTrue(false);
                        correct = false;
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
                        record.setExplain(executor.explainSql(item.getValue()));
                        record.setParseTrue(true);
                        record.setExplainTrue(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        record.setError(e.getMessage());
                        record.setParseTrue(false);
                        record.setExplainTrue(false);
                        correct = false;
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
                    executor.getStreamGraph();
                } else {
                    executor.executeSql(item.getValue());
                }
                record.setType("DATASTREAM");
                record.setParseTrue(true);
            } catch (Exception e) {
                e.printStackTrace();
                record.setError(e.getMessage());
                record.setExplainTrue(false);
                record.setExplainTime(LocalDateTime.now());
                record.setSql(item.getValue());
                record.setIndex(index);
                sqlExplainRecords.add(record);
                correct = false;
                break;
            }
            record.setExplainTrue(true);
            record.setExplainTime(LocalDateTime.now());
            record.setSql(item.getValue());
            record.setIndex(index++);
            sqlExplainRecords.add(record);
        }
        return new ExplainResult(correct, sqlExplainRecords.size(), sqlExplainRecords);
    }

    public ObjectNode getStreamGraph(String statement) {
        List<SqlExplainResult> sqlExplainRecords = explainSql(statement).getSqlExplainResults();
        List<String> sqlPlans = new ArrayList<>();
        List<String> datastreamPlans = new ArrayList<>();
        for (SqlExplainResult item : sqlExplainRecords) {
            if (Asserts.isNotNull(item.getType())
                    && item.getType().contains(FlinkSQLConstant.DML)) {
                String[] statements = SqlUtil.getStatements(item.getSql(), sqlSeparator);
                for (String str : statements) {
                    sqlPlans.add(str);
                }
                continue;
            }
            if (Asserts.isNotNull(item.getType())
                    && item.getType().equals(FlinkSQLConstant.DATASTREAM)) {
                String[] statements = SqlUtil.getStatements(item.getSql(), sqlSeparator);
                for (String str : statements) {
                    datastreamPlans.add(str);
                }
            }
        }
        if (sqlPlans.size() > 0) {
            return executor.getStreamGraph(sqlPlans);
        } else if (datastreamPlans.size() > 0) {
            return executor.getStreamGraphFromDataStream(sqlPlans);
        } else {
            return mapper.createObjectNode();
        }
    }

    public JobPlanInfo getJobPlanInfo(String statement) {
        List<SqlExplainResult> sqlExplainRecords = explainSql(statement).getSqlExplainResults();
        List<String> sqlPlans = new ArrayList<>();
        List<String> datastreamPlans = new ArrayList<>();
        for (SqlExplainResult item : sqlExplainRecords) {
            if (Asserts.isNotNull(item.getType())
                    && item.getType().contains(FlinkSQLConstant.DML)) {
                String[] statements = SqlUtil.getStatements(item.getSql(), sqlSeparator);
                for (String str : statements) {
                    sqlPlans.add(str);
                }
                continue;
            }
            if (Asserts.isNotNull(item.getType())
                    && item.getType().equals(FlinkSQLConstant.DATASTREAM)) {
                String[] statements = SqlUtil.getStatements(item.getSql(), sqlSeparator);
                for (String str : statements) {
                    datastreamPlans.add(str);
                }
            }
        }
        if (sqlPlans.size() > 0) {
            return executor.getJobPlanInfo(sqlPlans);
        } else if (datastreamPlans.size() > 0) {
            return executor.getJobPlanInfoFromDataStream(datastreamPlans);
        } else {
            return new JobPlanInfo("");
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
                    sinkTableCA.setFields(FlinkUtil.getFieldNamesFromCatalogManager(catalogManager, sinkTableCA.getCatalog(), sinkTableCA.getDatabase(), sinkTableCA.getTable()));
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
            if (Asserts.isNotNull(sqlExplainRecords.get(i).getType()) && sqlExplainRecords.get(i).getType().contains("DML")) {
                strPlans.add(sqlExplainRecords.get(i).getSql());
            }
        }
        List<ColumnCAResult> results = new ArrayList<>();
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
        return results;
    }

    private void correctColumn(ColumnCAResult columnCAResult) {
        for (TableCA tableCA : columnCAResult.getTableCAS()) {
            CatalogManager catalogManager = executor.getCatalogManager();
            List<String> columnList = FlinkUtil.getFieldNamesFromCatalogManager(catalogManager, tableCA.getCatalog(), tableCA.getDatabase(), tableCA.getTable());
            List<String> fields = tableCA.getFields();
            List<String> oldFields = new ArrayList<>();
            oldFields.addAll(fields);
            if (tableCA.getType().equals("Data Sink")) {
                for (int i = 0; i < columnList.size(); i++) {
                    String sinkColumnName = columnList.get(i);
                    if (!sinkColumnName.equals(oldFields.get(i))) {
                        for (Map.Entry<Integer, ColumnCA> item : columnCAResult.getColumnCASMaps().entrySet()) {
                            ColumnCA columnCA = item.getValue();
                            if (columnCA.getTableId() == tableCA.getId() && columnCA.getName().equals(oldFields.get(i))) {
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
            List<String> columnList = FlinkUtil.getFieldNamesFromCatalogManager(catalogManager, tableCA.getCatalog(), tableCA.getDatabase(), tableCA.getTable());
            List<String> fields = tableCA.getFields();
            int i = 0;
            while (i < fields.size()) {
                if (!columnList.contains(fields.get(i))) {
                    List<Integer> idList = new ArrayList<>();
                    for (Map.Entry<Integer, ColumnCA> item : columnCAResult.getColumnCASMaps().entrySet()) {
                        if (item.getValue().getName().equals(fields.get(i)) && item.getValue().getTableId() == tableCA.getId()) {
                            idList.add(item.getValue().getId());
                            break;
                        }
                    }
                    for (Integer id : idList) {
                        for (NodeRel nodeRel : columnCAResult.getColumnCASRelChain()) {
                            if (nodeRel.getPreId() == id) {
                                columnCAResult.getColumnCASMaps().remove(id);
                                columnCAResult.getColumnCASRelChain().remove(nodeRel);
                                break;
                            }
                        }
                    }
                    fields.remove(i);
                } else {
                    i++;
                }
            }
        }
    }

    private ObjectNode translateObjectNode(String statement) {
        return executor.getStreamGraph(statement);
    }

    private List<Trans> translateTrans(ObjectNode plan) {
        return new TransGenerator(plan).translateTrans();
    }

}
