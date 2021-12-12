package com.dlink.result;

import java.util.List;

/**
 * ExplainResult
 *
 * @author wenmo
 * @since 2021/12/12 13:11
 */
public class ExplainResult {
    private boolean correct;
    private int total;
    private List<SqlExplainResult> sqlExplainResults;

    public ExplainResult(boolean correct, int total, List<SqlExplainResult> sqlExplainResults) {
        this.correct = correct;
        this.total = total;
        this.sqlExplainResults = sqlExplainResults;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SqlExplainResult> getSqlExplainResults() {
        return sqlExplainResults;
    }

    public void setSqlExplainResults(List<SqlExplainResult> sqlExplainResults) {
        this.sqlExplainResults = sqlExplainResults;
    }
}
