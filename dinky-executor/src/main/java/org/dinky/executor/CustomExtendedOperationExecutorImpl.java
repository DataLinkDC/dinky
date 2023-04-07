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

import org.dinky.trans.ExtendOperation;

import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.operations.Operation;

import java.util.Optional;

public class CustomExtendedOperationExecutorImpl implements CustomExtendedOperationExecutor {

    private Executor executor;

    public CustomExtendedOperationExecutorImpl(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Optional<? extends TableResult> executeOperation(Operation operation) {
        if (operation instanceof ExtendOperation) {
            ExtendOperation extendOperation = (ExtendOperation) operation;
            return extendOperation.execute(executor);
        }

        return Optional.empty();
    }
}
