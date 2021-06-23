package com.dlink.explainer.ca;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ColumnCAResult
 *
 * @author wenmo
 * @since 2021/6/22
 **/
@Getter
@Setter
public class ColumnCAResult {
    private String sinkName;
    private ICA sinkTableCA;
    private List<ICA> columnCAS;
    private Map<Integer, ICA> columnCASMaps;
    private Set<NodeRel> columnCASRel;
    private List<Integer> sinkColumns;
    private List<Integer> sourColumns;

    public ColumnCAResult(ColumnCAGenerator generator) {
        this.columnCAS = generator.getColumnCAS();
        this.sinkTableCA = generator.getSinkTableCA();
        this.sinkName = generator.getSinkTableName();
        this.columnCASMaps = generator.getColumnCASMaps();
        this.columnCASRel = generator.getColumnCASRel();
        this.sinkColumns = generator.getSinkColumns();
        this.sourColumns = generator.getSourceColumns();
    }
}
