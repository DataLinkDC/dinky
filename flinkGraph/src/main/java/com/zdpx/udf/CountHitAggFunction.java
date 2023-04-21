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

/** */
public class CountHitAggFunction extends AggregateFunction<Long, CountFunction.MyAccumulator>
        implements IUdfDefine {
    @Override
    public Long getValue(CountFunction.MyAccumulator accumulator) {
        return accumulator.getCount();
    }

    @Override
    public CountFunction.MyAccumulator createAccumulator() {
        return new CountFunction.MyAccumulator();
    }

    public void accumulate(
            CountFunction.MyAccumulator accumulator, Double longitude, Double latitude) {
        if (longitude < 90 && latitude < 30) {
            accumulator.setCount(accumulator.getCount() + 1);
        }
    }

    @Override
    public String getUdfName() {
        return "countHit";
    }
}
