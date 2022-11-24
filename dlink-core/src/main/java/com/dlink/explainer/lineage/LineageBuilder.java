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

package com.dlink.explainer.lineage;

import com.dlink.explainer.ca.ColumnCAResult;
import com.dlink.explainer.ca.NodeRel;
import com.dlink.explainer.ca.TableCA;
import com.dlink.model.LineageRel;
import com.dlink.plus.FlinkSqlPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * LineageBuilder
 *
 * @author wenmo
 * @since 2022/3/15 22:58
 */
public class LineageBuilder {

    @Deprecated
    public static LineageResult getLineage(String statement) {
        return getLineage(statement, true);
    }

    @Deprecated
    public static LineageResult getLineage(String statement, boolean statementSet) {
        FlinkSqlPlus plus = FlinkSqlPlus.build(statementSet);
        List<ColumnCAResult> columnCAResults = plus.explainSqlColumnCA(statement);
        List<LineageTable> tables = new ArrayList<>();
        List<LineageRelation> relations = new ArrayList<>();
        int index = 0;
        for (ColumnCAResult item : columnCAResults) {
            for (TableCA tableCA : item.getTableCAS()) {
                tables.add(LineageTable.build(tableCA));
            }
            Set<String> keySet = new HashSet<>();
            for (NodeRel nodeRel : item.getColumnCASRelChain()) {
                if (item.getColumnCASMaps().containsKey(nodeRel.getPreId())
                        && item.getColumnCASMaps().containsKey(nodeRel.getSufId())
                        && !item.getColumnCASMaps().get(nodeRel.getPreId()).getTableId()
                                .equals(item.getColumnCASMaps().get(nodeRel.getSufId()).getTableId())) {
                    String key = item.getColumnCASMaps().get(nodeRel.getPreId()).getTableId().toString() + "@"
                            + item.getColumnCASMaps().get(nodeRel.getSufId()).getTableId().toString() + "@"
                            + item.getColumnCASMaps().get(nodeRel.getPreId()).getName() + "@"
                            + item.getColumnCASMaps().get(nodeRel.getSufId()).getName();
                    // 去重
                    if (!keySet.contains(key)) {
                        index++;
                        relations.add(LineageRelation.build(index + "",
                                item.getColumnCASMaps().get(nodeRel.getPreId()).getTableId().toString(),
                                item.getColumnCASMaps().get(nodeRel.getSufId()).getTableId().toString(),
                                item.getColumnCASMaps().get(nodeRel.getPreId()).getName(),
                                item.getColumnCASMaps().get(nodeRel.getSufId()).getName()));
                        keySet.add(key);
                    }
                }
            }
        }
        // 获取重复表集合
        List<List<LineageTable>> repeatTablesList = new ArrayList<>();
        for (int i = 0; i < tables.size() - 1; i++) {
            List<LineageTable> repeatTables = new ArrayList<>();
            for (int j = i + 1; j < tables.size(); j++) {
                if (tables.get(i).getName().equals(tables.get(j).getName())) {
                    repeatTables.add(tables.get(j));
                }
            }
            if (repeatTables.size() > 0) {
                repeatTables.add(tables.get(i));
                repeatTablesList.add(repeatTables);
            }
        }
        // 重复表合并
        Map<String, String> correctTableIdMap = new HashMap<>();
        for (List<LineageTable> tableList : repeatTablesList) {
            LineageTable newTable = new LineageTable();
            Set<String> columnKeySet = new HashSet<>();
            for (LineageTable table : tableList) {
                if (newTable.getId() == null || newTable.getName() == null) {
                    newTable.setId(table.getId());
                    newTable.setName(table.getName());
                    newTable.setColumns(new ArrayList<>());
                }
                for (LineageColumn column : table.getColumns()) {
                    String key = column.getName() + "@&" + column.getTitle();
                    if (!columnKeySet.contains(key)) {
                        newTable.getColumns().add(column);
                        columnKeySet.add(key);
                    }
                }
                correctTableIdMap.put(table.getId(), newTable.getId());
                tables.remove(table);
            }
            tables.add(newTable);
        }
        // 关系中id重新指向
        for (LineageRelation relation : relations) {
            if (correctTableIdMap.containsKey(relation.getSrcTableId())) {
                relation.setSrcTableId(correctTableIdMap.get(relation.getSrcTableId()));
            }
            if (correctTableIdMap.containsKey(relation.getTgtTableId())) {
                relation.setTgtTableId(correctTableIdMap.get(relation.getTgtTableId()));
            }
        }
        return LineageResult.build(tables, relations);
    }

    public static LineageResult getColumnLineageByLogicalPlan(String statement) {
        FlinkSqlPlus plus = FlinkSqlPlus.build(false);
        List<LineageRel> lineageRelList = plus.getLineage(statement);
        List<LineageRelation> relations = new ArrayList<>();
        Map<String, LineageTable> tableMap = new HashMap<>();
        int tableIndex = 1;
        int relIndex = 1;
        for (LineageRel lineageRel : lineageRelList) {
            String sourceTablePath = lineageRel.getSourceTablePath();
            String sourceTableId = null;
            String targetTableId = null;
            if (tableMap.containsKey(sourceTablePath)) {
                LineageTable lineageTable = tableMap.get(sourceTablePath);
                LineageColumn lineageColumn =
                        LineageColumn.build(lineageRel.getSourceColumn(), lineageRel.getSourceColumn());
                if (!lineageTable.getColumns().contains(lineageColumn)) {
                    lineageTable.getColumns().add(lineageColumn);
                }
                sourceTableId = lineageTable.getId();
            } else {
                tableIndex++;
                LineageTable lineageTable = LineageTable.build(tableIndex + "", sourceTablePath);
                lineageTable.getColumns()
                        .add(LineageColumn.build(lineageRel.getSourceColumn(), lineageRel.getSourceColumn()));
                tableMap.put(sourceTablePath, lineageTable);
                sourceTableId = lineageTable.getId();
            }
            String targetTablePath = lineageRel.getTargetTablePath();
            if (tableMap.containsKey(targetTablePath)) {
                LineageTable lineageTable = tableMap.get(targetTablePath);
                LineageColumn lineageColumn =
                        LineageColumn.build(lineageRel.getTargetColumn(), lineageRel.getTargetColumn());
                if (!lineageTable.getColumns().contains(lineageColumn)) {
                    lineageTable.getColumns().add(lineageColumn);
                }
                targetTableId = lineageTable.getId();
            } else {
                tableIndex++;
                LineageTable lineageTable = LineageTable.build(tableIndex + "", targetTablePath);
                lineageTable.getColumns()
                        .add(LineageColumn.build(lineageRel.getTargetColumn(), lineageRel.getTargetColumn()));
                tableMap.put(targetTablePath, lineageTable);
                targetTableId = lineageTable.getId();
            }
            LineageRelation lineageRelation = LineageRelation.build(sourceTableId, targetTableId,
                    lineageRel.getSourceColumn(), lineageRel.getTargetColumn());
            if (!relations.contains(lineageRelation)) {
                relIndex++;
                lineageRelation.setId(relIndex + "");
                relations.add(lineageRelation);
            }
        }
        List<LineageTable> tables = new ArrayList<>(tableMap.values());
        return LineageResult.build(tables, relations);
    }

}
