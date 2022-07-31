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

import com.dlink.explainer.lineage.LineageColumnGenerator;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ColumnCAResult
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class ColumnCAResult {
    private List<TableCA> tableCAS;
    private Map<Integer, ColumnCA> columnCASMaps;
    private Set<NodeRel> columnCASRel;
    private Set<NodeRel> columnCASRelChain;
    private List<Integer> sinkColumns;
    private List<Integer> sourColumns;

    public ColumnCAResult(LineageColumnGenerator generator) {
        this.tableCAS = generator.getTableCAS();
        this.columnCASMaps = generator.getColumnCASMaps();
        this.columnCASRel = generator.getColumnCASRel();
        this.columnCASRelChain = generator.getColumnCASRelChain();
        this.sinkColumns = generator.getSinkColumns();
        this.sourColumns = generator.getSourceColumns();
    }

    public List<TableCA> getTableCAS() {
        return tableCAS;
    }

    public void setTableCAS(List<TableCA> tableCAS) {
        this.tableCAS = tableCAS;
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

    public Set<NodeRel> getColumnCASRelChain() {
        return columnCASRelChain;
    }

    public void setColumnCASRelChain(Set<NodeRel> columnCASRelChain) {
        this.columnCASRelChain = columnCASRelChain;
    }

    public List<Integer> getSinkColumns() {
        return sinkColumns;
    }

    public void setSinkColumns(List<Integer> sinkColumns) {
        this.sinkColumns = sinkColumns;
    }

    public List<Integer> getSourColumns() {
        return sourColumns;
    }

    public void setSourColumns(List<Integer> sourColumns) {
        this.sourColumns = sourColumns;
    }
}
