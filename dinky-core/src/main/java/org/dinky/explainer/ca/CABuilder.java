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

package com.dlink.explainer.ca;

import com.dlink.plus.FlinkSqlPlus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * CABuilder
 *
 * @author wenmo
 * @since 2021/6/23 11:03
 **/
@Deprecated
public class CABuilder {

    public static List<TableCANode> getOneTableCAByStatement(String statement) {
        List<TableCANode> tableCANodes = new ArrayList<>();
        FlinkSqlPlus plus = FlinkSqlPlus.build();
        List<TableCAResult> results = plus.generateTableCA(statement);
        for (int j = 0; j < results.size(); j++) {
            TableCAResult result = results.get(j);
            TableCANode node = new TableCANode();
            TableCA sinkTableCA = (TableCA) result.getSinkTableCA();
            node.setName(sinkTableCA.getTableName());
            List<TableCANode> children = new ArrayList<>();
            for (int k = 0; k < result.getSourceTableCAS().size(); k++) {
                children.add(new TableCANode(result.getSourceTableCAS().get(k).getTableName()));
            }
            node.setChildren(children);
            tableCANodes.add(node);
        }
        return tableCANodes;
    }

    public static List<TableCANode> getOneTableColumnCAByStatement(String statement) {
        List<TableCANode> tableCANodes = new ArrayList<>();
        FlinkSqlPlus plus = FlinkSqlPlus.build();
        int id = 1;
        List<TableCAResult> results = plus.explainSqlTableColumnCA(statement);
        for (int j = 0; j < results.size(); j++) {
            TableCAResult result = results.get(j);
            TableCA sinkTableCA = (TableCA) result.getSinkTableCA();
            TableCANode node = new TableCANode(id++, sinkTableCA.getTableName(), sinkTableCA.getFields());
            List<TableCANode> children = new ArrayList<>();
            for (int k = 0; k < result.getSourceTableCAS().size(); k++) {
                TableCA tableCA = (TableCA) result.getSourceTableCAS().get(k);
                children.add(new TableCANode(id++, tableCA.getTableName(), tableCA.getFields()));
            }
            node.setChildren(children);
            tableCANodes.add(node);
        }
        return tableCANodes;
    }

    @Deprecated
    public static List<ColumnCANode> getColumnCAByStatement(String statement) {
        List<ColumnCANode> columnCANodes = new ArrayList<>();
        FlinkSqlPlus plus = FlinkSqlPlus.build();
        List<ColumnCAResult> columnCAResults = plus.explainSqlColumnCA(statement);
        for (int j = 0; j < columnCAResults.size(); j++) {
            ColumnCAResult result = columnCAResults.get(j);
            List<Integer> sinkColumns = result.getSinkColumns();
            for (int k = 0; k < sinkColumns.size(); k++) {
                ColumnCA columnCA = (ColumnCA) result.getColumnCASMaps().get(sinkColumns.get(k));
                ColumnCANode node = new ColumnCANode();
                node.setName(columnCA.getAlias());
                node.setType(columnCA.getType());
                node.setTitle(columnCA.getAlias());
                node.setOperation(columnCA.getOperation());
                List<ColumnCANode> children = new ArrayList<>();
                buildColumnCANodeChildren(children, result, sinkColumns.get(k), columnCA.getOperation());
                node.setChildren(children);
                columnCANodes.add(node);
            }
        }
        return columnCANodes;
    }

    private static void buildColumnCANodeChildren(List<ColumnCANode> children, ColumnCAResult result, Integer columnId, String operation) {
        Set<NodeRel> columnCASRel = result.getColumnCASRel();
        boolean hasChildren = false;
        for (NodeRel nodeRel : columnCASRel) {
            if (columnId.equals(nodeRel.getSufId())) {
                ColumnCA childca = (ColumnCA) result.getColumnCASMaps().get(nodeRel.getPreId());
                //operation = operation.replaceAll(childca.getAlias().replaceAll("\\$","\\\\$"),childca.getOperation());
                operation = operation.replaceAll(childca.getAlias()
                        .replaceAll("\\)", ""), childca.getOperation());
                buildColumnCANodeChildren(children, result, nodeRel.getPreId(), operation);
                hasChildren = true;
            }
        }
        if (!hasChildren) {
            ColumnCA columnCA = (ColumnCA) result.getColumnCASMaps().get(columnId);
            if (result.getSourColumns().contains(columnCA.getId())) {
                ColumnCANode columnCANode = new ColumnCANode();
                columnCANode.setName(columnCA.getName());
                columnCANode.setTitle(columnCA.getName());
                columnCANode.setOperation(operation);
                children.add(columnCANode);
            }
        }
    }
}
