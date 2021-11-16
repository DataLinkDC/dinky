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

    public JobParam(List<StatementParam> ddl, List<StatementParam> trans) {
        this.ddl = ddl;
        this.trans = trans;
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
}
