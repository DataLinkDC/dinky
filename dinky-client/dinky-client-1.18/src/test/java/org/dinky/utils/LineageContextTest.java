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

import static org.junit.Assert.assertEquals;

import org.dinky.data.model.LineageRel;
import org.dinky.executor.CustomTableEnvironmentImpl;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @description: LineageContextTest
 * @author: HamaWhite
 */
public class LineageContextTest {

    private static CustomTableEnvironmentImpl tableEnv;
    private static LineageContext context;

    @BeforeClass
    public static void setUp() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(new Configuration());

        EnvironmentSettings settings =
                EnvironmentSettings.newInstance().inStreamingMode().build();
        tableEnv = CustomTableEnvironmentImpl.create(env, settings);
        context = new LineageContext(tableEnv);
    }

    @Before
    public void init() {
        // create table ST
        tableEnv.executeSql("DROP TABLE IF EXISTS ST");
        tableEnv.executeSql("CREATE TABLE ST (     " + "    a STRING                               ,"
                + "    b STRING                               ,"
                + "    c STRING                                "
                + ") WITH (                                    "
                + "    'connector' = 'datagen'                ,"
                + "    'rows-per-second' = '1'                 "
                + ")");

        // create table TT
        tableEnv.executeSql("DROP TABLE IF EXISTS TT");
        tableEnv.executeSql("CREATE TABLE TT (     " + "    A STRING                               ,"
                + "    B STRING                                "
                + ") WITH (                                    "
                + "    'connector' = 'print'                   "
                + ")");
    }

    @Test
    public void testGetLineage() {
        List<LineageRel> actualList = context.analyzeLineage("INSERT INTO TT select a||c A ,b||c B from ST");
        String[][] expectedArray = {
            {"ST", "a", "TT", "A", "||(a, c)"},
            {"ST", "c", "TT", "A", "||(a, c)"},
            {"ST", "b", "TT", "B", "||(b, c)"},
            {"ST", "c", "TT", "B", "||(b, c)"}
        };

        List<LineageRel> expectedList = buildResult(expectedArray);
        assertEquals(expectedList, actualList);
    }

    private List<LineageRel> buildResult(String[][] expectedArray) {
        return Stream.of(expectedArray)
                .map(e -> {
                    String transform = e.length == 5 ? e[4] : null;
                    return new LineageRel(
                            "default_catalog",
                            "default_database",
                            e[0],
                            e[1],
                            "default_catalog",
                            "default_database",
                            e[2],
                            e[3],
                            transform);
                })
                .collect(Collectors.toList());
    }
}
