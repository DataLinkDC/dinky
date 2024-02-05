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

import org.dinky.assertion.Asserts;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.Task;
import org.dinky.function.data.model.UDF;
import org.dinky.function.util.UDFUtil;

import org.apache.flink.table.catalog.FunctionLanguage;

public class UDFUtils extends UDFUtil {

    public static UDF taskToUDF(Task task) {
        if (Asserts.isNotNull(task.getConfigJson())
                && Asserts.isNotNull(task.getConfigJson().getUdfConfig())) {
            return UDF.builder()
                    .className(task.getConfigJson().getUdfConfig().getClassName())
                    .code(task.getStatement())
                    .functionLanguage(FunctionLanguage.valueOf(task.getDialect().toUpperCase()))
                    .build();
        } else {
            throw new BusException("udf `class` config is null,please check your udf task config");
        }
    }
}
