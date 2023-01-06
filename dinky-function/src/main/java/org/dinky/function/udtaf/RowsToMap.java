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

import com.dlink.function.udtaf.RowsToMap.MyAccum;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.catalog.DataTypeFactory;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.inference.InputTypeStrategies;
import org.apache.flink.table.types.inference.TypeInference;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * RowsToMap
 *
 * @param <K> Map key type
 * @param <V> Map value type
 * @author wenmo, lixiaoPing
 * @since 2021/5/25 15:50
 **/

public class RowsToMap<K, V> extends TableAggregateFunction<Map<K, V>, MyAccum<K, V>> {

    private static final long serialVersionUID = 42L;

    @Override
    public TypeInference getTypeInference(DataTypeFactory typeFactory) {
        return TypeInference.newBuilder()
                .inputTypeStrategy(InputTypeStrategies.sequence(
                        InputTypeStrategies.ANY,
                        InputTypeStrategies.ANY))
                .accumulatorTypeStrategy(callContext -> {
                    List<DataType> argumentDataTypes = callContext.getArgumentDataTypes();
                    final DataType arg0DataType = argumentDataTypes.get(0);
                    final DataType arg1DataType = argumentDataTypes.get(1);
                    final DataType accDataType = DataTypes.STRUCTURED(
                            MyAccum.class,
                            DataTypes.FIELD("mapView",
                                    DataTypes.MAP(arg0DataType, arg1DataType)));
                    return Optional.of(accDataType);
                })
                .outputTypeStrategy(callContext -> {
                    List<DataType> argumentDataTypes = callContext.getArgumentDataTypes();
                    final DataType arg0DataType = argumentDataTypes.get(0);
                    final DataType arg1DataType = argumentDataTypes.get(1);
                    return Optional.of(DataTypes.MAP(arg0DataType, arg1DataType));
                })
                .build();
    }

    @Override
    public MyAccum<K, V> createAccumulator() {
        return new MyAccum<>();
    }

    public void accumulate(
                           MyAccum<K, V> acc, K cls, V v) {
        if (v == null) {
            return;
        }

        acc.mapView.put(cls, v);
    }

    /**
     * Retracts the input values from the accumulator instance. The current design assumes the
     * inputs are the values that have been previously accumulated. The method retract can be
     * overloaded with different custom types and arguments. This function must be implemented for
     * datastream bounded over aggregate.
     *
     * @param acc the accumulator which contains the current aggregated results
     */
    public void retract(MyAccum<K, V> acc, K cls, V v) {
        if (v == null) {
            return;
        }
        acc.mapView.remove(cls);
    }

    /**
     * Merges a group of accumulator instances into one accumulator instance. This function must be
     * implemented for datastream session window grouping aggregate and bounded grouping aggregate.
     *
     * @param acc      the accumulator which will keep the merged aggregate results. It should be
     *                 noted that the accumulator may contain the previous aggregated results.
     *                 Therefore user should not replace or clean this instance in the custom merge
     *                 method.
     * @param iterable an {@link Iterable} pointed to a group of accumulators that will be merged.
     */
    public void merge(MyAccum<K, V> acc, Iterable<MyAccum<K, V>> iterable) {
        for (MyAccum<K, V> otherAcc : iterable) {
            for (Map.Entry<K, V> entry : otherAcc.mapView.entrySet()) {
                accumulate(acc, entry.getKey(), entry.getValue());
            }
        }
    }

    public void emitValue(MyAccum<K, V> acc, Collector<Map<K, V>> out) {
        out.collect(acc.mapView);
    }

    public static class MyAccum<K, V> {

        /**
         * 不能 final
         */
        public Map<K, V> mapView;

        /**
         * 不能删除，否则不能生成查询计划
         */
        public MyAccum() {
            this.mapView = new HashMap<>();
        }
    }
}
