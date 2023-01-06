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

import com.dlink.executor.Executor;
import com.dlink.trans.AbstractOperation;
import com.dlink.trans.Operation;

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
        if (wheres != null && wheres.size() > 0) {
            for (String s : wheres) {
                source = source.filter($(s));
            }
        }
        Table sink = source.groupBy($(aggTable.getGroupBy()))
                .flatAggregate($(aggTable.getAggBy()))
                .select($(aggTable.getColumns()));
        executor.getCustomTableEnvironment().registerTable(aggTable.getName(), sink);
        return null;
    }
}
