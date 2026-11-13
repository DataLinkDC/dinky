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

package org.dinky.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.dinky.configure.AppConfig;
import org.dinky.data.model.Configuration;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.result.Result;
import org.dinky.service.SysConfigService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.spring.SaTokenContextForSpring;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.DesensitizedUtil;

/**
 * Tests the all-configuration query login session and overview display.
 */
public class SysConfigDisplayTest {

    private static final String SAMPLE_TOKEN = "plain-dinky-token-value";
    private static final String SAMPLE_DS_TOKEN = "plain-ds-token-value";
    private static final String SAMPLE_LDAP_PASSWORD = "plain-ldap-password";
    private static final String SAMPLE_OSS_ACCESS_KEY = "plain-oss-access-key";
    private static final String SAMPLE_OSS_SECRET_KEY = "plain-oss-secret-key";
    private static final String SAMPLE_MAVEN_PASSWORD = "plain-maven-password";

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
    public void getAllWithoutLoginSessionIsRejected() throws Exception {
        SysConfigController controller = new SysConfigController(overviewService());
        MockHttpServletRequest request = bindRequest("/api/sysConfig/getAll");
        MockHttpServletResponse response = currentResponse();
        HandlerMethod handlerMethod = new HandlerMethod(controller, "getAll");
        HandlerInterceptor interceptor = new SaInterceptor();

        try {
            interceptor.preHandle(request, response, handlerMethod);
            Assert.fail("The all-configuration query should require a login session");
        } catch (NotLoginException ex) {
            Assert.assertFalse(StpUtil.isLogin());
        }
    }

    @Test
    public void getAllWithLoginSessionMasksSensitiveValues() throws Exception {
        SystemConfiguration configuration = SystemConfiguration.getInstances();
        String originalToken = configuration.getDinkyToken().getValue();
        String originalDsToken = configuration.getDolphinschedulerToken().getValue();
        String originalLdapPassword = configuration.getLdapUserPassword().getValue();
        String originalAccessKey = configuration.getResourcesOssAccessKey().getValue();
        String originalSecretKey = configuration.getResourcesOssSecretKey().getValue();
        String originalMavenPassword = configuration.getMavenRepositoryPassword().getValue();
        try {
            configuration.getDinkyToken().setValue(SAMPLE_TOKEN);
            configuration.getDolphinschedulerToken().setValue(SAMPLE_DS_TOKEN);
            configuration.getLdapUserPassword().setValue(SAMPLE_LDAP_PASSWORD);
            configuration.getResourcesOssAccessKey().setValue(SAMPLE_OSS_ACCESS_KEY);
            configuration.getResourcesOssSecretKey().setValue(SAMPLE_OSS_SECRET_KEY);
            configuration.getMavenRepositoryPassword().setValue(SAMPLE_MAVEN_PASSWORD);

            SysConfigController controller = new SysConfigController(overviewService());
            MockHttpServletRequest request = bindRequest("/api/sysConfig/getAll");
            MockHttpServletResponse response = currentResponse();
            HandlerMethod handlerMethod = new HandlerMethod(controller, "getAll");
            HandlerInterceptor interceptor = new SaInterceptor();

            StpUtil.login(1);
            Assert.assertTrue(StpUtil.isLogin());
            Assert.assertTrue(interceptor.preHandle(request, response, handlerMethod));

            Result<Map<String, List<Configuration<?>>>> result = controller.getAll();
            Assert.assertTrue(result.isSuccess());
            Assert.assertNotNull(result.getData());

            Map<String, Object> displayed = flattenValues(result.getData());
            assertMasked(displayed, configuration.getDinkyToken().getKey(), SAMPLE_TOKEN);
            assertMasked(displayed, configuration.getDolphinschedulerToken().getKey(), SAMPLE_DS_TOKEN);
            assertMasked(displayed, configuration.getLdapUserPassword().getKey(), SAMPLE_LDAP_PASSWORD);
            assertMasked(displayed, configuration.getResourcesOssAccessKey().getKey(), SAMPLE_OSS_ACCESS_KEY);
            assertMasked(displayed, configuration.getResourcesOssSecretKey().getKey(), SAMPLE_OSS_SECRET_KEY);
            assertMasked(displayed, configuration.getMavenRepositoryPassword().getKey(), SAMPLE_MAVEN_PASSWORD);

            Assert.assertEquals(SAMPLE_TOKEN, configuration.getDinkyToken().getValue());
            Assert.assertEquals(SAMPLE_DS_TOKEN, configuration.getDolphinschedulerToken().getValue());
            Assert.assertEquals(SAMPLE_LDAP_PASSWORD, configuration.getLdapUserPassword().getValue());
            Assert.assertEquals(SAMPLE_OSS_ACCESS_KEY, configuration.getResourcesOssAccessKey().getValue());
            Assert.assertEquals(SAMPLE_OSS_SECRET_KEY, configuration.getResourcesOssSecretKey().getValue());
            Assert.assertEquals(SAMPLE_MAVEN_PASSWORD, configuration.getMavenRepositoryPassword().getValue());
        } finally {
            configuration.getDinkyToken().setValue(originalToken);
            configuration.getDolphinschedulerToken().setValue(originalDsToken);
            configuration.getLdapUserPassword().setValue(originalLdapPassword);
            configuration.getResourcesOssAccessKey().setValue(originalAccessKey);
            configuration.getResourcesOssSecretKey().setValue(originalSecretKey);
            configuration.getMavenRepositoryPassword().setValue(originalMavenPassword);
        }
    }

