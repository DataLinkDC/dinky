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

    public LineageResult() {}

    public LineageResult(List<LineageTable> tables, List<LineageRelation> relations) {
        this.tables = tables;
        this.relations = relations;
    }

    public static LineageResult build(List<LineageTable> tables, List<LineageRelation> relations) {
        return new LineageResult(tables, relations);
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
