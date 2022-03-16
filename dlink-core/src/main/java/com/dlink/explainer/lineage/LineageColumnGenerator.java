package com.dlink.explainer.lineage;

import com.dlink.assertion.Asserts;
import com.dlink.explainer.ca.ColumnCA;
import com.dlink.explainer.ca.NodeRel;
import com.dlink.explainer.ca.TableCA;
import com.dlink.explainer.trans.Field;
import com.dlink.explainer.trans.OperatorTrans;
import com.dlink.explainer.trans.SinkTrans;
import com.dlink.explainer.trans.SourceTrans;
import com.dlink.explainer.trans.Trans;
import com.dlink.utils.MapParseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * LineageColumnGenerator
 *
 * @author wenmo
 * @since 2022/3/16 20:20
 **/
public class LineageColumnGenerator {

    private Map<Integer, Trans> transMaps;
    private List<TableCA> tableCAS = new ArrayList<>();
    private List<ColumnCA> columnCAS = new ArrayList<>();
    private Map<Integer, ColumnCA> columnCASMaps = new HashMap<>();
    private Set<NodeRel> columnCASRel = new HashSet<>();
    private Set<NodeRel> columnCASRelChain = new HashSet<>();
    private int index = 1;
    private List<Integer> sinkColumns = new ArrayList<>();
    private List<Integer> sourceColumns = new ArrayList<>();

    public static LineageColumnGenerator build(List<Trans> transList) {
        LineageColumnGenerator generator = new LineageColumnGenerator();
        Map<Integer, Trans> map = new HashMap<>();
        for (Trans trans : transList) {
            map.put(trans.getId(), trans);
        }
        generator.setTransMaps(map);
        return generator;
    }

    public void translate() {
        for (Map.Entry<Integer, Trans> entry : transMaps.entrySet()) {
            Trans trans = entry.getValue();
            if (trans instanceof SourceTrans) {
                TableCA tableCA = new TableCA((SourceTrans) trans);
                for (String fieldName : tableCA.getFields()) {
                    int id = index++;
                    ColumnCA columnCA = new ColumnCA(id, fieldName, fieldName, fieldName, fieldName, fieldName, tableCA, trans);
                    columnCASMaps.put(id, columnCA);
                    columnCAS.add(columnCA);
                }
                for (ColumnCA columnCA : columnCAS) {
                    if (columnCA.getTableCA().getId() == tableCA.getId()) {
                        buildColumnCAFields(tableCA, tableCA.getParentId(), columnCA);
                    }
                }
            }
        }
        for (Map.Entry<Integer, Trans> entry : transMaps.entrySet()) {
            Trans trans = entry.getValue();
            if (trans instanceof SinkTrans) {
                TableCA tableCA = new TableCA((SinkTrans) trans);
                matchSinkField(tableCA,trans);
                searchColumnCAId(tableCA);
            }
        }
        chainRelation();
    }

    private void matchSinkField(TableCA tableCA,Trans trans){
        for(ColumnCA columnCA: columnCAS){
            for(String fieldName: tableCA.getFields()){
                if(columnCA.getName().equals(fieldName)){
                    int cid = index++;
                    ColumnCA sinkColumnCA = new ColumnCA(cid, fieldName, fieldName, fieldName, fieldName, fieldName, tableCA, trans);
                    columnCASMaps.put(cid, sinkColumnCA);
                    columnCASRel.add(new NodeRel(columnCA.getId(), cid));
                }
            }
        }
    }

    private void buildColumnCAFields(TableCA tableCA, Integer id, ColumnCA columnCA) {
        if (transMaps.get(id) instanceof OperatorTrans) {
            OperatorTrans trans = (OperatorTrans) transMaps.get(id);
            List<Field> selects = trans.getSelect();
            if (Asserts.isNotNull(selects)) {
                for (int i = 0; i < selects.size(); i++) {
                    String operation = selects.get(i).getFragment();
                    String alias = selects.get(i).getAlias();
                    searchSelect(tableCA, columnCA, trans, operation, alias);
                }
            }
            List<String> fields = trans.getFields();
            if (Asserts.isNotNull(fields)) {
                for (int i = 0; i < fields.size(); i++) {
                    String field = fields.get(i);
                    searchSelect(tableCA, columnCA, trans, field, field);
                }
            }
            if (trans.getParentId() != null) {
                buildColumnCAFields(tableCA, trans.getParentId(), columnCA);
            }
        }
    }

