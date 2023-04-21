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
public class CountFunction extends AggregateFunction<Long, CountFunction.MyAccumulator>
        implements IUdfDefine {

    @Override
    public MyAccumulator createAccumulator() {
        return new MyAccumulator();
    }

    public void accumulate(MyAccumulator accumulator, Integer i) {
        if (i != null) {
            accumulator.setCount(accumulator.count + i);
        }
    }

    @Override
    public Long getValue(MyAccumulator accumulator) {
        return accumulator.getCount();
    }

    @Override
    public String getUdfName() {
        return "count";
    }

    public static class MyAccumulator {
        private volatile long count = 0L;

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}
