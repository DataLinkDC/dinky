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

package org.dinky.job;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.dinky.configure.MybatisPlusConfig;
import org.dinky.context.SpringContextUtils;
import org.dinky.context.TenantContextHolder;
import org.dinky.data.dto.JobDataDto;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.model.ClusterInstance;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.data.model.job.JobInstance;
import org.dinky.mapper.AlertRulesMapper;
import org.dinky.mybatis.properties.MybatisPlusFillProperties;
import org.dinky.service.AlertHistoryService;
import org.dinky.service.ClusterInstanceService;
import org.dinky.service.HistoryService;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.MonitorService;
import org.dinky.service.TaskService;
import org.dinky.service.UserService;
import org.dinky.service.impl.AlertRuleServiceImpl;

import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import com.sun.net.httpserver.HttpServer;

class FlinkJobTaskTest {

    private static final JobInstanceService JOB_INSTANCE_SERVICE = mock(JobInstanceService.class);

    private static StaticApplicationContext applicationContext;
    private static ApplicationContext previousApplicationContext;

    private final Map<Integer, Integer> jobTenants = new HashMap<>();
    private final Map<Integer, String> persistedStatuses = new HashMap<>();

    @BeforeAll
    static void registerServices() {
        previousApplicationContext = SpringContextUtils.applicationContext;
        applicationContext = new StaticApplicationContext();
        applicationContext.getBeanFactory().registerSingleton("jobInstanceServiceImpl", JOB_INSTANCE_SERVICE);
        applicationContext.getBeanFactory().registerSingleton("monitorServiceImpl", mock(MonitorService.class));
        applicationContext.getBeanFactory().registerSingleton("jobHistoryServiceImpl", mock(JobHistoryService.class));
        applicationContext
                .getBeanFactory()
                .registerSingleton("clusterInstanceServiceImpl", mock(ClusterInstanceService.class));
        applicationContext.getBeanFactory().registerSingleton("historyServiceImpl", mock(HistoryService.class));
        applicationContext.getBeanFactory().registerSingleton("taskServiceImpl", mock(TaskService.class));
        applicationContext
                .getBeanFactory()
                .registerSingleton("alertHistoryServiceImpl", mock(AlertHistoryService.class));
        applicationContext.getBeanFactory().registerSingleton("userServiceImpl", mock(UserService.class));
        AlertRuleServiceImpl alertRuleService = mock(AlertRuleServiceImpl.class);
        AlertRulesMapper alertRulesMapper = mock(AlertRulesMapper.class);
        when(alertRuleService.getBaseMapper()).thenReturn(alertRulesMapper);
        when(alertRulesMapper.selectWithTemplate()).thenReturn(Collections.emptyList());
        applicationContext.getBeanFactory().registerSingleton("alertRuleServiceImpl", alertRuleService);
        SpringContextUtils.applicationContext = applicationContext;
    }

    @AfterAll
    static void restoreApplicationContext() {
        SpringContextUtils.applicationContext = previousApplicationContext;
        applicationContext.close();
    }

    @BeforeEach
    void prepareTenantScopedPersistence() {
        TenantContextHolder.clear();
        reset(JOB_INSTANCE_SERVICE);
        doAnswer(invocation -> {
                    TenantContextHolder.set(jobTenants.get(invocation.getArgument(0)));
                    return null;
                })
                .when(JOB_INSTANCE_SERVICE)
                .initTenantByJobInstanceId(anyInt());
        when(JOB_INSTANCE_SERVICE.updateById(any(JobInstance.class))).thenAnswer(invocation -> {
            JobInstance instance = invocation.getArgument(0);
            // A tenant-filtered update matches no row when a worker keeps another job's tenant.
            if (!jobTenants.get(instance.getId()).equals(TenantContextHolder.get())) {
                return false;
            }
            persistedStatuses.put(instance.getId(), instance.getStatus());
            return true;
        });
    }

    @AfterEach
    void clearTenantContext() {
        TenantContextHolder.clear();
    }

    @Test
    void persistsTerminalStatusUsingTheJobTenant() {
        TenantContextHolder.set(1);
        FlinkJobTask task = task(101, 2);

        assertTrue(task.dealTask());

        assertAll(
                () -> assertEquals(JobStatus.UNKNOWN.getValue(), persistedStatuses.get(101)),
                () -> assertEquals(1, TenantContextHolder.get()));
    }

