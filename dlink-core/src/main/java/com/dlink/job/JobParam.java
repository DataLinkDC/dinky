package com.dlink.job;

import java.util.List;

/**
 * JobParam
 *
 * @author wenmo
 * @since 2021/11/16
 */
public class JobParam {
    private List<StatementParam> ddl;
    private List<StatementParam> trans;
    private List<StatementParam> execute;

    public JobParam(List<StatementParam> ddl, List<StatementParam> trans) {
        this.ddl = ddl;
        this.trans = trans;
    }
    public JobParam(List<StatementParam> ddl, List<StatementParam> trans, List<StatementParam> execute) {
        this.ddl = ddl;
        this.trans = trans;
        this.execute = execute;
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
