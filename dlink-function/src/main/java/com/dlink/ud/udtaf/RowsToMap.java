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

package com.dlink.ud.udtaf;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.catalog.DataTypeFactory;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.inference.TypeInference;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * RowsToMap
 *
 * @author wenmo
 * @since 2021/5/25 15:50
 **/

public class RowsToMap extends TableAggregateFunction<String, Map<String, Integer>> {

    @Override
    public Map<String, Integer> createAccumulator() {
        return new HashMap<>(16);
    }

    @Override
    public TypeInference getTypeInference(DataTypeFactory typeFactory) {
        return TypeInference.newBuilder()
            .accumulatorTypeStrategy(callContext -> {
                    DataType accDataType = DataTypes.MAP(
                        DataTypes.STRING(), DataTypes.INT());
                    return Optional.of(accDataType);

                }
            )
            .outputTypeStrategy(callContext -> Optional.of(DataTypes.STRING()))
            .build();

    }

    public void accumulate(
        Map<String, Integer> acc,
        String cls,
        Integer v) {
        if (v == null) {
            return;
        }

        String[] keys = cls.split(",");
        for (String s : keys) {
            if (s.equals(cls)) {
                acc.put(cls, v);
            }
        }
    }

    /**
     * Retracts the input values from the accumulator instance. The current design assumes the
     * inputs are the values that have been previously accumulated. The method retract can be
     * overloaded with different custom types and arguments. This function must be implemented for
     * datastream bounded over aggregate.
     *
     * @param acc           the accumulator which contains the current aggregated results
     */
    public void retract(Map<String, Integer> acc, String cls, Integer v) {
        if (v == null) {
            return;
        }
        acc.remove(cls);
    }

    /**
     * Merges a group of accumulator instances into one accumulator instance. This function must be
     * implemented for datastream session window grouping aggregate and bounded grouping aggregate.
     *
     * @param acc  the accumulator which will keep the merged aggregate results. It should
     *                     be noted that the accumulator may contain the previous aggregated
     *                     results. Therefore user should not replace or clean this instance in the
     *                     custom merge method.
     * @param iterable          an {@link java.lang.Iterable} pointed to a group of accumulators that will be
     *                     merged.
     */
    public void merge(Map<String, Integer> acc, Iterable<Map<String, Integer>> iterable) {
        for (Map<String, Integer> otherAcc : iterable) {
            for (Map.Entry<String, Integer> entry : otherAcc.entrySet()) {
                accumulate(acc, entry.getKey(), entry.getValue());
            }
        }
    }

    public void emitValue(Map<String, Integer> acc, Collector<String> out) {
        out.collect(acc.toString());
    }
}
