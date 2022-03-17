package com.dlink.explainer.lineage;

import java.util.List;

/**
 * LineageResult
 *
 * @author wenmo
 * @since 2022/3/15 22:59
 */
public class LineageResult {
    private List<LineageTable> tables;
    private List<LineageRelation> relations;

    public LineageResult() {
    }

    public LineageResult(List<LineageTable> tables, List<LineageRelation> relations) {
        this.tables = tables;
        this.relations = relations;
    }

    public static LineageResult build(List<LineageTable> tables, List<LineageRelation> relations){
        return new LineageResult(tables,relations);
    }

    public List<LineageTable> getTables() {
        return tables;
    }

    public void setTables(List<LineageTable> tables) {
        this.tables = tables;
    }

    public List<LineageRelation> getRelations() {
        return relations;
    }

    public void setRelations(List<LineageRelation> relations) {
        this.relations = relations;
    }
}
