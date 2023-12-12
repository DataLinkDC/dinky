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

package org.dinky.trans.ddl;

import org.dinky.assertion.Asserts;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.ExtendOperation;
import org.dinky.trans.parse.SetSqlParseStrategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SetOperation
 *
 * @since 2021/10/21 19:56
 */
public class CustomSetOperation extends AbstractOperation implements ExtendOperation {

    private String key;
    private String value;

    public CustomSetOperation() {}

    public CustomSetOperation(String statement) {
        super(statement);
        parseConfig();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isValid() {
        return Asserts.isAllNotNullString(key, value);
    }

    private void parseConfig() {
        Map<String, List<String>> map = SetSqlParseStrategy.getInfo(statement);
        if (Asserts.isNotNullMap(map) && map.size() == 2) {
            key = StringUtils.join(map.get("SET"), ".");
            value = StringUtils.join(map.get("="), ",");
        }
    }

    @Override
    public Optional<? extends TableResult> execute(CustomTableEnvironment tEnv) {
        try {
            if (null != Class.forName("org.apache.log4j.Logger")) {
                tEnv.parseAndLoadConfiguration(statement, new HashMap<>());
                return Optional.of(TABLE_RESULT_OK);
            }
        } catch (ClassNotFoundException e) {
            logger.error("Class not found: org.apache.log4j.Logger");
        }
        if (Asserts.isAllNotNullString(key, value)) {
            Map<String, String> confMap = new HashMap<>();
            confMap.put(key, value);
            TableConfig config = tEnv.getConfig();
            config.addConfiguration(Configuration.fromMap(confMap));
            Configuration configuration = Configuration.fromMap(confMap);
            tEnv.getStreamExecutionEnvironment().getConfig().configure(configuration, null);
            config.addConfiguration(configuration);
        }
        return Optional.of(TABLE_RESULT_OK);
    }

    @Override
    public String asSummaryString() {
        return null;
    }
}
