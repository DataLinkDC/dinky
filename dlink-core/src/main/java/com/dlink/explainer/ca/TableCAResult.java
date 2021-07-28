package com.dlink.explainer.ca;

import java.util.List;

/**
 * TableCAResult
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class TableCAResult {
    private String sinkName;
    private List<ICA> sourceTableCAS ;
    private ICA sinkTableCA;

    public TableCAResult(TableCAGenerator generator) {
        this.sourceTableCAS = generator.getSourceTableCAS();
        this.sinkTableCA = generator.getSinkTableCA();
        this.sinkName = generator.getSinkTableName();
    }

    public String getSinkName() {
        return sinkName;
    }

    public void setSinkName(String sinkName) {
        this.sinkName = sinkName;
    }

    public List<ICA> getSourceTableCAS() {
        return sourceTableCAS;
    }

    public void setSourceTableCAS(List<ICA> sourceTableCAS) {
        this.sourceTableCAS = sourceTableCAS;
    }

    public ICA getSinkTableCA() {
        return sinkTableCA;
    }

    public void setSinkTableCA(ICA sinkTableCA) {
        this.sinkTableCA = sinkTableCA;
    }
}
