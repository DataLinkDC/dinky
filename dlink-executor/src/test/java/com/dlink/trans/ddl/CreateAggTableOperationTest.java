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

import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.executor.LocalStreamExecutor;

import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CreateAggTableOperationTest {

    @Test
    public void parseCreateAggTableSchemaTest() {
        String sourceStatement = "CREATE TABLE score (\n" +
                "    cid INT,\n" +
                "    cls STRING,\n" +
                "    score INT\n" +
                ") WITH (\n" +
                "    'connector' = 'datagen'\n" +
                ")";
        String udfStatement = "create temporary function TOP2 as 'com.dlink.function.udtaf.Top2'";
        String aggTableStatement = "CREATE AGGTABLE aggscore AS \n" +
                "SELECT cls,score,rank\n" +
                "FROM score\n" +
                "GROUP BY cls\n" +
                "AGG BY TOP2(score) as (score,rank)";
        ExecutorSetting executorSetting = ExecutorSetting.DEFAULT;
        Executor executor = LocalStreamExecutor.buildLocalExecutor(executorSetting);
        executor.executeSql(sourceStatement);
        executor.executeSql(udfStatement);
        executor.executeSql(aggTableStatement);
        TableResult tableResult = executor.executeSql("desc aggscore");
        CloseableIterator<Row> collect = tableResult.collect();
        List<String> columns = new ArrayList<>();
        while (collect.hasNext()) {
            Row row = collect.next();
            columns.add(row.getField(0).toString());
        }
        List<String> targetColumns = new ArrayList<String>() {

            {
                add("cls");
                add("score");
                add("rank");
            }
        };
        Assert.assertArrayEquals(columns.toArray(), targetColumns.toArray());
    }
}
