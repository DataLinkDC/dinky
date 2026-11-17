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

package org.dinky.ws;

import org.dinky.context.EngineContextHolder;
import org.dinky.data.model.ClusterInstance;
import org.dinky.gateway.SqlClientOptions;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

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
import cn.hutool.core.lang.Dict;

/**
 * Tests SQL Gateway handshake and connection handling.
 */
public class SqlGatewayHandshakeTest {

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
    public void openWithoutLoginSessionDoesNotStartClient() throws Exception {
        RecordingSqlGateway gateway = new RecordingSqlGateway();
        Session session = openSession(Collections.singletonMap("connectAddress", KNOWN_ADDRESS), null);

        gateway.onOpen(session);

        Assert.assertFalse(gateway.started);
        Assert.assertNull(gateway.startedOptions);
        Assert.assertFalse(gateway.fragmentsLoaded);
        Assert.assertFalse(gateway.heartbeatStarted);
        assertClosedWithReason(session, "login session is required");
    }

    @Test
    public void openWithLoginSessionCompletesHandshake() throws Exception {
        bindRequest();
        StpUtil.login(1);
        String token = StpUtil.getTokenValue();
        Assert.assertTrue(SqlGatewayHandshake.hasValidLoginSession(token));

        RecordingSqlGateway gateway = new RecordingSqlGateway();
        gateway.fragments.put("db_name", "demo_db");
        Map<String, String> params = baseParams();
        params.put("initSql", "SELECT * FROM ${db_name}");
        params.put("token", token);
        Session session = openSession(params, null);

        gateway.onOpen(session);

        Assert.assertTrue(gateway.started);
        Assert.assertNotNull(gateway.startedOptions);
        Assert.assertEquals(KNOWN_ADDRESS, gateway.startedOptions.getConnectAddress());
        Assert.assertEquals("SELECT * FROM demo_db", gateway.startedOptions.getInitSql());
        Assert.assertTrue(gateway.fragmentsLoaded);
        Mockito.verify(session, Mockito.never()).close();
        Mockito.verify(session, Mockito.never()).close(Mockito.any(CloseReason.class));
    }

    @Test
    public void openWithCookieLoginSessionCompletesHandshake() throws Exception {
        bindRequest();
        StpUtil.login(1);
        String token = StpUtil.getTokenValue();

        RecordingSqlGateway gateway = new RecordingSqlGateway();
        Map<String, String> params = baseParams();
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Cookie", Collections.singletonList("dinky-token=" + token));
        Session session = openSession(params, headers);

        gateway.onOpen(session);

        Assert.assertTrue(gateway.started);
        Assert.assertEquals(KNOWN_ADDRESS, gateway.startedOptions.getConnectAddress());
        Mockito.verify(session, Mockito.never()).close(Mockito.any(CloseReason.class));
    }

    @Test
    public void openWithUnknownConnectAddressDoesNotStartClient() throws Exception {
        bindRequest();
        StpUtil.login(1);
        String token = StpUtil.getTokenValue();

        RecordingSqlGateway gateway = new RecordingSqlGateway();
        Map<String, String> params = baseParams();
        params.put("connectAddress", "10.0.0.9:9999");
        params.put("token", token);
        Session session = openSession(params, null);

        gateway.onOpen(session);

        Assert.assertFalse(gateway.started);
        Assert.assertFalse(gateway.fragmentsLoaded);
        assertClosedWithReason(session, "connect address is not a known cluster address");
    }

    @Test
    public void handshakeInitSqlDoesNotApplyExpressionEvaluation() throws Exception {
        bindRequest();
        StpUtil.login(1);
        String token = StpUtil.getTokenValue();
        Dict context = EngineContextHolder.getEngineContext();
        Object previous = context.get("markerUtil");
        context.set("markerUtil", HandshakeMarker.class);

        try {
            RecordingSqlGateway gateway = new RecordingSqlGateway();
            Map<String, String> params = baseParams();
            params.put("initSql", "SELECT '${markerUtil.touch()}' FROM dual");
            params.put("token", token);
            Session session = openSession(params, null);

            gateway.onOpen(session);

            Assert.assertTrue(gateway.started);
            Assert.assertEquals("SELECT '${markerUtil.touch()}' FROM dual", gateway.startedOptions.getInitSql());
            Assert.assertFalse(HandshakeMarker.touched);
        } finally {
            if (previous == null) {
                context.remove("markerUtil");
            } else {
                context.set("markerUtil", previous);
            }
            HandshakeMarker.touched = false;
        }
    }

