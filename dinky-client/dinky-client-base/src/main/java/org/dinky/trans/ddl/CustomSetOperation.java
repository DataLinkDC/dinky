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

import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.command.ResetOperation;
import org.apache.flink.table.operations.command.SetOperation;
import org.dinky.assertion.Asserts;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.ExtendOperation;
import org.dinky.trans.parse.SetSqlParseStrategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableResult;

import java.util.Collections;
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
                parseAndLoadConfiguration(statement, new HashMap<>(), tEnv);
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

    public boolean parseAndLoadConfiguration(String statement, Map<String, Object> setMap, CustomTableEnvironment tEnv) {
        List<Operation> operations = tEnv.getParser().parse(statement);
        for (Operation operation : operations) {
            if (operation instanceof SetOperation) {
                callSet((SetOperation) operation, tEnv, setMap);
                return true;
            } else if (operation instanceof ResetOperation) {
                callReset((ResetOperation) operation, tEnv, setMap);
                return true;
            } else if (operation instanceof CustomSetOperation) {
                CustomSetOperation customSetOperation = (CustomSetOperation) operation;
                if (customSetOperation.isValid()) {
                    callSet(
                            new SetOperation(customSetOperation.getKey(), customSetOperation.getValue()),
                            tEnv,
                            setMap);
                }
                return true;
            }
        }
        return false;
    }

    private void callSet(
            SetOperation setOperation, CustomTableEnvironment environment, Map<String, Object> setMap) {
        if (!setOperation.getKey().isPresent() || !setOperation.getValue().isPresent()) {
            return;
        }

        String key = setOperation.getKey().get().trim();
        String value = setOperation.getValue().get().trim();
        if (Asserts.isNullString(key) || Asserts.isNullString(value)) {
            return;
        }
        setMap.put(key, value);

        setConfiguration(environment, Collections.singletonMap(key, value));
    }


    private void callReset(
            ResetOperation resetOperation, CustomTableEnvironment environment, Map<String, Object> setMap) {
        final Optional<String> keyOptional = resetOperation.getKey();
        if (!keyOptional.isPresent()) {
            setMap.clear();
            return;
        }

        String key = keyOptional.get().trim();
        if (Asserts.isNullString(key)) {
            return;
        }

        setMap.remove(key);
        setConfiguration(environment, Collections.singletonMap(key, null));
    }

    private void setConfiguration(CustomTableEnvironment environment, Map<String, String> config) {
        Configuration configuration = Configuration.fromMap(config);
        environment.getStreamExecutionEnvironment().getConfig().configure(configuration, null);
        environment.getStreamExecutionEnvironment().getCheckpointConfig().configure(configuration);
        environment.getConfig().addConfiguration(configuration);
    }


    @Override
    public String asSummaryString() {
        return null;
    }
}
