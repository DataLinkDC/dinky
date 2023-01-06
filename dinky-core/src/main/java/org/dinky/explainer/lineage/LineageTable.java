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

import com.dlink.explainer.ca.TableCA;

import java.util.ArrayList;
import java.util.List;

/**
 * LineageTable
 *
 * @author wenmo
 * @since 2022/3/15 22:55
 */
public class LineageTable {
    private String id;
    private String name;
    private List<LineageColumn> columns;

    public LineageTable() {
    }

    public LineageTable(String id, String name) {
        this.id = id;
        this.name = name;
        this.columns = new ArrayList<>();
    }

    public static LineageTable build(String id, String name) {
        return new LineageTable(id, name);
    }

    public static LineageTable build(TableCA tableCA) {
        LineageTable lineageTable = new LineageTable();
        lineageTable.setId(tableCA.getId().toString());
        lineageTable.setName(tableCA.getTableName());
        List<LineageColumn> columnList = new ArrayList<>();
        for (String columnName : tableCA.getFields()) {
            columnList.add(LineageColumn.build(columnName, columnName));
        }
        lineageTable.setColumns(columnList);
        return lineageTable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LineageColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<LineageColumn> columns) {
        this.columns = columns;
    }
}
