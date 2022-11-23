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

package com.dlink.core;

import com.dlink.explainer.lineage.LineageBuilder;
import com.dlink.explainer.lineage.LineageResult;

import org.junit.Test;

/**
 * LineageTest
 *
 * @author wenmo
 * @since 2022/3/15 23:08
 */
public class LineageTest {

    @Test
    public void sumTest() {
        String sql = "CREATE TABLE ST (\n"
                + "    a STRING,\n"
                + "    b STRING,\n"
                + "    c STRING\n"
                + ") WITH (\n"
                + "  'connector' = 'datagen',\n"
                + "  'rows-per-second' = '1'\n"
                + ");\n"
                + "CREATE TABLE TT (\n"
                + "  A STRING,\n"
                + "  B STRING\n"
                + ") WITH (\n"
                + " 'connector' = 'print'\n"
                + ");\n"
                + "insert into TT select a||c A ,b||c B from ST";
        LineageResult result = LineageBuilder.getColumnLineageByLogicalPlan(sql);
        System.out.println("end");
    }
}
