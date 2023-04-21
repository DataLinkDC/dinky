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

package com.zdpx.udf;

import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.FunctionKind;
import org.apache.flink.table.functions.ScalarFunction;

/** */
public interface IUdfDefine {
    default String getUdfName() {
        return getClass().getSimpleName();
    }

    default FunctionKind getFunctionType() {
        if (ScalarFunction.class.isAssignableFrom(this.getClass())) {
            return FunctionKind.SCALAR;
        } else if (AggregateFunction.class.isAssignableFrom(this.getClass())) {
            return FunctionKind.AGGREGATE;
        } else {
            return FunctionKind.OTHER;
        }
    }
}
