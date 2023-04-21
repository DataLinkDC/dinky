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

package com.zdpx.coder.operator;

import java.util.ArrayList;
import java.util.List;

import com.zdpx.coder.graph.DataType;
import com.zdpx.coder.graph.PseudoData;

/** */
public class TableInfo implements PseudoData<TableInfo> {
    private String name;

    private List<Column> columns = new ArrayList<>();

    public TableInfo(String name) {
        this.name = name;
    }

    public TableInfo(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    private TableInfo(Builder builder) {
        setName(builder.name);
        setColumns(builder.columns);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    // region g/s

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public DataType getType() {
        return DataType.TABLE;
    }

    // endregion

    public static final class Builder {
        private String name;
        private List<Column> columns;

        private Builder() {}

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder columns(List<Column> val) {
            columns = val;
            return this;
        }

        public TableInfo build() {
            return new TableInfo(this);
        }
    }
}
