package com.dlink.explainer;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.Executor;
import com.dlink.explainer.ca.ColumnCAGenerator;
import com.dlink.explainer.ca.ColumnCAResult;
import com.dlink.explainer.ca.TableCA;
import com.dlink.explainer.ca.TableCAGenerator;
import com.dlink.explainer.ca.TableCAResult;
import com.dlink.explainer.trans.Trans;
import com.dlink.explainer.trans.TransGenerator;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.result.SqlExplainResult;
import com.dlink.utils.SqlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.ObjectIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public List<SqlExplainResult> explainSqlResult(String statement, ExplainDetail... extraDetails) {
        String[] sqls = SqlUtil.getStatements(statement);
        List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
        for (int i = 0; i < sqls.length; i++) {
            SqlExplainResult record = new SqlExplainResult();
            try {
                if (!FlinkInterceptor.build(executor.getCustomTableEnvironmentImpl(), sqls[i])) {
                    record = executor.explainSqlRecord(sqls[i], extraDetails);
                    if (Asserts.isEquals(FlinkSQLConstant.DDL,record.getType())) {
                        executor.executeSql(sqls[i]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                record.setError(e.getMessage());
            } finally {
                record.setExplainTime(new Date());
                record.setIndex(i + 1);
                record.setSql(sqls[i]);
                sqlExplainRecords.add(record);
            }
        }
        return sqlExplainRecords;
    }

    public ObjectNode getStreamGraph(String statement){
        List<SqlExplainResult> sqlExplainRecords = explainSqlResult(statement);
        List<String> strPlans = new ArrayList<>();
        for (int i = 0; i < sqlExplainRecords.size(); i++) {
            if (Asserts.isNotNull(sqlExplainRecords.get(i).getType())
                    && sqlExplainRecords.get(i).getType().contains(FlinkSQLConstant.DML)) {
                strPlans.add(sqlExplainRecords.get(i).getSql());
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
                    Optional<CatalogManager.TableLookupResult> tableOpt = catalogManager.getTable(
                            ObjectIdentifier.of(sinkTableCA.getCatalog(), sinkTableCA.getDatabase(), sinkTableCA.getTable())
                    );
                    if (tableOpt.isPresent()) {
                        String[] fieldNames = tableOpt.get().getResolvedSchema().getFieldNames();
                        sinkTableCA.setFields(Arrays.asList(fieldNames));
                    }
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
