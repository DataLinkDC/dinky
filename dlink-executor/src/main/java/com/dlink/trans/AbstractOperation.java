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

import com.dlink.executor.CustomTableEnvironmentImpl;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractOperation
 *
 * @author wenmo
 * @since 2021/6/14 18:18
 */
public class AbstractOperation {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractOperation.class);

    protected String statement;

    public AbstractOperation() {
    }

    public AbstractOperation(String statement) {
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public boolean checkFunctionExist(CustomTableEnvironmentImpl stEnvironment, String key) {
        String[] udfs = stEnvironment.listUserDefinedFunctions();
        List<String> udflist = Arrays.asList(udfs);
        if (udflist.contains(key.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean noExecute() {
        return true;
    }
}