    private void searchSelect(TableCA tableCA, ColumnCA columnCA, OperatorTrans trans, String operation, String alias) {
        if (MapParseUtils.hasField(operation, columnCA.getAlias())) {
            boolean isHad = false;
            Integer cid = null;
            for (Map.Entry<Integer, ColumnCA> item : columnCASMaps.entrySet()) {
                ColumnCA columnCA1 = item.getValue();
                if (columnCA1.getTableCA().getId() == tableCA.getId() && columnCA1.getName().equals(alias)) {
                    isHad = true;
                    cid = columnCA1.getId();
                    break;
                }
            }
            if(columnCA.getId()==cid){
                return;
            }
            if (!isHad) {
                cid = index++;
                ColumnCA columnCA2 = new ColumnCA(cid, alias, alias, alias, alias, operation, tableCA, trans);
                columnCASMaps.put(cid, columnCA2);
                buildColumnCAFields(tableCA, trans.getParentId(), columnCA2);
            }
            columnCASRel.add(new NodeRel(columnCA.getId(), cid));
        }
    }

    private void searchColumnCAId(TableCA tableCA) {
        List<Integer> sufOnly = new ArrayList<>();
        for (NodeRel nodeRel : columnCASRel) {
            if (!sufOnly.contains(nodeRel.getSufId())) {
                sufOnly.add(nodeRel.getSufId());
            }
        }
        for (NodeRel nodeRel : columnCASRel) {
            if (sufOnly.contains(nodeRel.getPreId())) {
                sufOnly.remove(nodeRel.getPreId());
            }
        }
        List<Integer> preOnly = new ArrayList<>();
        for (NodeRel nodeRel : columnCASRel) {
            if (!preOnly.contains(nodeRel.getPreId())) {
                preOnly.add(nodeRel.getPreId());
            }
        }
        for (NodeRel nodeRel : columnCASRel) {
            if (preOnly.contains(nodeRel.getSufId())) {
                preOnly.remove(nodeRel.getSufId());
            }
        }
        for (int i = 0; i < sufOnly.size(); i++) {
            ColumnCA columnCA = columnCASMaps.get(sufOnly.get(i));
            List<String> fields = tableCA.getFields();
            for (int j = 0; j < fields.size(); j++) {
                if (columnCA.getAlias().equals(fields.get(j))) {
                    tableCA.getColumnCAIds().add(sufOnly.get(i));
                    columnCA.setTableId(tableCA.getId());
                    break;
                }
            }
        }
        sinkColumns = sufOnly;
        sourceColumns = preOnly;
    }

    private void chainRelation() {
        Set<NodeRel> nodeRelsChain = new HashSet<>();
        for (Integer item : sourceColumns) {
            nodeRelsChain.add(new NodeRel(item, getNextSuf(item)));
        }
        columnCASRelChain = nodeRelsChain;
    }

    private Integer getNextSuf(Integer sufId) {
        for (NodeRel nodeRel : columnCASRel) {
            if (nodeRel.getPreId() == sufId) {
                return getNextSuf(nodeRel.getSufId());
            }
        }
        return sufId;
    }

    public Map<Integer, Trans> getTransMaps() {
        return transMaps;
    }

    public void setTransMaps(Map<Integer, Trans> transMaps) {
        this.transMaps = transMaps;
    }

    public List<TableCA> getTableCAS() {
        return tableCAS;
    }

    public void setTableCAS(List<TableCA> tableCAS) {
        this.tableCAS = tableCAS;
    }

    public List<ColumnCA> getColumnCAS() {
        return columnCAS;
    }

    public void setColumnCAS(List<ColumnCA> columnCAS) {
        this.columnCAS = columnCAS;
    }

    public Map<Integer, ColumnCA> getColumnCASMaps() {
        return columnCASMaps;
    }

    public void setColumnCASMaps(Map<Integer, ColumnCA> columnCASMaps) {
        this.columnCASMaps = columnCASMaps;
    }

    public Set<NodeRel> getColumnCASRel() {
        return columnCASRel;
    }

    public void setColumnCASRel(Set<NodeRel> columnCASRel) {
        this.columnCASRel = columnCASRel;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Integer> getSinkColumns() {
        return sinkColumns;
    }

    public void setSinkColumns(List<Integer> sinkColumns) {
        this.sinkColumns = sinkColumns;
    }

    public List<Integer> getSourceColumns() {
        return sourceColumns;
    }

    public void setSourceColumns(List<Integer> sourceColumns) {
        this.sourceColumns = sourceColumns;
    }

    public Set<NodeRel> getColumnCASRelChain() {
        return columnCASRelChain;
    }

    public void setColumnCASRelChain(Set<NodeRel> columnCASRelChain) {
        this.columnCASRelChain = columnCASRelChain;
    }
}
