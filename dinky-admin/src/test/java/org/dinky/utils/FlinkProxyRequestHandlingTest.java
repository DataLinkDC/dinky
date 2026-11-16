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

import org.dinky.controller.FlinkProxyController;
import org.dinky.controller.JobInstanceController;
import org.dinky.data.model.ClusterInstance;
import org.dinky.data.result.Result;
import org.dinky.service.JobInstanceService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.spring.SaTokenContextForSpring;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpRequest;

/**
 * Tests Flink proxy and JobManager address request handling.
 */
public class FlinkProxyRequestHandlingTest {

    private static final String KNOWN_ADDRESS = "127.0.0.1:8081";

    @Before
    public void setUp() {
        bindRequest();
    }

    @After
    public void tearDown() {
        try {
            if (StpUtil.isLogin()) {
                StpUtil.logout();
            }
        } catch (Exception ignored) {
            // ignore
        }
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void proxyWithoutLoginSessionIsRejected() throws Exception {
        RecordingProxyServlet servlet = new RecordingProxyServlet();
        MockHttpServletRequest request = proxyRequest(KNOWN_ADDRESS, "job-1", null);
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        Assert.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        Assert.assertTrue(response.getErrorMessage().contains("login session is required"));
        Assert.assertFalse(servlet.forwarded);
    }

    @Test
    public void proxyWithLoginSessionAndKnownClusterAddressIsAllowed() throws Exception {
        String token = loginAndToken();
        RecordingProxyServlet servlet = new RecordingProxyServlet();
        MockHttpServletRequest request = proxyRequest(KNOWN_ADDRESS, "job-1", token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        Assert.assertNotEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        Assert.assertNotEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        Assert.assertTrue(servlet.forwarded);
    }

    @Test
    public void proxyWithUnknownAddressIsRejected() throws Exception {
        String token = loginAndToken();
        RecordingProxyServlet servlet = new RecordingProxyServlet();
        MockHttpServletRequest request = proxyRequest("10.0.0.9:9999", "job-1", token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        Assert.assertTrue(response.getErrorMessage().contains("address is not a known cluster address"));
        Assert.assertFalse(servlet.forwarded);
    }

    @Test
    public void proxyWithMalformedAddressIsRejected() throws Exception {
        String token = loginAndToken();
        RecordingProxyServlet servlet = new RecordingProxyServlet();
        MockHttpServletRequest request = proxyRequest("http://user@example.com:8081", "job-1", token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        Assert.assertTrue(response.getErrorMessage().contains("address is malformed"));
        Assert.assertFalse(servlet.forwarded);
    }

    @Test
    public void flinkPathWithoutLoginSessionIsRejected() throws Exception {
        RecordingFlinkProxyController controller = new RecordingFlinkProxyController();
        MockHttpServletRequest request = flinkPathRequest(KNOWN_ADDRESS, null);
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.proxyUba(request, response);

        Assert.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        Assert.assertTrue(response.getErrorMessage().contains("login session is required"));
        Assert.assertFalse(controller.forwarded);
    }

    @Test
    public void flinkPathWithLoginSessionAndKnownClusterAddressIsAllowed() throws Exception {
        String token = loginAndToken();
        RecordingFlinkProxyController controller = new RecordingFlinkProxyController();
        MockHttpServletRequest request = flinkPathRequest(KNOWN_ADDRESS, token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.proxyUba(request, response);

        Assert.assertNotEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        Assert.assertNotEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        Assert.assertTrue(controller.forwarded);
    }

    @Test
    public void flinkPathWithUnknownAddressIsRejected() throws Exception {
        String token = loginAndToken();
        RecordingFlinkProxyController controller = new RecordingFlinkProxyController();
        MockHttpServletRequest request = flinkPathRequest("10.0.0.9:9999", token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.proxyUba(request, response);

        Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        Assert.assertTrue(response.getErrorMessage().contains("address is not a known cluster address"));
        Assert.assertFalse(controller.forwarded);
    }

    @Test
    public void flinkPathWithMalformedAddressIsRejected() throws Exception {
        String token = loginAndToken();
        RecordingFlinkProxyController controller = new RecordingFlinkProxyController();
        MockHttpServletRequest request = flinkPathRequest("http://user@example.com:8081", token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.proxyUba(request, response);

        Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        Assert.assertTrue(response.getErrorMessage().contains("address is malformed"));
        Assert.assertFalse(controller.forwarded);
    }

    @Test
    public void jobManagerLogWithUnknownAddressIsRejected() {
        JobInstanceController controller =
                new JobInstanceController(Mockito.mock(JobInstanceService.class), new StubAddressLoader());
        Result<String> result = controller.getJobManagerLog("10.0.0.9:9999");
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getMsg().contains("address is not a known cluster address"));
    }

    @Test
    public void jobManagerLogWithMalformedAddressIsRejected() {
        JobInstanceController controller =
                new JobInstanceController(Mockito.mock(JobInstanceService.class), new StubAddressLoader());
        Result<String> result = controller.getJobManagerLog(":::bad");
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getMsg().contains("address is malformed"));
    }

    @Test
    public void knownClusterAddressMatchesStoredHostsAndJobManagerHost() {
        ClusterInstance cluster = new ClusterInstance();
        cluster.setHosts("10.0.0.1:8081,10.0.0.2:8081");
        cluster.setJobManagerHost("10.0.0.1:8081");
        List<String> known = FlinkProxySupport.collectClusterAddresses(Collections.singletonList(cluster));

        Assert.assertTrue(FlinkProxySupport.isKnownClusterAddress("10.0.0.1:8081", known));
        Assert.assertTrue(FlinkProxySupport.isKnownClusterAddress("10.0.0.2:8081", known));
        Assert.assertTrue(FlinkProxySupport.isKnownClusterAddress("http://10.0.0.1:8081", known));
        Assert.assertTrue(FlinkProxySupport.isKnownClusterAddress("10.0.0.1:8081,10.0.0.2:8081", known));
        Assert.assertFalse(FlinkProxySupport.isKnownClusterAddress("10.0.0.9:9999", known));
    }

    @Test
    public void extractTargetAddressReadsHostAndPortFromPath() {
        Assert.assertEquals(KNOWN_ADDRESS, FlinkProxySupport.extractTargetAddress("127.0.0.1:8081"));
        Assert.assertEquals(KNOWN_ADDRESS, FlinkProxySupport.extractTargetAddress("127.0.0.1:8081/jobs"));
        Assert.assertEquals(KNOWN_ADDRESS, FlinkProxySupport.extractTargetAddress("http://127.0.0.1:8081/jobs"));
    }

    private static String loginAndToken() {
        bindRequest();
        StpUtil.login(1);
        return StpUtil.getTokenValue();
    }

    private static MockHttpServletRequest proxyRequest(String authority, String jobId, String token) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/flink_web/proxy");
        request.setRequestURI("/api/flink_web/proxy");
        request.setQueryString("_authority=" + authority + "&_jid=" + jobId);
        request.addParameter("_authority", authority);
        request.addParameter("_jid", jobId);
        if (token != null) {
            request.addHeader("dinky-token", token);
            request.setCookies(new Cookie("dinky-token", token));
        }
        return request;
    }

    private static MockHttpServletRequest flinkPathRequest(String address, String token) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/flink/" + address + "/");
        request.setRequestURI("/api/flink/" + address + "/");
        if (token != null) {
            request.addHeader("dinky-token", token);
            request.setCookies(new Cookie("dinky-token", token));
        }
        return request;
    }

    private static void bindRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/flink_web/proxy");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
        SaManager.setSaTokenContext(new SaTokenContextForSpring());
    }

    private static final class StubAddressLoader extends KnownClusterAddressLoader {
        private final List<String> addresses = Collections.singletonList(KNOWN_ADDRESS);

        private StubAddressLoader() {
            super(null);
        }

        @Override
        public List<String> load() {
            return addresses;
        }
    }

    private static final class RecordingProxyServlet extends FlinkWebURITemplateProxyServlet {
        private boolean forwarded;

        private RecordingProxyServlet() {
            this.targetUriTemplate = "http://{_authority}/#/job/running/{_jid}/overview";
        }

        @Override
        protected Collection<String> loadKnownClusterAddresses() {
            return Collections.singletonList(KNOWN_ADDRESS);
        }

        @Override
        protected void forwardToTarget(
                javax.servlet.http.HttpServletRequest servletRequest,
                javax.servlet.http.HttpServletResponse servletResponse,
                String targetUriTemplateCurrent) {
            this.forwarded = true;
        }
    }

    private static final class RecordingFlinkProxyController extends FlinkProxyController {
        private boolean forwarded;

        private RecordingFlinkProxyController() {
            super(new StubAddressLoader());
        }

        @Override
        protected Collection<String> loadKnownClusterAddresses() {
            return Collections.singletonList(KNOWN_ADDRESS);
        }

        @Override
        protected void executeProxy(HttpRequest httpRequest, HttpServletResponse resp) {
            this.forwarded = true;
        }
    }
}
