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

import org.dinky.data.model.FunctionResult;
import org.dinky.data.model.LineageRel;
import org.dinky.executor.CustomTableEnvironmentImpl;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @description: LineageContextTest
 * @author: HamaWhite
 */
public class LineageContextTest {

    private static final String CATALOG_NAME = "default_catalog";

    private static final String DEFAULT_DATABASE = "default_database";

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
        // Create custom function my_suffix_udf
        tableEnv.executeSql("DROP FUNCTION IF EXISTS my_suffix_udf");
        tableEnv.executeSql("CREATE FUNCTION IF NOT EXISTS my_suffix_udf " + "AS 'org.dinky.utils.MySuffixFunction'");
    }

    @Test
    public void testAnalyzeLineage() {
        String sql = "INSERT INTO TT SELECT a||c A ,b||c B FROM ST";
        String[][] expectedArray = {
            {"ST", "a", "TT", "A", "||(a, c)"},
            {"ST", "c", "TT", "A", "||(a, c)"},
            {"ST", "b", "TT", "B", "||(b, c)"},
            {"ST", "c", "TT", "B", "||(b, c)"}
        };

        analyzeLineage(sql, expectedArray);
    }

    @Ignore
    @Test
    public void testAnalyzeLineageAndFunction() {
        String sql = "INSERT INTO TT SELECT LOWER(a) , my_suffix_udf(b) FROM ST";

        String[][] expectedArray = {
            {"ST", "a", "TT", "A", "LOWER(a)"},
            {"ST", "b", "TT", "B", "my_suffix_udf(b)"}
        };

        analyzeLineage(sql, expectedArray);

        analyzeFunction(sql, new String[] {"my_suffix_udf"});
    }

    private void analyzeLineage(String sql, String[][] expectedArray) {
        List<LineageRel> actualList = context.analyzeLineage(sql);
        List<LineageRel> expectedList = LineageRel.build(CATALOG_NAME, DEFAULT_DATABASE, expectedArray);
        assertEquals(expectedList, actualList);
    }

    private void analyzeFunction(String sql, String[] expectedArray) {
        Set<FunctionResult> actualSet = context.analyzeFunction(tableEnv, sql);
        Set<FunctionResult> expectedSet = FunctionResult.build(CATALOG_NAME, DEFAULT_DATABASE, expectedArray);
        assertEquals(expectedSet, actualSet);
    }
}
