package com.dlink.explainer;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.Executor;
import com.dlink.explainer.ca.*;
import com.dlink.explainer.trans.Trans;
import com.dlink.explainer.trans.TransGenerator;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.parser.SqlType;
import com.dlink.result.SqlExplainResult;
import com.dlink.trans.Operations;
import com.dlink.utils.FlinkUtil;
import com.dlink.utils.SqlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.table.catalog.CatalogManager;

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
    private ObjectMapper mapper = new ObjectMapper();

    public Explainer(Executor executor) {
        this.executor = executor;
    }

    public static Explainer build(Executor executor){
        return new Explainer(executor);
    }

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
                SqlType operationType = Operations.getOperationType(statement);
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
                record.setExplainTime(new Date());
                record.setSql(sql);
                record.setIndex(index);
                sqlExplainRecords.add(record);
                break;
            }
            record.setExplainTrue(true);
            record.setExplainTime(new Date());
            record.setSql(sql);
            record.setIndex(index++);
            sqlExplainRecords.add(record);
        }
        return sqlExplainRecords;
    }

    public ObjectNode getStreamGraph(String statement){
        List<SqlExplainResult> sqlExplainRecords = explainSqlResult(statement);
        List<String> strPlans = new ArrayList<>();
        for (SqlExplainResult item : sqlExplainRecords) {
            if (Asserts.isNotNull(item.getType())
                    && item.getType().contains(FlinkSQLConstant.DML)) {
                strPlans.add(item.getSql());
            }
        }
        if(strPlans.size()>0){
            return translateObjectNode(strPlans.get(0));
        }else{
            return mapper.createObjectNode();
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
