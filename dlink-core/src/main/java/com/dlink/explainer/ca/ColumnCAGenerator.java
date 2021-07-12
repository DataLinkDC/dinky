package com.dlink.explainer.ca;


import com.dlink.explainer.trans.Field;
import com.dlink.explainer.trans.OperatorTrans;
import com.dlink.explainer.trans.SinkTrans;
import com.dlink.explainer.trans.SourceTrans;
import com.dlink.explainer.trans.Trans;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ColumnCAGenerator
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class ColumnCAGenerator implements CAGenerator {
    private List<Trans> transList;
    private Map<Integer, Trans> transMaps;
    private Set<Integer> parentIdSet;
    private List<ICA> sourceTableCAS = new ArrayList<>();
    private List<ICA> columnCAS = new ArrayList<>();
    private Map<Integer, ICA> columnCASMaps;
    private Set<NodeRel> columnCASRel;
    private ICA sinkTableCA = null;
    private String sinkTableName;
    private Integer index = 0;
    private List<Integer> sinkColumns;
    private List<Integer> sourceColumns;

    public ColumnCAGenerator(List<Trans> transList) {
        this.transList = transList;
        this.transMaps = new HashMap<>();
        this.parentIdSet = new HashSet<>();
        this.columnCASMaps = new HashMap<>();
        this.columnCASRel = new HashSet<>();
        for (int i = 0; i < transList.size(); i++) {
            this.transMaps.put(transList.get(i).getId(), transList.get(i));
            if (transList.get(i).getParentId() != null) {
                parentIdSet.add(transList.get(i).getParentId());
            }
        }
    }

    @Override
    public void translate() {
        for (int i = 0; i < transList.size(); i++) {
            if (transList.get(i) instanceof SourceTrans) {
                TableCA tableCA = new TableCA((SourceTrans) transList.get(i));
                paddingFields(tableCA);
                List<String> sourceFields = new ArrayList<>();
                CollectionUtils.addAll(sourceFields, new Object[tableCA.getFields().size()]);
                Collections.copy(sourceFields, tableCA.getFields());
                for (int j = 0; j < sourceFields.size(); j++) {
                    String fieldName = sourceFields.get(j);
                    Integer id = index++;
                    ColumnCA columnCA = new ColumnCA(id, fieldName, fieldName, fieldName, fieldName,fieldName, tableCA,transList.get(i));
                    this.columnCASMaps.put(id, columnCA);
                    this.columnCAS.add(columnCA);
                }
                for (int j = 0; j < this.columnCAS.size(); j++) {
                    ColumnCA columnCA = (ColumnCA) this.columnCAS.get(j);
                    if (columnCA.getTableCA().getId() == tableCA.getId()) {
                        buildColumnCAFields(tableCA, tableCA.getParentId(), columnCA);
                    }
                }
            } else if (transList.get(i) instanceof SinkTrans) {
                TableCA tableCA = new TableCA((SinkTrans) transList.get(i));
                searchColumnCAId(tableCA);
                this.sinkTableCA = tableCA;
                this.sinkTableName = tableCA.getName();
            }
        }
    }

    private void searchColumnCAId(TableCA tableCA){
        List<Integer> sufOnly = new ArrayList<>();
        for (NodeRel nodeRel : this.columnCASRel) {
            if(!sufOnly.contains(nodeRel.getSufId())) {
                sufOnly.add(nodeRel.getSufId());
            }
        }
        /*for (NodeRel nodeRel : this.columnCASRel) {
            if(sufOnly.contains(nodeRel.getPreId())) {
                sufOnly.remove(nodeRel.getPreId());
            }
        }*/
        List<Integer> preOnly = new ArrayList<>();
        for (NodeRel nodeRel : this.columnCASRel) {
            if(!preOnly.contains(nodeRel.getPreId())) {
                preOnly.add(nodeRel.getPreId());
            }
        }
        /*for (NodeRel nodeRel : this.columnCASRel) {
            if(preOnly.contains(nodeRel.getSufId())) {
                preOnly.remove(nodeRel.getSufId());
            }
        }*/
        for (int i = 0; i < sufOnly.size(); i++) {
            ColumnCA columnCA = (ColumnCA)this.columnCASMaps.get(sufOnly.get(i));
            List<String> fields = tableCA.getFields();
            for (int j = 0; j < fields.size(); j++) {
                if(columnCA.getAlias().equals(fields.get(j))){
                    tableCA.getColumnCAIds().add(sufOnly.get(i));
                    break;
                }
            }
        }
        this.sinkColumns = sufOnly;
        this.sourceColumns = preOnly;
    }

    private void paddingFields(TableCA tableCA) {
        for (int i = 0; i < this.sourceTableCAS.size(); i++) {
            if (this.sourceTableCAS.get(i) instanceof TableCA) {
                TableCA sourceTableCA = (TableCA) this.sourceTableCAS.get(i);
                if (sourceTableCA.getId() == tableCA.getId()) {
                    tableCA.setFields(sourceTableCA.getFields());
                }
            }
        }
    }

    private void buildColumnCAFields(TableCA tableCA, Integer id, ColumnCA columnCA) {
        if (transMaps.get(id) instanceof OperatorTrans) {
            OperatorTrans trans = (OperatorTrans) transMaps.get(id);
            List<Field> selects = trans.getSelect();
            if (selects != null && selects.size() > 0) {
                for (int i = 0; i < selects.size(); i++) {
                    String operation = selects.get(i).getFragment();
                    String alias = selects.get(i).getAlias();
                    searchSelect(tableCA, columnCA, trans, operation, alias);
                }
            }
            if (trans.getParentId() != null) {
                buildColumnCAFields(tableCA, trans.getParentId(), columnCA);
            }
        }
    }

    private void searchSelect(TableCA tableCA, ColumnCA columnCA, OperatorTrans trans, String operation, String alias) {
        if (operation.contains(" " + columnCA.getAlias() + " ") ||
                operation.contains("(" + columnCA.getAlias() + " ") ||
                operation.contains(" " + columnCA.getAlias() + ")")) {
            boolean isHad = false;
            Integer cid = null;
            for (int j = 0; j < this.columnCAS.size(); j++) {
                ColumnCA columnCA1 = (ColumnCA) this.columnCAS.get(j);
                if (columnCA1.getTableCA().getId() == tableCA.getId() &&
                        columnCA1.getName().equals(operation)) {
                    isHad = true;
                    cid = columnCA1.getId();
                    break;
                }
            }
            if (!isHad) {
                cid = index++;
                String columnOperation = operation.replaceAll(" " + columnCA.getAlias() + " "," " + columnCA.getOperation() + " ")
                .replaceAll("\\(" + columnCA.getAlias() + " "," " + columnCA.getOperation() + " ")
                .replaceAll(" " + columnCA.getAlias() + "\\)"," " + columnCA.getOperation() + " ");
                ColumnCA columnCA2 = new ColumnCA(cid, operation, alias, operation, operation,columnOperation, tableCA,trans);
                this.columnCASMaps.put(cid, columnCA2);
                this.columnCAS.add(columnCA2);
                buildColumnCAFields(tableCA, trans.getParentId(), columnCA2);
            }
            this.columnCASRel.add(new NodeRel(columnCA.getId(), cid));
        }
    }

    public List<Trans> getTransList() {
        return transList;
    }

    public void setTransList(List<Trans> transList) {
        this.transList = transList;
    }

    public Map<Integer, Trans> getTransMaps() {
        return transMaps;
    }

    public void setTransMaps(Map<Integer, Trans> transMaps) {
        this.transMaps = transMaps;
    }

    public Set<Integer> getParentIdSet() {
        return parentIdSet;
    }

    public void setParentIdSet(Set<Integer> parentIdSet) {
        this.parentIdSet = parentIdSet;
    }

    public List<ICA> getSourceTableCAS() {
        return sourceTableCAS;
    }

    public void setSourceTableCAS(List<ICA> sourceTableCAS) {
        this.sourceTableCAS = sourceTableCAS;
    }

    public List<ICA> getColumnCAS() {
        return columnCAS;
    }

    public void setColumnCAS(List<ICA> columnCAS) {
        this.columnCAS = columnCAS;
    }

    public ICA getSinkTableCA() {
        return sinkTableCA;
    }

    public void setSinkTableCA(ICA sinkTableCA) {
        this.sinkTableCA = sinkTableCA;
    }

    public String getSinkTableName() {
        return sinkTableName;
    }

    public void setSinkTableName(String sinkTableName) {
        this.sinkTableName = sinkTableName;
    }

    public Map<Integer, ICA> getColumnCASMaps() {
        return columnCASMaps;
    }

    public void setColumnCASMaps(Map<Integer, ICA> columnCASMaps) {
        this.columnCASMaps = columnCASMaps;
    }

    public Set<NodeRel> getColumnCASRel() {
        return columnCASRel;
    }

    public void setColumnCASRel(Set<NodeRel> columnCASRel) {
        this.columnCASRel = columnCASRel;
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
}
