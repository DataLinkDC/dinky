/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.explainer.lineage;

import org.dinky.assertion.Asserts;
import org.dinky.explainer.ca.ColumnCA;
import org.dinky.explainer.ca.NodeRel;
import org.dinky.explainer.ca.TableCA;
import org.dinky.explainer.trans.Field;
import org.dinky.explainer.trans.OperatorTrans;
import org.dinky.explainer.trans.Predecessor;
import org.dinky.explainer.trans.SinkTrans;
import org.dinky.explainer.trans.Trans;
import org.dinky.utils.MapParseUtils;

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
 */
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
        for (TableCA tableCA : tableCAS) {
            for (String fieldName : tableCA.getFields()) {
                int id = index++;
                ColumnCA columnCA =
                        new ColumnCA(
                                id, fieldName, fieldName, fieldName, fieldName, fieldName, tableCA);
                columnCASMaps.put(id, columnCA);
                columnCAS.add(columnCA);
                buildColumnCAFields(
                        tableCA,
                        tableCA.getParentId(),
                        tableCA.getId(),
                        columnCA,
                        new ArrayList<>());
            }
            /*
             * for (ColumnCA columnCA : columnCAS) { if (columnCA.getTableCA().getId() == tableCA.getId()) {
             * buildColumnCAFields(tableCA, tableCA.getParentId(), tableCA.getId() columnCA, new ArrayList<>()); } }
             */
        }
        for (Map.Entry<Integer, Trans> entry : transMaps.entrySet()) {
            Trans trans = entry.getValue();
            if (trans instanceof SinkTrans) {
                TableCA tableCA = new TableCA((SinkTrans) trans);
                matchSinkField(tableCA);
                searchColumnCAId(tableCA);
            }
        }
        chainRelation();
    }

    private void matchSinkField(TableCA tableCA) {
        for (ColumnCA columnCA : columnCAS) {
            if (columnCA.getTableId().equals(tableCA.getId())) {
                continue;
            }
            for (String fieldName : tableCA.getFields()) {
                if (columnCA.getName().equals(fieldName)) {
                    int cid = index++;
                    ColumnCA sinkColumnCA =
                            new ColumnCA(
                                    cid, fieldName, fieldName, fieldName, fieldName, fieldName,
                                    tableCA);
                    columnCASMaps.put(cid, sinkColumnCA);
                    columnCASRel.add(new NodeRel(columnCA.getId(), cid));
                }
            }
        }
    }

    private void buildColumnCAFields(
            TableCA tableCA,
            Integer id,
            Integer preId,
            ColumnCA columnCA,
            List<Field> ignoreSelects) {
        if (transMaps.get(id) instanceof OperatorTrans) {
            if (tableCA.getId().equals(id)) {
                return;
            }
            OperatorTrans trans = (OperatorTrans) transMaps.get(id);
            List<Field> selects = trans.getSelect();
            List<Field> targetSelects = new ArrayList<>();
            if (trans.getPredecessors().size() > 1) {
                int predecessorsIndex = 0;
                for (Predecessor predecessor : trans.getPredecessors()) {
                    if (!predecessor.getId().equals(preId)) {
                        predecessorsIndex = predecessorsIndex + 1;
                    } else {
                        break;
                    }
                }
                if (predecessorsIndex == 0) {
                    for (int i = 0; i < selects.size(); i++) {
                        if (tableCA.getUseFields().contains(selects.get(i).getAlias())) {
                            if (targetSelects.size() == 0) {
                                targetSelects.add(selects.get(i));
                            } else if (tableCA.getUseFields()
                                            .indexOf(
                                                    targetSelects
                                                            .get(targetSelects.size() - 1)
                                                            .getAlias())
                                    < tableCA.getUseFields().indexOf(selects.get(i).getAlias())) {
                                targetSelects.add(selects.get(i));
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    for (int i = selects.size() - 1; i >= 0; i--) {
                        if (tableCA.getUseFields().contains(selects.get(i).getAlias())) {
                            if (targetSelects.size() == 0) {
                                targetSelects.add(selects.get(i));
                            } else if (tableCA.getUseFields()
                                            .indexOf(
                                                    targetSelects
                                                            .get(targetSelects.size() - 1)
                                                            .getAlias())
                                    > tableCA.getUseFields().indexOf(selects.get(i).getAlias())) {
                                targetSelects.add(selects.get(i));
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            } else {
                if (Asserts.isNotNullCollection(trans.getTable())) {
                    for (int i = 0; i < selects.size(); i++) {
                        if (tableCA.getUseFields().contains(selects.get(i).getAlias())) {
                            if (targetSelects.size() == 0) {
                                targetSelects.add(selects.get(i));
                            } else if (tableCA.getUseFields()
                                            .indexOf(
                                                    targetSelects
                                                            .get(targetSelects.size() - 1)
                                                            .getAlias())
                                    < tableCA.getUseFields().indexOf(selects.get(i).getAlias())) {
                                targetSelects.add(selects.get(i));
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    targetSelects = selects;
                }
            }
            if (Asserts.isNotNull(targetSelects)) {
                if (selects.size() != targetSelects.size()) {
                    for (int i = 0; i < selects.size(); i++) {
                        if (!targetSelects.contains(selects.get(i))) {
                            ignoreSelects.add(selects.get(i));
                        }
                    }
                }
                for (int i = 0; i < targetSelects.size(); i++) {
                    String operation = targetSelects.get(i).getFragment();
                    String alias = targetSelects.get(i).getAlias();
                    searchSelect(tableCA, columnCA, trans, operation, alias, ignoreSelects);
                }
            }
            List<String> fields = trans.getFields();
            if (Asserts.isNotNull(fields)) {
                for (int i = 0; i < fields.size(); i++) {
                    String field = fields.get(i);
                    searchSelect(tableCA, columnCA, trans, field, field, ignoreSelects);
                }
            }
            if (trans.getParentId() != null) {
                buildColumnCAFields(
                        tableCA, trans.getParentId(), trans.getId(), columnCA, ignoreSelects);
            }
        }
    }

    private void searchSelect(
            TableCA tableCA,
            ColumnCA columnCA,
            OperatorTrans trans,
            String operation,
            String alias,
            List<Field> ignoreSelects) {
        if (MapParseUtils.hasField(operation, columnCA.getAlias())) {
            for (Field field : ignoreSelects) {
                if (field.getAlias().equals(columnCA.getAlias())) {
                    return;
                }
            }
            boolean isHad = false;
            Integer cid = null;
            for (Map.Entry<Integer, ColumnCA> item : columnCASMaps.entrySet()) {
                ColumnCA columnCA1 = item.getValue();
                if (columnCA1.getTableCA().getId().equals(tableCA.getId())
                        && columnCA1.getName().equals(alias)) {
                    isHad = true;
                    cid = columnCA1.getId();
                    break;
                }
            }
            if (columnCA.getId().equals(cid)) {
                return;
            }
            if (!isHad) {
                cid = index++;
                ColumnCA columnCA2 =
                        new ColumnCA(cid, alias, alias, alias, alias, operation, tableCA);
                columnCASMaps.put(cid, columnCA2);
                buildColumnCAFields(
                        tableCA, trans.getParentId(), trans.getId(), columnCA2, ignoreSelects);
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
        for (Integer item : sourceColumns) {
            buildSinkSuf(item, item);
        }
    }

    private void buildSinkSuf(Integer preId, Integer sourcePreId) {
        for (NodeRel nodeRel : columnCASRel) {
            if (nodeRel.getPreId().equals(preId)) {
                Integer nextSufId = nodeRel.getSufId();
                if (sinkColumns.contains(nextSufId)) {
                    columnCASRelChain.add(new NodeRel(sourcePreId, nextSufId));
                    continue;
                }
                buildSinkSuf(nextSufId, sourcePreId);
            }
        }
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
