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

package org.dinky.core;

import org.dinky.plus.FlinkSqlPlus;

import org.apache.flink.runtime.rest.messages.JobPlanInfo;

import org.junit.Ignore;
import org.junit.Test;

/**
 * FlinkSqlPlusTest
 *
 * @author wenmo
 * @since 2021/6/23 10:37
 */
public class FlinkSqlPlusTest {

    @Ignore
    @Test
    public void getJobPlanInfo() {
        String sql =
                "jdbcconfig:='connector' = 'jdbc',\n"
                        + "    'url' = 'jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false"
                        + "&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',\n"
                        + "    'username'='dinky',\n"
                        + "    'password'='dinky',;\n"
                        + "create temporary function TOP2 as 'org.dinky.ud.udtaf.Top2';\n"
                        + "CREATE TABLE student (\n"
                        + "    sid INT,\n"
                        + "    name STRING,\n"
                        + "    PRIMARY KEY (sid) NOT ENFORCED\n"
                        + ") WITH (\n"
                        + "    ${jdbcconfig}\n"
                        + "    'table-name' = 'student'\n"
                        + ");\n"
                        + "CREATE TABLE score (\n"
                        + "    cid INT,\n"
                        + "    sid INT,\n"
                        + "    cls STRING,\n"
                        + "    score INT,\n"
                        + "    PRIMARY KEY (cid) NOT ENFORCED\n"
                        + ") WITH (\n"
                        + "    ${jdbcconfig}\n"
                        + "    'table-name' = 'score'\n"
                        + ");\n"
                        + "CREATE TABLE scoretop2 (\n"
                        + "    cls STRING,\n"
                        + "    score INT,\n"
                        + "    `rank` INT,\n"
                        + "    PRIMARY KEY (cls,`rank`) NOT ENFORCED\n"
                        + ") WITH (\n"
                        + "    ${jdbcconfig}\n"
                        + "    'table-name' = 'scoretop2'\n"
                        + ");\n"
                        + "CREATE AGGTABLE aggscore AS \n"
                        + "SELECT cls,score,rank\n"
                        + "FROM score\n"
                        + "GROUP BY cls\n"
                        + "AGG BY TOP2(score) as (score,rank);\n"
                        + "insert into scoretop2\n"
                        + "select \n"
                        + "b.cls,b.score,b.`rank`\n"
                        + "from aggscore b";

        FlinkSqlPlus plus = FlinkSqlPlus.build();
        JobPlanInfo jobPlanInfo = plus.getJobPlanInfo(sql);
        System.out.println(jobPlanInfo.getJsonPlan());
    }
}
