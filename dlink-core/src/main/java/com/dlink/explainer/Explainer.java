package com.dlink.explainer;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.Executor;
import com.dlink.explainer.ca.*;
import com.dlink.explainer.trans.Trans;
import com.dlink.explainer.trans.TransGenerator;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.job.JobParam;
import com.dlink.job.StatementParam;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Explainer
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class Explainer {

    private Executor executor;
    private boolean useStatementSet;
    private ObjectMapper mapper = new ObjectMapper();

    public Explainer(Executor executor) {
        this.executor = executor;
    }

    public Explainer(Executor executor, boolean useStatementSet) {
        this.executor = executor;
        this.useStatementSet = useStatementSet;
    }

    public static Explainer build(Executor executor){
        return new Explainer(executor,false);
    }

    public static Explainer build(Executor executor, boolean useStatementSet){
        return new Explainer(executor,useStatementSet);
    }

    public JobParam pretreatStatements(String[] statements) {
        List<StatementParam> ddl = new ArrayList<>();
        List<StatementParam> trans = new ArrayList<>();
        for (String item : statements) {
            String statement = executor.pretreatStatement(item);
            if (statement.isEmpty()) {
                continue;
            }
            SqlType operationType = Operations.getOperationType(statement);
            if (operationType.equals(SqlType.INSERT) || operationType.equals(SqlType.SELECT)) {
                trans.add(new StatementParam(statement, operationType));
                if (!useStatementSet) {
                    break;
                }
            } else {
                ddl.add(new StatementParam(statement, operationType));
            }
        }
        return new JobParam(ddl, trans);
    }

    @Deprecated
    public List<SqlExplainResult> explainSqlResult(String statement) {
        String[] sqls = SqlUtil.getStatements(statement);
        List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
        int index = 1;
        for (String item : sqls) {
            SqlExplainResult record = new SqlExplainResult();
            String sql = "";
            try {
                sql = FlinkInterceptor.pretreatStatement(executor,item);
                if(Asserts.isNullString(sql)){
                    continue;
                }
                SqlType operationType = Operations.getOperationType(item);
                if (operationType.equals(SqlType.INSERT)||operationType.equals(SqlType.SELECT)) {
                    record = executor.explainSqlRecord(sql);
                    if(Asserts.isNull(record)){
                        continue;
                    }
                }else{
                    record = executor.explainSqlRecord(sql);
                    if(Asserts.isNull(record)){
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
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement));
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
            }catch (Exception e){
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
                    String sqlSet = String.join(FlinkSQLConstant.SEPARATOR, inserts);
                    try {
                        record.setExplain(executor.explainStatementSet(inserts));
                        record.setParseTrue(true);
                        record.setExplainTrue(true);
                    }catch (Exception e){
                        e.printStackTrace();
                        record.setError(e.getMessage());
                        record.setParseTrue(false);
                        record.setExplainTrue(false);
                        correct = false;
                    }finally {
                        record.setType("Modify DML");
                        record.setExplainTime(LocalDateTime.now());
                        record.setSql(sqlSet);
                        record.setIndex(index);
                        sqlExplainRecords.add(record);
                    }
                }
            }else{
                for (StatementParam item : jobParam.getTrans()) {
                    SqlExplainResult record = new SqlExplainResult();
                    try {
                        record.setExplain(executor.explainSql(item.getValue()));
                        record.setParseTrue(true);
                        record.setExplainTrue(true);
                    }catch (Exception e){
                        e.printStackTrace();
                        record.setError(e.getMessage());
                        record.setParseTrue(false);
                        record.setExplainTrue(false);
                        correct = false;
                    }finally {
                        record.setType("Modify DML");
                        record.setExplainTime(LocalDateTime.now());
                        record.setSql(item.getValue());
                        record.setIndex(index++);
                        sqlExplainRecords.add(record);
                    }
                }
            }
        }
        return new ExplainResult(correct,sqlExplainRecords.size(),sqlExplainRecords);
    }

    public ObjectNode getStreamGraph(String statement){
        List<SqlExplainResult> sqlExplainRecords = explainSql(statement).getSqlExplainResults();
        List<String> strPlans = new ArrayList<>();
        for (SqlExplainResult item : sqlExplainRecords) {
            if (Asserts.isNotNull(item.getType())
                    && item.getType().contains(FlinkSQLConstant.DML)) {
                String[] statements = SqlUtil.getStatements(item.getSql());
                for(String str : statements){
                    strPlans.add(str);
                }
            }
        }
        if(strPlans.size()>0){
            return executor.getStreamGraph(strPlans);
        }else{
            return mapper.createObjectNode();
        }
    }

    public JobPlanInfo getJobPlanInfo(String statement){
        List<SqlExplainResult> sqlExplainRecords = explainSql(statement).getSqlExplainResults();
        List<String> strPlans = new ArrayList<>();
        for (SqlExplainResult item : sqlExplainRecords) {
            if (Asserts.isNotNull(item.getType())
                    && item.getType().contains(FlinkSQLConstant.DML)) {
                String[] statements = SqlUtil.getStatements(item.getSql());
                for(String str : statements){
                    strPlans.add(str);
                }
            }
        }
        if(strPlans.size()>0){
            return executor.getJobPlanInfo(strPlans);
        }else{
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
                    sinkTableCA.setFields(FlinkUtil.getFieldNamesFromCatalogManager(catalogManager,sinkTableCA.getCatalog(), sinkTableCA.getDatabase(), sinkTableCA.getTable()));
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
            ColumnCAGenerator generator = new ColumnCAGenerator(trans);
            TableCAGenerator tableGenerator = new TableCAGenerator(trans);
            tableGenerator.translate();
            generator.setSourceTableCAS(tableGenerator.getSourceTableCAS());
            generator.translate();
            results.add(new ColumnCAResult(generator));
        }
        return results;
    }

    private ObjectNode translateObjectNode(String statement) {
        return executor.getStreamGraph(statement);
    }

    private List<Trans> translateTrans(ObjectNode plan) {
        return new TransGenerator(plan).translateTrans();
    }

}
