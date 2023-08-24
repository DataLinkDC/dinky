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

package com.dlink.trans;

import com.dlink.assertion.Asserts;
import com.dlink.parser.SqlType;

import java.util.Iterator;
import java.util.ServiceLoader;

import lombok.extern.slf4j.Slf4j;

/**
 * Operations
 *
 * @author wenmo
 * @since 2021/5/25 15:50
 **/
@Slf4j
public class Operations {

    private Operations() {
    }

    public static SqlType getOperationType(String sql) {
        String sqlTrim = sql.replaceAll("[\\s\\t\\n\\r]", "").trim().toUpperCase();
        SqlType type = SqlType.UNKNOWN;
        for (SqlType sqlType : SqlType.values()) {
            if (sqlTrim.startsWith(sqlType.getType())) {
                type = sqlType;
                break;
            }
        }
        return type;
    }

    public static Operation buildOperation(String statement) {
        Asserts.checkNotNull(statement, "配置不能为空");
        String sql = statement.replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .toUpperCase();
        ServiceLoader<Operation> loader = ServiceLoader.load(Operation.class);
        Iterator<Operation> iterator = loader.iterator();
        while (iterator.hasNext()) {
            Operation operation = iterator.next();
            if (sql.startsWith(operation.getHandle())) {
                return operation.create(statement);
            }
        }
        return null;
    }
}
