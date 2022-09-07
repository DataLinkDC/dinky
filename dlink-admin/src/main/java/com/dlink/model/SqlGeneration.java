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

package com.dlink.model;

/**
 * SqlGeneration
 *
 * @author wenmo
 * @since 2022/1/29 16:13
 */
public class SqlGeneration {
    private String flinkSqlCreate;
    private String sqlSelect;
    private String sqlCreate;

    public SqlGeneration() {
    }

    public SqlGeneration(String flinkSqlCreate, String sqlSelect, String sqlCreate) {
        this.flinkSqlCreate = flinkSqlCreate;
        this.sqlSelect = sqlSelect;
        this.sqlCreate = sqlCreate;
    }

    public String getFlinkSqlCreate() {
        return flinkSqlCreate;
    }

    public void setFlinkSqlCreate(String flinkSqlCreate) {
        this.flinkSqlCreate = flinkSqlCreate;
    }

    public String getSqlSelect() {
        return sqlSelect;
    }

    public void setSqlSelect(String sqlSelect) {
        this.sqlSelect = sqlSelect;
    }

    public String getSqlCreate() {
        return sqlCreate;
    }

    public void setSqlCreate(String sqlCreate) {
        this.sqlCreate = sqlCreate;
    }
}
