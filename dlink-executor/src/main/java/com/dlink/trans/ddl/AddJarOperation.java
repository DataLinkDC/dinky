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

import com.dlink.context.JarPathContextHolder;
import com.dlink.executor.Executor;
import com.dlink.parser.check.AddJarSqlParser;
import com.dlink.trans.AbstractOperation;
import com.dlink.trans.Operation;

import org.apache.flink.table.api.TableResult;

/**
 * @author ZackYoung
 * @since 0.7.0
 */
public class AddJarOperation extends AbstractOperation implements Operation {

    private static final String KEY_WORD = "ADD JAR";

    public AddJarOperation(String statement) {
        super(statement);
    }

    public AddJarOperation() {
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new AddJarOperation(statement);
    }

    @Override
    public TableResult build(Executor executor) {
        return null;
    }

    public void init() {
        AddJarSqlParser.getAllFilePath(statement).forEach(JarPathContextHolder::addOtherPlugins);

    }
}
