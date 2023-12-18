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

package org.dinky.trans.show;

import org.dinky.assertion.Asserts;
import org.dinky.executor.Executor;
import org.dinky.parser.SingleSqlParserFactory;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.Operation;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.api.TableResult;

import java.util.List;
import java.util.Map;

/**
 * ShowFragmentOperation
 *
 * @since 2022/2/17 17:08
 */
public class ShowFragmentOperation extends AbstractOperation implements Operation {

    private static final String KEY_WORD = "SHOW FRAGMENT ";

    public ShowFragmentOperation() {}

    public ShowFragmentOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new ShowFragmentOperation(statement);
    }

    @Override
    public TableResult execute(Executor executor) {
        Map<String, List<String>> map = SingleSqlParserFactory.generateParser(statement);
        if (Asserts.isNotNullMap(map)) {
            if (map.containsKey("FRAGMENT")) {
                return executor.getVariableManager().getVariableResult(StringUtils.join(map.get("FRAGMENT"), ""));
            }
        }
        return executor.getVariableManager().getVariableResult(null);
    }
}