    @Test
    void clearsTenantContextWhenTheWorkerHadNoTenant() {
        FlinkJobTask task = task(101, 2);

        assertTrue(task.dealTask());

        assertAll(
                () -> assertEquals(JobStatus.UNKNOWN.getValue(), persistedStatuses.get(101)),
                () -> assertNull(TenantContextHolder.get()));
    }

    @Test
    void refreshesDifferentTenantsOnTheSameWorker() {
        TenantContextHolder.set(1);
        FlinkJobTask firstTask = task(101, 2);
        FlinkJobTask secondTask = task(102, 3);

        assertTrue(firstTask.dealTask());
        assertTrue(secondTask.dealTask());

        assertAll(
                () -> assertEquals(JobStatus.UNKNOWN.getValue(), persistedStatuses.get(101)),
                () -> assertEquals(JobStatus.UNKNOWN.getValue(), persistedStatuses.get(102)),
                () -> assertEquals(1, TenantContextHolder.get()));
    }

    @Test
    void restoresPreviousTenantWhenRefreshFails() {
        TenantContextHolder.set(1);
        FlinkJobTask task = task(101, 2);
        IllegalStateException failure = new IllegalStateException("Unable to persist status");
        doAnswer(invocation -> {
                    assertEquals(2, TenantContextHolder.get());
                    throw failure;
                })
                .when(JOB_INSTANCE_SERVICE)
                .updateById(any(JobInstance.class));

        assertSame(failure, assertThrows(IllegalStateException.class, task::dealTask));
        assertEquals(1, TenantContextHolder.get());
    }

    @Test
    void clearsTenantContextWhenRefreshFailsWithoutPreviousTenant() {
        FlinkJobTask task = task(101, 2);
        IllegalStateException failure = new IllegalStateException("Unable to persist status");
        doThrow(failure).when(JOB_INSTANCE_SERVICE).updateById(any(JobInstance.class));

        assertSame(failure, assertThrows(IllegalStateException.class, task::dealTask));
        assertNull(TenantContextHolder.get());
    }

    @Test
    void preservesTheTenantForManualRefresh() {
        TenantContextHolder.set(2);
        FlinkJobTask task = task(101, 2);

        assertTrue(task.dealTask());

        assertAll(
                () -> assertEquals(JobStatus.UNKNOWN.getValue(), persistedStatuses.get(101)),
                () -> assertEquals(2, TenantContextHolder.get()));
    }