    @Test
    public void neededConfigQueryKeepsInitAccess() throws Exception {
        Assert.assertNotNull(SysConfigController.class.getAnnotation(SaCheckLogin.class));
        Method getAll = SysConfigController.class.getMethod("getAll");
        Method getNeededCfg = SysConfigController.class.getMethod("getNeededCfg");
        Method setInitConfig = SysConfigController.class.getMethod("setInitConfig", Map.class);
        Assert.assertNull(getAll.getAnnotation(SaIgnore.class));
        Assert.assertNotNull(getNeededCfg.getAnnotation(SaIgnore.class));
        Assert.assertNotNull(setInitConfig.getAnnotation(SaIgnore.class));

        InterceptorRegistry registry = new InterceptorRegistry();
        new AppConfig().addInterceptors(registry);
        boolean excluded = false;
        for (Object registration : interceptorRegistrations(registry)) {
            if (!(saInterceptorOf(registration) instanceof SaInterceptor)) {
                continue;
            }
            List<String> patterns = excludePathPatterns(registration);
            Assert.assertTrue(patterns.contains("/api/sysConfig/getNeededCfg"));
            Assert.assertTrue(patterns.contains("/api/sysConfig/setInitConfig"));
            Assert.assertFalse(patterns.contains("/api/sysConfig/getAll"));
            excluded = true;
        }
        Assert.assertTrue(excluded);
    }

    private static SysConfigService overviewService() {
        SysConfigService service = mock(SysConfigService.class);
        when(service.getAll()).thenReturn(SystemConfiguration.getInstances().getAllConfiguration());
        return service;
    }

    private static void assertMasked(Map<String, Object> displayed, String key, String original) {
        Assert.assertTrue("Missing configuration key: " + key, displayed.containsKey(key));
        Object value = displayed.get(key);
        Assert.assertEquals(DesensitizedUtil.password(original), value);
        Assert.assertNotEquals(original, value);
    }

    private static Map<String, Object> flattenValues(Map<String, List<Configuration<?>>> data) {
        Map<String, Object> values = new HashMap<>();
        for (List<Configuration<?>> group : data.values()) {
            for (Configuration<?> item : group) {
                values.put(item.getKey(), item.getValue());
            }
        }
        return values;
    }

    private static MockHttpServletRequest bindRequest(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        request.setServletPath(path);
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
        SaManager.setSaTokenContext(new SaTokenContextForSpring());
        return request;
    }

    private static MockHttpServletResponse currentResponse() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return (MockHttpServletResponse) attributes.getResponse();
    }

    @SuppressWarnings("unchecked")
    private static List<Object> interceptorRegistrations(InterceptorRegistry registry) throws Exception {
        Field field = InterceptorRegistry.class.getDeclaredField("registrations");
        field.setAccessible(true);
        return (List<Object>) field.get(registry);
    }

    private static Object saInterceptorOf(Object registration) throws Exception {
        Field field = InterceptorRegistration.class.getDeclaredField("interceptor");
        field.setAccessible(true);
        return field.get(registration);
    }

    @SuppressWarnings("unchecked")
    private static List<String> excludePathPatterns(Object registration) throws Exception {
        Field field = InterceptorRegistration.class.getDeclaredField("excludePatterns");
        field.setAccessible(true);
        List<String> patterns = (List<String>) field.get(registration);
        Assert.assertNotNull(patterns);
        return patterns;
    }
}
