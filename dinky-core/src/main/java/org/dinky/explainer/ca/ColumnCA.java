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

package org.dinky.explainer.ca;

import java.util.List;

/**
 * ColumnCA
 *
 * @author wenmo
 * @since 2021/6/22
 */
public class ColumnCA implements ICA {

    private Integer id;
    private Integer tableId;
    private List<Integer> parentId;
    private String name;
    private String alias;
    private String operation;
    private String columnName;
    private String familyName;
    private String type;
    private String columnType;
    private TableCA tableCA;
    private String tableName;

    public ColumnCA(
            Integer id,
            String name,
            String alias,
            String columnName,
            String familyName,
            String operation,
            TableCA tableCA) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.columnName = columnName;
        this.familyName = familyName;
        this.operation = operation;
        this.tableCA = tableCA;
        this.tableId = tableCA.getId();
        this.tableName = tableCA.getName();
        this.type = tableCA.getType();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getParentId() {
        return parentId;
    }

    public void setParentId(List<Integer> parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TableCA getTableCA() {
        return tableCA;
    }

    public void setTableCA(TableCA tableCA) {
        this.tableCA = tableCA;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
