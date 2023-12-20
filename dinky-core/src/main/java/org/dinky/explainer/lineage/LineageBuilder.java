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

import org.dinky.data.model.LineageRel;
import org.dinky.executor.ExecutorFactory;
import org.dinky.explainer.Explainer;
import org.dinky.job.JobManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LineageBuilder
 *
 * @since 2022/3/15 22:58
 */
public class LineageBuilder {

    public static LineageResult getColumnLineageByLogicalPlan(String statement) {
        Explainer explainer = new Explainer(ExecutorFactory.getDefaultExecutor(), false, new JobManager());
        List<LineageRel> lineageRelList = explainer.getLineage(statement);
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
                lineageTable
                        .getColumns()
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
                lineageTable
                        .getColumns()
                        .add(LineageColumn.build(lineageRel.getTargetColumn(), lineageRel.getTargetColumn()));
                tableMap.put(targetTablePath, lineageTable);
                targetTableId = lineageTable.getId();
            }
            LineageRelation lineageRelation = LineageRelation.build(
                    sourceTableId, targetTableId, lineageRel.getSourceColumn(), lineageRel.getTargetColumn());
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
