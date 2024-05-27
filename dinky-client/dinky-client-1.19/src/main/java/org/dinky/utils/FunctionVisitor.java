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

package org.dinky.utils;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.util.SqlBasicVisitor;
import org.apache.flink.table.catalog.UnresolvedIdentifier;

import java.util.ArrayList;
import java.util.List;

public class FunctionVisitor extends SqlBasicVisitor<Void> {

    private final List<UnresolvedIdentifier> functionList = new ArrayList<>();

    @Override
    public Void visit(SqlCall call) {
        if (call instanceof SqlBasicCall && call.getOperator() instanceof SqlFunction) {
            SqlFunction function = (SqlFunction) call.getOperator();
            SqlIdentifier opName = function.getNameAsId();

            functionList.add(UnresolvedIdentifier.of(opName.names));
        }
        return super.visit(call);
    }

    public List<UnresolvedIdentifier> getFunctionList() {
        return functionList;
    }
}
