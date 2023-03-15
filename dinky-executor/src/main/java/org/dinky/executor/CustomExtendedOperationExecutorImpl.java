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

package org.dinky.executor;

import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.internal.TableResultInternal;
import org.apache.flink.table.operations.Operation;
import org.dinky.trans.ddl.AggTable;
import org.dinky.trans.ddl.NewCreateAggTableOperation;

import java.util.List;
import java.util.Optional;

import static org.apache.flink.table.api.Expressions.$;

public class CustomExtendedOperationExecutorImpl implements CustomExtendedOperationExecutor {

    private Executor executor;

    public CustomExtendedOperationExecutorImpl(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Optional<TableResultInternal> executeOperation(Operation operation) {
        if (operation instanceof NewCreateAggTableOperation) {
            return executeCreateAggTableOperationNew((NewCreateAggTableOperation) operation);
        }

        // note: null result represent not in custom operator,
        return null;
    }

    public Optional<TableResultInternal> executeCreateAggTableOperationNew(
            NewCreateAggTableOperation option) {
        AggTable aggTable = AggTable.build(option.getStatement());
        Table source =
                executor.getCustomTableEnvironment()
                        .sqlQuery("select * from " + aggTable.getTable());
        List<String> wheres = aggTable.getWheres();
        if (wheres != null && wheres.size() > 0) {
            for (String s : wheres) {
                source = source.filter($(s));
            }
        }
        Table sink =
                source.groupBy($(aggTable.getGroupBy()))
                        .flatAggregate($(aggTable.getAggBy()))
                        .select($(aggTable.getColumns()));
        executor.getCustomTableEnvironment().registerTable(aggTable.getName(), sink);
        return null;
    }
}
