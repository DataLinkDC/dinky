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

import org.dinky.assertion.Asserts;
import org.dinky.explainer.trans.Field;
import org.dinky.explainer.trans.OperatorTrans;
import org.dinky.explainer.trans.SinkTrans;
import org.dinky.explainer.trans.SourceTrans;
import org.dinky.explainer.trans.Trans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * TableCA
 *
 * @author wenmo
 * @since 2021/6/22
 **/
@Getter
@Setter
public class TableCA implements ICA {

    private Integer id;
    private Integer parentId;
    private String name;
    private String catalog;
    private String database;
    private String table;
    private String type;
    private List<String> fields;
    private List<String> useFields;
    private List<String> alias;
    private Set<Integer> columnCAIds = new HashSet<>();
    private Integer parallelism;

    private static final TableCA EMPTY = new TableCA();

    public TableCA() {
    }

    public TableCA(SourceTrans trans) {
        this.id = trans.getId();
        this.parentId = trans.getParentId();
        this.name = trans.getName();
        this.catalog = trans.getCatalog();
        this.database = trans.getDatabase();
        this.table = trans.getTable();
        this.fields = trans.getFields();
        this.useFields = trans.getFields();
        this.parallelism = trans.getParallelism();
        this.type = trans.getPact();
    }

    public TableCA(SinkTrans trans) {
        this.id = trans.getId();
        this.parentId = trans.getParentId();
        this.name = trans.getName();
        this.catalog = trans.getCatalog();
        this.database = trans.getDatabase();
        this.table = trans.getTable();
        this.fields = trans.getFields();
        this.useFields = trans.getFields();
        this.parallelism = trans.getParallelism();
        this.type = trans.getPact();
    }

    public TableCA(OperatorTrans trans) {
        List<String> tableList = trans.getTable();
        this.id = trans.getId();
        this.parentId = trans.getParentId();
        List<Field> select = trans.getSelect();
        List<String> fieldList = new ArrayList<>();
        for (Field field : select) {
            fieldList.add(field.getAlias());
        }
        this.fields = fieldList;
        this.useFields = fieldList;
        this.parallelism = trans.getParallelism();
        this.type = trans.getPact();
        if (tableList.size() > 0) {
            String tableStr = tableList.get(0);
            String[] strings = tableStr.split("\\.");
            if (strings.length > 2) {
                this.catalog = strings[0];
                this.database = strings[1];
                this.table = strings[2];
            } else if (strings.length == 2) {
                this.catalog = "default_catalog";
                this.database = strings[0];
                this.table = strings[1];
            } else if (strings.length == 1) {
                this.catalog = "default_catalog";
                this.database = "default_database";
                this.table = strings[0];
            }
        }
        this.name = this.catalog + "." + this.database + "." + this.table;
    }

    public static TableCA build(Trans trans) {
        if (trans instanceof SourceTrans) {
            return new TableCA((SourceTrans) trans);
        } else if (trans instanceof SinkTrans) {
            return new TableCA((SinkTrans) trans);
        } else if (trans instanceof OperatorTrans) {
            OperatorTrans operatorTrans = (OperatorTrans) trans;
            if (Asserts.isNotNullCollection(operatorTrans.getTable())) {
                return new TableCA(operatorTrans);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "TableCA{"
                + "id=" + id
                + ", parentId=" + parentId
                + ", name='" + name + '\''
                + ", catalog='" + catalog + '\''
                + ", database='" + database + '\''
                + ", table='" + table + '\''
                + ", fields=" + fields
                + ", useFields=" + useFields
                + ", parallelism=" + parallelism
                + '}';
    }

    @Override
    public String getTableName() {
        return this.table;
    }
}
