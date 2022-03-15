package com.dlink.explainer.lineage;

/**
 * LineageRelation
 *
 * @author wenmo
 * @since 2022/3/15 23:00
 */
public class LineageRelation {
    private String id;
    private String srcTableId;
    private String tgtTableId;
    private String srcTableColName;
    private String tgtTableColName;

    public LineageRelation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrcTableId() {
        return srcTableId;
    }

    public void setSrcTableId(String srcTableId) {
        this.srcTableId = srcTableId;
    }

    public String getTgtTableId() {
        return tgtTableId;
    }

    public void setTgtTableId(String tgtTableId) {
        this.tgtTableId = tgtTableId;
    }

    public String getSrcTableColName() {
        return srcTableColName;
    }

    public void setSrcTableColName(String srcTableColName) {
        this.srcTableColName = srcTableColName;
    }

    public String getTgtTableColName() {
        return tgtTableColName;
    }

    public void setTgtTableColName(String tgtTableColName) {
        this.tgtTableColName = tgtTableColName;
    }
}
