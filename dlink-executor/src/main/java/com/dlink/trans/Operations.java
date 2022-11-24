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

import com.dlink.parser.SqlType;
import com.dlink.trans.ddl.CreateAggTableOperation;
import com.dlink.trans.ddl.CreateCDCSourceOperation;
import com.dlink.trans.ddl.SetOperation;
import com.dlink.trans.ddl.ShowFragmentOperation;
import com.dlink.trans.ddl.ShowFragmentsOperation;

import java.util.Arrays;

/**
 * Operations
 *
 * @author wenmo
 * @since 2021/5/25 15:50
 **/
public class Operations {

    private Operations() {
    }

    private static final Operation[] ALL_OPERATIONS = {new CreateAggTableOperation()
            , new SetOperation()
            , new CreateCDCSourceOperation()
            , new ShowFragmentsOperation()
            , new ShowFragmentOperation()
    };

    public static SqlType getSqlTypeFromStatements(String statement) {
        String[] statements = statement.split(";");
        SqlType sqlType = SqlType.UNKNOWN;
        for (String item : statements) {
            if (item.trim().isEmpty()) {
                continue;
            }
            sqlType = Operations.getOperationType(item);
            if (sqlType == SqlType.INSERT || sqlType == SqlType.SELECT) {
                return sqlType;
            }
        }
        return sqlType;
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
        String sql = statement.replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .toUpperCase();

        return Arrays.stream(ALL_OPERATIONS)
                .filter(p -> sql.startsWith(p.getHandle()))
                .findFirst()
                .map(p -> p.create(statement))
                .orElse(null);
    }
}
