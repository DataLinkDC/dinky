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

package com.dlink.executor;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.ConnectorDescriptor;
import org.apache.flink.table.descriptors.StreamTableDescriptor;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.table.functions.TableFunction;

/**
 *
 */
public interface DefaultStreamTableEnvironment extends StreamTableEnvironment, DefaultTableEnvironment, TableEnvironmentInstance {

    default StreamTableEnvironment getStreamTableEnvironment() {
        return (StreamTableEnvironment) getTableEnvironment();
    }

    @Override// region StreamTableEnvironment interface
    default <T> void registerFunction(String s, TableFunction<T> tableFunction) {
        getStreamTableEnvironment().registerFunction(s, tableFunction);
    }

    @Override
    default <T, A> void registerFunction(String s, AggregateFunction<T, A> aggregateFunction) {
        getStreamTableEnvironment().registerFunction(s, aggregateFunction);
    }

    @Override
    default <T, A> void registerFunction(String s, TableAggregateFunction<T, A> tableAggregateFunction) {
        getStreamTableEnvironment().registerFunction(s, tableAggregateFunction);
    }

    @Override
    default <T> Table fromDataStream(DataStream<T> dataStream) {
        return getStreamTableEnvironment().fromDataStream(dataStream);
    }

    @Override
    default <T> void createTemporaryView(String s, DataStream<T> dataStream) {
        getStreamTableEnvironment().createTemporaryView(s, dataStream);
    }

    @Override
    default <T> Table fromDataStream(DataStream<T> dataStream, String s) {
        return getStreamTableEnvironment().fromDataStream(dataStream, s);
    }

    @Override
    default <T> Table fromDataStream(DataStream<T> dataStream, Expression... expressions) {
        return getStreamTableEnvironment().fromDataStream(dataStream, expressions);
    }

    @Override
    default <T> void registerDataStream(String s, DataStream<T> dataStream) {
        getStreamTableEnvironment().registerDataStream(s, dataStream);
    }

    @Override
    default <T> void registerDataStream(String s, DataStream<T> dataStream, String s1) {
        getStreamTableEnvironment().registerDataStream(s, dataStream, s1);
    }

    @Override
    default <T> void createTemporaryView(String s, DataStream<T> dataStream, String s1) {
        getStreamTableEnvironment().createTemporaryView(s, dataStream, s1);
    }

    @Override
    default <T> void createTemporaryView(String s, DataStream<T> dataStream, Expression... expressions) {
        getStreamTableEnvironment().createTemporaryView(s, dataStream, expressions);
    }

    @Override
    default <T> DataStream<T> toAppendStream(Table table, Class<T> aClass) {
        return getStreamTableEnvironment().toAppendStream(table, aClass);
    }

    @Override
    default <T> DataStream<T> toAppendStream(Table table, TypeInformation<T> typeInformation) {
        return getStreamTableEnvironment().toAppendStream(table, typeInformation);
    }

    @Override
    default <T> DataStream<Tuple2<Boolean, T>> toRetractStream(Table table, Class<T> aClass) {
        return getStreamTableEnvironment().toRetractStream(table, aClass);
    }

    @Override
    default <T> DataStream<Tuple2<Boolean, T>> toRetractStream(Table table, TypeInformation<T> typeInformation) {
        return getStreamTableEnvironment().toRetractStream(table, typeInformation);
    }

    @Override
    default JobExecutionResult execute(String s) throws Exception {
        return getStreamTableEnvironment().execute(s);
    }

    @Override
    default StreamTableDescriptor connect(ConnectorDescriptor connectorDescriptor) {
        return getStreamTableEnvironment().connect(connectorDescriptor);
    }

    // endregion
}
