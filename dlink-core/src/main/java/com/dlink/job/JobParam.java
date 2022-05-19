package com.dlink.job;

import java.util.ArrayList;
import java.util.List;

/**
 * JobParam
 *
 * @author wenmo
 * @since 2021/11/16
 */
public class JobParam {
    private List<String> statements;
    private List<StatementParam> ddl;
    private List<StatementParam> trans;
    private List<StatementParam> execute;

    public JobParam(List<StatementParam> ddl, List<StatementParam> trans) {
        this.ddl = ddl;
        this.trans = trans;
    }

    public JobParam(List<String> statements, List<StatementParam> ddl, List<StatementParam> trans, List<StatementParam> execute) {
        this.statements = statements;
        this.ddl = ddl;
        this.trans = trans;
        this.execute = execute;
    }

    public List<String> getStatements() {
        return statements;
    }

    public void setStatements(List<String> statements) {
        this.statements = statements;
    }

    public List<StatementParam> getDdl() {
        return ddl;
    }

    public void setDdl(List<StatementParam> ddl) {
        this.ddl = ddl;
    }

    public List<StatementParam> getTrans() {
        return trans;
    }

    public List<String> getTransStatement() {
        List<String> statementList = new ArrayList<>();
        for(StatementParam statementParam: trans){
            statementList.add(statementParam.getValue());
        }
        return statementList;
    }

    public void setTrans(List<StatementParam> trans) {
        this.trans = trans;
    }

    public List<StatementParam> getExecute() {
        return execute;
    }

    public void setExecute(List<StatementParam> execute) {
        this.execute = execute;
    }
}
