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
import org.dinky.executor.Executor;
import org.dinky.parser.SingleSqlParserFactory;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.Operation;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.TableResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SetOperation
 *
 * @author wenmo
 * @since 2021/10/21 19:56
 **/
public class SetOperation extends AbstractOperation implements Operation {

    private static final String KEY_WORD = "SET";

    public SetOperation() {
    }

    public SetOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new SetOperation(statement);
    }

    @Override
    public TableResult build(Executor executor) {
        try {
            if (null != Class.forName("org.apache.log4j.Logger")) {
                executor.parseAndLoadConfiguration(statement);
                return null;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, List<String>> map = SingleSqlParserFactory.generateParser(statement);
        if (Asserts.isNotNullMap(map) && map.size() == 2) {
            Map<String, String> confMap = new HashMap<>();
            confMap.put(StringUtils.join(map.get("SET"), "."), StringUtils.join(map.get("="), ","));
            executor.getCustomTableEnvironment().getConfig().addConfiguration(Configuration.fromMap(confMap));
            Configuration configuration = Configuration.fromMap(confMap);
            executor.getExecutionConfig().configure(configuration, null);
            executor.getCustomTableEnvironment().getConfig().addConfiguration(configuration);
        }
        return null;
    }
}