    @Test
    void persistsFailedYarnSessionJobWithTenantFilteringEnabled() throws Exception {
        HttpServer flink = failedJobServer();
        Boolean metricsEnabled =
                SystemConfiguration.getInstances().getMetricsSysEnable().getValue();
        Integer resendInterval =
                SystemConfiguration.getInstances().getJobReSendDiffSecond().getValue();
        SystemConfiguration.getInstances().getMetricsSysEnable().setValue(false);
        SystemConfiguration.getInstances().getJobReSendDiffSecond().setValue(60);
        UnpooledDataSource dataSource =
                new UnpooledDataSource("org.h2.Driver", "jdbc:h2:mem:" + UUID.randomUUID(), "sa", "");

        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute(
                    "CREATE TABLE dinky_job_instance (id INT PRIMARY KEY, tenant_id INT, status VARCHAR(32))");
            statement.execute("INSERT INTO dinky_job_instance VALUES (101, 2, 'RUNNING'), (102, 1, 'RUNNING')");
            Configuration configuration =
                    new Configuration(new Environment("test", new JdbcTransactionFactory(), dataSource));
            configuration.addInterceptor(
                    new MybatisPlusConfig(new MybatisPlusFillProperties()).mybatisPlusInterceptor());
            configuration.addMapper(JobStatusMapper.class);

            try (SqlSession session =
                    new SqlSessionFactoryBuilder().build(configuration).openSession(true)) {
                JobStatusMapper mapper = session.getMapper(JobStatusMapper.class);
                TenantContextHolder.set(1);
                assertFalse(TenantContextHolder.isIgnoreTenant());
                FlinkJobTask task = task(101, 2);
                JobInfoDetail detail = task.getJobInfoDetail();
                JobInstance instance = detail.getInstance();
                instance.setTaskId(10);
                instance.setName("failed-job");
                instance.setJid("job-101");
                instance.setStatus(JobStatus.FAILED.getValue());
                assertEquals(0, mapper.updateStatus(instance), "Another tenant must not be able to update this row");
                instance.setStatus(JobStatus.RUNNING.getValue());
                detail.setJobDataDto(JobDataDto.builder().id(101).tenantId(2).build());
                ClusterInstance cluster = new ClusterInstance();
                cluster.setName("yarn-session");
                cluster.setType(GatewayType.YARN_SESSION.getLongValue());
                cluster.setJobManagerHost("127.0.0.1:" + flink.getAddress().getPort());
                cluster.setHosts(cluster.getJobManagerHost());
                detail.setClusterInstance(cluster);
                doAnswer(invocation -> mapper.updateStatus(invocation.getArgument(0)) == 1)
                        .when(JOB_INSTANCE_SERVICE)
                        .updateById(any(JobInstance.class));

                assertTrue(task.dealTask());

                assertAll(
                        () -> assertEquals(JobStatus.FAILED.getValue(), persistedStatus(connection, 101)),
                        () -> assertEquals(JobStatus.RUNNING.getValue(), persistedStatus(connection, 102)),
                        () -> assertEquals(1, TenantContextHolder.get()),
                        () -> assertFalse(TenantContextHolder.isIgnoreTenant()));
            }
        } finally {
            flink.stop(0);
            SystemConfiguration.getInstances().getMetricsSysEnable().setValue(metricsEnabled);
            SystemConfiguration.getInstances().getJobReSendDiffSecond().setValue(resendInterval);
        }
    }

    private static HttpServer failedJobServer() throws IOException {
        Map<String, String> responses = new HashMap<>();
        responses.put(
                "/jobs/job-101",
                "{\"jid\":\"job-101\",\"name\":\"failed-job\",\"state\":\"FAILED\","
                        + "\"start-time\":1000,\"end-time\":2000,\"duration\":1000,\"vertices\":[],\"plan\":{\"nodes\":[]}}");
        responses.put("/jobs/job-101/config", "{\"jid\":\"job-101\",\"name\":\"failed-job\",\"execution-config\":{}}");
        responses.put("/jobs/job-101/checkpoints", "{\"errors\":[]}");
        responses.put("/jobs/job-101/checkpoints/config", "{\"errors\":[]}");
        responses.put(
                "/jobs/job-101/exceptions",
                "{\"all-exceptions\":[],\"root-exception\":\"\",\"timestamp\":2000,\"truncated\":false,"
                        + "\"exceptionHistory\":{\"entries\":[],\"truncated\":false}}");
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/jobs/job-101", exchange -> {
            String response = responses.get(exchange.getRequestURI().getPath());
            boolean validRequest = "GET".equals(exchange.getRequestMethod()) && response != null;
            byte[] body = (validRequest ? response : "{\"errors\":[\"Unexpected request\"]}")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(validRequest ? 200 : 404, body.length);
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        return server;
    }

    private static String persistedStatus(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement("SELECT status FROM dinky_job_instance WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                assertTrue(rows.next());
                return rows.getString("status");
            }
        }
    }

    interface JobStatusMapper {
        @Update("UPDATE dinky_job_instance SET status = #{status} WHERE id = #{id}")
        int updateStatus(JobInstance instance);
    }

    private FlinkJobTask task(int id, int tenantId) {
        JobInstance instance = new JobInstance();
        instance.setId(id);
        instance.setTenantId(tenantId);
        instance.setStatus(JobStatus.RUNNING.getValue());
        jobTenants.put(id, tenantId);
        persistedStatuses.put(id, JobStatus.RUNNING.getValue());

        // Missing cluster metadata is a terminal refresh path that needs no Flink or YARN server.
        JobInfoDetail detail = new JobInfoDetail(id);
        detail.setInstance(instance);
        FlinkJobTask task = new FlinkJobTask();
        task.setJobInfoDetail(detail);
        return task;
    }
}
