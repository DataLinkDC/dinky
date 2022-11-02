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

package com.dlink.function.udtaf;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.util.Collector;

/**
 * Top2WithRetract
 *
 * @author wenmo
 * @since 2021/12/17 18:55
 */

public class Top2WithRetract
        extends
            TableAggregateFunction<Tuple2<Integer, Integer>, Top2WithRetract.Top2WithRetractAccumulator> {

    public static class Top2WithRetractAccumulator {

        public Integer first;
        public Integer second;
        public Integer oldFirst;
        public Integer oldSecond;
    }

    @Override
    public Top2WithRetractAccumulator createAccumulator() {
        Top2WithRetractAccumulator acc = new Top2WithRetractAccumulator();
        acc.first = Integer.MIN_VALUE;
        acc.second = Integer.MIN_VALUE;
        acc.oldFirst = Integer.MIN_VALUE;
        acc.oldSecond = Integer.MIN_VALUE;
        return acc;
    }

    public void accumulate(Top2WithRetractAccumulator acc, Integer v) {
        if (v > acc.first) {
            acc.second = acc.first;
            acc.first = v;
        } else if (v > acc.second) {
            acc.second = v;
        }
    }

    public void retract(Top2WithRetractAccumulator acc, Integer v) {
        if (v.equals(acc.first)) {
            acc.oldFirst = acc.first;
            acc.oldSecond = acc.second;
            acc.first = acc.second;
            acc.second = Integer.MIN_VALUE;
        } else if (v.equals(acc.second)) {
            acc.oldSecond = acc.second;
            acc.second = Integer.MIN_VALUE;
        }
    }

    public void emitValue(Top2WithRetractAccumulator acc, Collector<Tuple2<Integer, Integer>> out) {
        // emit the value and rank
        if (acc.first != Integer.MIN_VALUE) {
            out.collect(Tuple2.of(acc.first, 1));
        }
        if (acc.second != Integer.MIN_VALUE) {
            out.collect(Tuple2.of(acc.second, 2));
        }
    }

    public void emitUpdateWithRetract(
                                      Top2WithRetractAccumulator acc,
                                      RetractableCollector<Tuple2<Integer, Integer>> out) {
        if (!acc.first.equals(acc.oldFirst)) {
            // if there is an update, retract the old value then emit a new value
            if (acc.oldFirst != Integer.MIN_VALUE) {
                out.retract(Tuple2.of(acc.oldFirst, 1));
            }
            out.collect(Tuple2.of(acc.first, 1));
            acc.oldFirst = acc.first;
        }
        if (!acc.second.equals(acc.oldSecond)) {
            // if there is an update, retract the old value then emit a new value
            if (acc.oldSecond != Integer.MIN_VALUE) {
                out.retract(Tuple2.of(acc.oldSecond, 2));
            }
            out.collect(Tuple2.of(acc.second, 2));
            acc.oldSecond = acc.second;
        }
    }
}
