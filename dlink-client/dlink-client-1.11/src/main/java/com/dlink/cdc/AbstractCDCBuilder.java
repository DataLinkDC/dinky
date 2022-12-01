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

package com.dlink.cdc;

import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkParamConstant;
import com.dlink.model.FlinkCDCConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AbstractCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:28
 **/
public abstract class AbstractCDCBuilder {

    protected FlinkCDCConfig config;

    public AbstractCDCBuilder() {
    }

    public AbstractCDCBuilder(FlinkCDCConfig config) {
        this.config = config;
    }

    public FlinkCDCConfig getConfig() {
        return config;
    }

    public void setConfig(FlinkCDCConfig config) {
        this.config = config;
    }

    public List<String> getSchemaList() {
        List<String> schemaList = new ArrayList<>();
        String schema = getSchema();
        if (Asserts.isNotNullString(schema)) {
            String[] schemas = schema.split(FlinkParamConstant.SPLIT);
            Collections.addAll(schemaList, schemas);
        }
        List<String> tableList = getTableList();
        for (String tableName : tableList) {
            tableName = tableName.trim();
            if (Asserts.isNotNullString(tableName) && tableName.contains(".")) {
                String[] names = tableName.split("\\\\.");
                if (!schemaList.contains(names[0])) {
                    schemaList.add(names[0]);
                }
            }
        }
        return schemaList;
    }

    public List<String> getTableList() {
        List<String> tableList = new ArrayList<>();
        String table = config.getTable();
        if (Asserts.isNullString(table)) {
            return tableList;
        }
        String[] tables = table.split(FlinkParamConstant.SPLIT);
        Collections.addAll(tableList, tables);
        return tableList;
    }

    public String getSchemaFieldName() {
        return "schema";
    }

    public abstract String getSchema();
}
