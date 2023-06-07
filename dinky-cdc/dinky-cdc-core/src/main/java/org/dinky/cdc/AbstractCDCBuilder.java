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

package org.dinky.cdc;

import org.dinky.assertion.Asserts;
import org.dinky.constant.ClientConstant;
import org.dinky.constant.FlinkParamConstant;
import org.dinky.data.model.FlinkCDCConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCDCBuilder implements CDCBuilder {

    protected FlinkCDCConfig config;

    protected AbstractCDCBuilder() {}

    protected AbstractCDCBuilder(FlinkCDCConfig config) {
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

        getTableList().stream()
                .map(String::trim)
                .filter(tableName -> Asserts.isNotNullString(tableName) && tableName.contains("."))
                .map(tableName -> tableName.split("\\\\."))
                .filter(names -> !schemaList.contains(names[0]))
                .forEach(names -> schemaList.add(names[0]));
        return schemaList;
    }

    public List<String> getTableList() {
        List<String> tableList = new ArrayList<>();
        String table = config.getTable();
        if (Asserts.isNullString(table)) {
            return tableList;
        }

        Collections.addAll(tableList, table.split(FlinkParamConstant.SPLIT));
        return tableList;
    }

    public String getSchemaFieldName() {
        return "schema";
    }

    public Map<String, Map<String, String>> parseMetaDataConfigs() {
        Map<String, Map<String, String>> allConfigMap = new HashMap<>();
        for (String schema : getSchemaList()) {
            String url = generateUrl(schema);
            allConfigMap.put(schema, parseMetaDataSingleConfig(url));
        }
        return allConfigMap;
    }

    public Map<String, String> parseMetaDataSingleConfig(String url) {
        Map<String, String> configMap = new HashMap<>();
        configMap.put(ClientConstant.METADATA_NAME, url);
        configMap.put(ClientConstant.METADATA_URL, url);
        configMap.put(ClientConstant.METADATA_TYPE, getMetadataType());
        configMap.put(ClientConstant.METADATA_USERNAME, config.getUsername());
        configMap.put(ClientConstant.METADATA_PASSWORD, config.getPassword());
        return configMap;
    }

    public abstract String getSchema();

    protected abstract String getMetadataType();

    protected abstract String generateUrl(String schema);
}
