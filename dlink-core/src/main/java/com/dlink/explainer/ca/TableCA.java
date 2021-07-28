package com.dlink.explainer.ca;

import com.dlink.explainer.trans.SinkTrans;
import com.dlink.explainer.trans.SourceTrans;
import com.dlink.explainer.trans.Trans;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TableCA
 *
 * @author wenmo
 * @since 2021/6/22
 **/
@Getter
@Setter
public class TableCA implements ICA{
    private Integer id;
    private Integer parentId;
    private String name;
    private String catalog;
    private String database;
    private String table;
    private String type;
    private List<String> fields;
    private List<String> useFields;
    private List<String> alias;
    private Set<Integer> columnCAIds = new HashSet<>();
    private Integer parallelism;

    private static final TableCA EMPTY = new TableCA();

    public TableCA() {
    }

    public TableCA(SourceTrans trans) {
        this.id = trans.getId();
        this.parentId = trans.getParentId();
        this.name = trans.getName();
        this.catalog = trans.getCatalog();
        this.database = trans.getDatabase();
        this.table = trans.getTable();
        this.fields = trans.getFields();
        this.useFields = trans.getFields();
        this.parallelism = trans.getParallelism();
        this.type = trans.getPact();
    }

    public TableCA(SinkTrans trans) {
        this.id = trans.getId();
        this.parentId = trans.getParentId();
        this.name = trans.getName();
        this.catalog = trans.getCatalog();
        this.database = trans.getDatabase();
        this.table = trans.getTable();
        this.fields = trans.getFields();
        this.useFields = trans.getFields();
        this.parallelism = trans.getParallelism();
        this.type = trans.getPact();
    }

    public static TableCA build(Trans trans){
        if(trans instanceof SourceTrans){
            return new TableCA((SourceTrans)trans);
        }else if(trans instanceof SinkTrans){
            return new TableCA((SinkTrans)trans);
        }else{
            return TableCA.EMPTY;
        }
    }

    @Override
    public String toString() {
        return "TableCA{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", catalog='" + catalog + '\'' +
                ", database='" + database + '\'' +
                ", table='" + table + '\'' +
                ", fields=" + fields +
                ", useFields=" + useFields +
                ", parallelism=" + parallelism +
                '}';
    }

    @Override
    public String getTableName() {
        return this.table;
    }
}
