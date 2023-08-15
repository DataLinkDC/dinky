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

import org.dinky.api.FlinkAPI;
import org.dinky.gateway.result.SavePointResult;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * FlinkRestAPITest
 *
 * @since 2021/6/24 14:24
 */
@Ignore
public class FlinkRestAPITest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlinkRestAPITest.class);

    // private String address = "192.168.123.157:8081";
    private String address = "cdh1:8081";

    @Test
    public void savepointTest() {
        // JsonNode savepointInfo =
        // FlinkAPI.build(address).getSavepointInfo("602ad9d03b872dba44267432d1a2a3b2","04044589477a973a32e7dd53e1eb20fd");
        SavePointResult savepoints =
                FlinkAPI.build(address).savepoints("243b97597448edbd2e635fc3d25b1064", "trigger", null);
        LOGGER.info(savepoints.toString());
    }

    @Test
    public void selectTest() {
        List<JsonNode> jobs = FlinkAPI.build(address).listJobs();
        LOGGER.info(jobs.toString());
    }

    @Test
    public void stopTest() {
        FlinkAPI.build(address).stop("0727f796fcf9e07d89e724f7e15598cf");
    }

    @Test
    public void getCheckPointsDetailInfoTest() {
        JsonNode checkPointsDetailInfo =
                FlinkAPI.build(address).getCheckPointsConfig("9b0910c865874430b98d3817a248eb24");
        LOGGER.info(checkPointsDetailInfo.toString());
    }

    @Test
    public void getConfigurationsDetailsInfoTest() {
        JsonNode configurationsDetailsInfo = FlinkAPI.build(address).getJobsConfig("9b0910c865874430b98d3817a248eb24");
        LOGGER.info(configurationsDetailsInfo.toString());
    }

    @Test
    public void getExectionsInfoTest() {
        JsonNode exectionsDetailInfo = FlinkAPI.build(address).getException("9b0910c865874430b98d3817a248eb24");
        LOGGER.info(exectionsDetailInfo.toString());
    }

    @Test
    public void getJobManagerMetricsTest() {
        JsonNode jobManagerMetrics = FlinkAPI.build(address).getJobManagerMetrics();
        LOGGER.info(jobManagerMetrics.toString());
    }

    @Test
    public void getJobManagerConfigTest() {
        JsonNode jobManagerConfig = FlinkAPI.build(address).getJobManagerConfig();
        LOGGER.info(jobManagerConfig.toString());
    }

    @Test
    public void getJobManagerLogTest() {
        String jobManagerLog = FlinkAPI.build(address).getJobManagerLog();
        LOGGER.info(jobManagerLog);
    }

    @Test
    public void getJobManagerStdOutTest() {
        String jobManagerLogs = FlinkAPI.build(address).getJobManagerStdOut();
        LOGGER.info(jobManagerLogs);
    }

    @Test
    public void getJobManagerLogListTest() {
        JsonNode jobManagerLogList = FlinkAPI.build(address).getJobManagerLogList();
        LOGGER.info(jobManagerLogList.toString());
    }

    @Test
    public void getJobManagerLogListToDetailTest() {
        String jobManagerLogList = FlinkAPI.build(address).getJobManagerLogFileDetail("jobmanager.log");
        LOGGER.info(jobManagerLogList.toString());
    }

    @Test
    public void getTaskManagersTest() {
        JsonNode taskManagers = FlinkAPI.build(address).getTaskManagers();
        LOGGER.info(taskManagers.toString());
    }

    @Test
    public void getTaskManagerMetricsTest() {
        JsonNode taskManagerMetrics =
                FlinkAPI.build(address).getTaskManagerMetrics("container_e46_1655948912029_0061_01_000002");
        LOGGER.info(taskManagerMetrics.toString());
    }

    @Test
    public void getTaskManagerLogTest() {
        String taskManagerLog = FlinkAPI.build(address).getTaskManagerLog("container_e46_1655948912029_0061_01_000002");
        LOGGER.info(taskManagerLog);
    }

    @Test
    public void getTaskManagerStdOutTest() {
        String taskManagerStdOut =
                FlinkAPI.build(address).getTaskManagerStdOut("container_e46_1655948912029_0061_01_000002");
        LOGGER.info(taskManagerStdOut);
    }

    @Test
    public void getTaskManagerLogListTest() {
        JsonNode taskManagerLogList =
                FlinkAPI.build(address).getTaskManagerLogList("container_e46_1655948912029_0061_01_000002");
        LOGGER.info(taskManagerLogList.toString());
    }

    @Test
    public void getTaskManagerLogListToDetail() {
        String taskManagerLogDetail = FlinkAPI.build(address)
                .getTaskManagerLogFileDetail("container_e46_1655948912029_0061_01_000002", "taskmanager.log");
        LOGGER.info(taskManagerLogDetail);
    }

    @Test
    public void getTaskManagerThreadDumpTest() {
        JsonNode taskManagerThreadDump =
                FlinkAPI.build(address).getTaskManagerThreadDump("container_e46_1655948912029_0061_01_000002");
        LOGGER.info(taskManagerThreadDump.toString());
    }
}
