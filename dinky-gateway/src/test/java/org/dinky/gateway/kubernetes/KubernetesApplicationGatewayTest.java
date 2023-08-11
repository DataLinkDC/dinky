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

package org.dinky.gateway.kubernetes;

import org.dinky.data.model.SystemConfiguration;

public class KubernetesApplicationGatewayTest {

    //    @Test
    public void testWaitTaskManagerReadyJobNotExist() throws InterruptedException {
        SystemConfiguration.getInstances().setConfiguration("flink.settings.jobIdWait", "3");
        // 测试不存在的job
        KubernetesApplicationGateway.waitForTaskManagerToBeReady("flink.b.x", "xxxxx");
    }

    //    @Test
    public void testWaitTaskManagerReadyJobExist() throws InterruptedException {
        SystemConfiguration.getInstances().setConfiguration("flink.settings.jobIdWait", "3");
        // 测试不存在的job
        KubernetesApplicationGateway.waitForTaskManagerToBeReady(
                "flink.b.x", "1c53a8f1b4937f50456190e884c9f289");
        // 打印出： 18:50:50,882 INFO  org.dinky.gateway.AbstractGateway
        // [main]  - get job status
        // success,jobPath:http://flink.b.x/jobs/1c53a8f1b4937f50456190e884c9f289,result:
        // {"jid":"1c53a8f1b4937f50456190e884c9f289","name":"insert-into_hive_catalog.dwd.emp_club_user_follow","isStoppable":false,"state":"FINISHED","start-time":1691577361403,"end-time":1691577362486,"duration":1083,"maxParallelism":-1,"now":1691578250785,"timestamps":{"FAILING":0,"FAILED":0,"RESTARTING":0,"RECONCILING":0,"SUSPENDED":0,"FINISHED":1691577362486,"INITIALIZING":1691577361403,"CANCELLING":0,"CANCELED":0,"CREATED":1691577361408,"RUNNING":1691577361583},"vertices":[{"id":"cbc357ccb763df2852fee8c4fc7d55f2","name":"Source: t_user_follow_view[1] -> Calc[2] -> Map -> Sink: Unnamed","maxParallelism":128,"parallelism":1,"status":"FINISHED","start-time":1691577361679,"end-time":1691577362208,"duration":529,"tasks":{"CANCELED":0,"INITIALIZING":0,"RUNNING":0,"CANCELING":0,"FAILED":0,"SCHEDULED":0,"CREATED":0,"FINISHED":1,"RECONCILING":0,"DEPLOYING":0},"metrics":{"read-bytes":0,"read-bytes-complete":true,"write-bytes":0,"write-bytes-complete":true,"read-records":0,"read-records-complete":true,"write-records":0,"write-records-complete":true,"accumulated-backpressured-time":0,"accumulated-idle-time":0,"accumulated-busy-time":"NaN"}}],"status-counts":{"CANCELED":0,"INITIALIZING":0,"RUNNING":0,"CANCELING":0,"FAILED":0,"SCHEDULED":0,"CREATED":0,"FINISHED":1,"RECONCILING":0,"DEPLOYING":0},"plan":{"jid":"1c53a8f1b4937f50456190e884c9f289","name":"insert-into_hive_catalog.dwd.emp_club_user_follow","type":"BATCH","nodes":[{"id":"cbc357ccb763df2852fee8c4fc7d55f2","parallelism":1,"operator":"","operator_strategy":"","description":"[1]:TableSourceScan(table=[[default_catalog, default_database, t_user_follow_view]], fields=[id, user_id, other_id, status, create_time])<br/>+- [2]:Calc(select=[CAST(id AS BIGINT) AS id, CAST(user_id AS BIGINT) AS user_id, CAST(other_id AS BIGINT) AS other_id, CAST(status AS BIGINT) AS status, FROM_UNIXTIME((create_time / 1000), 'yyyy-MM-dd HH:mm:ss') AS create_time, '技术论坛' AS data_from, '' AS collect_time, '' AS data_src, CAST(CURRENT_TIMESTAMP() AS TIMESTAMP(9)) AS base_date])<br/>   +- Map<br/>      +- Sink: Unnamed<br/>","optimizer_properties":{}}]}}

    }
}
