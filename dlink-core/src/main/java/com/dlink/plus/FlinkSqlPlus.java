package com.dlink.plus;

import com.dlink.executor.Executor;
import com.dlink.explainer.Explainer;
import com.dlink.explainer.ca.ColumnCAResult;
import com.dlink.explainer.ca.TableCAResult;
import com.dlink.result.SqlExplainResult;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.table.api.ExplainDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * FlinkSqlPlus
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class FlinkSqlPlus {

    private Executor executor;
    private Explainer explainer;

    public FlinkSqlPlus(Executor executor) {
        this.executor = executor;
        this.explainer = new Explainer(executor);
    }

    public static FlinkSqlPlus build(){
        return new FlinkSqlPlus(Executor.build());
    }

    public List<SqlResult> executeSql(String sql) {
        if (sql == null || "".equals(sql)) {
            return new ArrayList<>();
        }
        String[] sqls = sql.split(";");
        List<SqlResult> sqlResults = new ArrayList<>();
        try {
            for (int i = 0; i < sqls.length; i++) {
                sqlResults.add(new SqlResult(executor.executeSql(sqls[i])));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sqlResults.add(new SqlResult(false, e.getMessage()));
            return sqlResults;
        }
        return sqlResults;
    }

    public SqlResult execute(String sql) {
        if (sql == null || "".equals(sql)) {
            return SqlResult.NULL;
        }
        try {
            return new SqlResult(executor.executeSql(sql));
        } catch (Exception e) {
            return new SqlResult(false,e.getMessage());
        }
    }

    public List<SqlExplainResult> explainSqlRecord(String statement) {
        return explainer.explainSqlResult(statement);
    }

    public List<TableCAResult> explainSqlTableColumnCA(String statement) {
        return explainer.explainSqlTableColumnCA(statement);
    }

    public List<TableCAResult> generateTableCA(String statement) {
        return explainer.generateTableCA(statement);
    }

    public List<ColumnCAResult> explainSqlColumnCA(String statement) {
        return explainer.explainSqlColumnCA(statement);
    }

    public ObjectNode getStreamGraph(String statement) {
        return executor.getStreamGraph(statement);
    }

    public JobPlanInfo getJobPlanInfo(String statement) {
        return explainer.getJobPlanInfo(statement);
    }

}
