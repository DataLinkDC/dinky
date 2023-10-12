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

package com.dlink.trans.ddl;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.call;
import static org.apache.flink.table.api.Expressions.callSql;

import com.dlink.assertion.Asserts;
import com.dlink.executor.Executor;
import com.dlink.trans.AbstractOperation;
import com.dlink.trans.Operation;

import org.apache.flink.table.api.ApiExpression;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;

import java.util.List;

/**
 * CreateAggTableOperation
 *
 * @author wenmo
 * @since 2021/6/13 19:24
 */
public class CreateAggTableOperation extends AbstractOperation implements Operation {

    private static final String KEY_WORD = "CREATE AGGTABLE";

    public CreateAggTableOperation() {
    }

    public CreateAggTableOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new CreateAggTableOperation(statement);
    }

    @Override
    public TableResult build(Executor executor) {
        AggTable aggTable = AggTable.build(statement);
        Table source = executor.getCustomTableEnvironment().sqlQuery("select * from " + aggTable.getTable());
        List<String> wheres = aggTable.getWheres();
        if (Asserts.isNotNullCollection(wheres)) {
            for (String where : wheres) {
                source = source.filter($(where));
            }
        }
        // Table API: call("Top2", $("score")).as("score", "rank");
        // Flink SQL: AGG BY TOP2(score) AS (score,rank)
        ApiExpression udtafExpression = call(aggTable.getAggByFunction());
        ApiExpression[] params = translateApiExpressionArray(aggTable.getAggByFunctionParam());
        if (params.length > 0) {
            udtafExpression = call(aggTable.getAggByFunction(), params);
        }

        ApiExpression[] columns = translateApiExpressionArray(aggTable.getColumns());
        String[] names = aggTable.getAggByFunctionASField().split(",");
        if (names.length == 1) {
            source = source.groupBy($(aggTable.getGroupBy())).flatAggregate(udtafExpression.as(names[0]))
                    .select(columns);
        } else if (names.length > 1) {
            String[] extraNames = new String[names.length - 1];
            System.arraycopy(names, 1, extraNames, 0, extraNames.length);
            source = source.groupBy($(aggTable.getGroupBy())).flatAggregate(udtafExpression.as(names[0], extraNames))
                    .select(columns);
        } else {
            source = source.groupBy($(aggTable.getGroupBy())).flatAggregate(udtafExpression).select(columns);
        }

        if (columns.length > 0) {
            executor.getCustomTableEnvironment().registerTable(aggTable.getName(), source);
        }

        return null;
    }

    private static ApiExpression[] translateApiExpressionArray(String str) {
        String[] strs = str.split(",");
        ApiExpression[] apiExpressions = new ApiExpression[strs.length];
        for (int i = 0; i < strs.length; i++) {
            String item = strs[i].trim();
            if (item.contains("'")) {
                apiExpressions[i] = callSql(strs[i].trim());
            } else {
                apiExpressions[i] = $(strs[i].trim());
            }
        }
        return apiExpressions;
    }
}