    @Test
    public void resolveTokenReadsQueryHeaderAndCookie() {
        Map<String, List<String>> query = new HashMap<>();
        query.put("token", Collections.singletonList("query-token"));
        Assert.assertEquals("query-token", SqlGatewayHandshake.resolveToken(query, Collections.emptyMap()));

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("dinky-token", Collections.singletonList("header-token"));
        Assert.assertEquals("header-token", SqlGatewayHandshake.resolveToken(Collections.emptyMap(), headers));

        Map<String, List<String>> cookieHeaders = new HashMap<>();
        cookieHeaders.put("Cookie", Collections.singletonList("tenantId=1; dinky-token=cookie-token"));
        Assert.assertEquals(
                "cookie-token", SqlGatewayHandshake.resolveToken(Collections.emptyMap(), cookieHeaders));
    }

    @Test
    public void knownClusterAddressMatchesStoredHosts() {
        ClusterInstance cluster = new ClusterInstance();
        cluster.setHosts("10.0.0.1:8081,10.0.0.2:8081");
        cluster.setJobManagerHost("10.0.0.1:8081");
        List<String> known = SqlGatewayHandshake.collectClusterAddresses(Collections.singletonList(cluster));

        Assert.assertTrue(SqlGatewayHandshake.isKnownClusterAddress("10.0.0.1:8081,10.0.0.2:8081", known));
        Assert.assertTrue(SqlGatewayHandshake.isKnownClusterAddress("10.0.0.2:8081", known));
        Assert.assertFalse(SqlGatewayHandshake.isKnownClusterAddress("8.8.8.8:8081", known));
        Assert.assertFalse(SqlGatewayHandshake.isKnownClusterAddress("", known));
    }

    private static Map<String, String> baseParams() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("cols", "80");
        params.put("rows", "24");
        params.put("mode", "MODE_EMBEDDED");
        params.put("sessionId", "s1");
        params.put("initSql", "");
        params.put("connectAddress", KNOWN_ADDRESS);
        return params;
    }

    private static Session openSession(Map<String, String> params, Map<String, List<String>> headers)
            throws IOException {
        Session session = Mockito.mock(Session.class);
        Map<String, List<String>> query = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            query.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }
        Map<String, Object> userProperties = new HashMap<>();
        if (headers != null) {
            userProperties.put(SqlGatewayConfigurator.HANDSHAKE_HEADERS, headers);
        }
        Mockito.when(session.getRequestParameterMap()).thenReturn(query);
        Mockito.when(session.getUserProperties()).thenReturn(userProperties);
        Mockito.when(session.isOpen()).thenReturn(true);
        Mockito.when(session.getBasicRemote()).thenReturn(Mockito.mock(RemoteEndpoint.Basic.class));
        return session;
    }

    private static void assertClosedWithReason(Session session, String reason) throws IOException {
        AtomicReference<CloseReason> captured = new AtomicReference<>();
        Mockito.verify(session)
                .close(Mockito.argThat(closeReason -> {
                    captured.set(closeReason);
                    return true;
                }));
        Assert.assertNotNull(captured.get());
        Assert.assertEquals(reason, captured.get().getReasonPhrase());
        Assert.assertEquals(CloseReason.CloseCodes.CANNOT_ACCEPT, captured.get().getCloseCode());
    }

    private static void bindRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/ws/sql-gateway/");
        request.setServletPath("/api/ws/sql-gateway/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
        SaManager.setSaTokenContext(new SaTokenContextForSpring());
    }

    public static class HandshakeMarker {
        static boolean touched = false;

        public static String touch() {
            touched = true;
            return "touched";
        }
    }

    private static class RecordingSqlGateway extends SqlGateway {
        private final List<String> knownAddresses = Collections.singletonList(KNOWN_ADDRESS);
        private final Map<String, String> fragments = new LinkedHashMap<>();
        private boolean started;
        private boolean fragmentsLoaded;
        private boolean heartbeatStarted;
        private SqlClientOptions startedOptions;

        @Override
        protected void startClient(SqlClientOptions options) {
            this.started = true;
            this.startedOptions = options;
        }

        @Override
        protected void startHeartbeat() {
            this.heartbeatStarted = true;
        }

        @Override
        protected List<String> loadKnownClusterAddresses() {
            return knownAddresses;
        }

        @Override
        protected Map<String, String> loadFragmentVariables(String initSql) {
            this.fragmentsLoaded = true;
            Map<String, String> selected = new LinkedHashMap<>();
            for (String name : SqlGatewayHandshake.findPlaceholderNames(initSql)) {
                if (fragments.containsKey(name)) {
                    selected.put(name, fragments.get(name));
                }
            }
            return selected;
        }

        @Override
        protected String prepareInitSql(String initSql) {
            Map<String, String> selected = loadFragmentVariables(initSql);
            String result = initSql;
            for (Map.Entry<String, String> entry : selected.entrySet()) {
                result = result.replace("${" + entry.getKey() + "}", entry.getValue());
            }
            return result;
        }
    }
}
