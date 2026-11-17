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

package org.dinky.configure;

import org.dinky.controller.APIController;
import org.dinky.data.result.Result;
import org.dinky.service.JobInstanceService;
import org.dinky.service.TaskService;

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
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.spring.SaTokenContextForSpring;
import cn.dev33.satoken.stp.StpUtil;

/**
 * Tests that OpenAPI requests use the same login session as other API requests.
 */
public class OpenApiRequestHandlingTest {

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
    public void openApiRequestWithoutLoginIsRejected() throws Exception {
        MockHttpServletRequest request = bindRequest("/openapi/version");
        MockHttpServletResponse response = currentResponse();
        HandlerInterceptor interceptor = saInterceptor();

        try {
            interceptor.preHandle(request, response, new Object());
            Assert.fail("OpenAPI requests without a login session should not be processed");
        } catch (NotLoginException ex) {
            Assert.assertFalse(StpUtil.isLogin());
            Assert.assertNull(StpUtil.getLoginIdDefaultNull());
        }
    }

    @Test
    public void openApiVersionSucceedsWithLogin() throws Exception {
        MockHttpServletRequest request = bindRequest("/openapi/version");
        MockHttpServletResponse response = currentResponse();
        APIController controller = new APIController((TaskService) null, (JobInstanceService) null);
        HandlerMethod handlerMethod = new HandlerMethod(controller, "getVersionInfo");
        HandlerInterceptor interceptor = saInterceptor();

        StpUtil.login(1);
        Assert.assertTrue(StpUtil.isLogin());
        Assert.assertTrue(interceptor.preHandle(request, response, handlerMethod));

        Result<String> result = controller.getVersionInfo();
        Assert.assertTrue(result.isSuccess());
        Assert.assertNotNull(result.getData());
    }

    @Test
    public void apiControllerRequiresLoginSession() {
        Assert.assertNotNull(APIController.class.getAnnotation(SaCheckLogin.class));
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

    private static HandlerInterceptor saInterceptor() {
        InterceptorRegistry registry = new InterceptorRegistry();
        new AppConfig().addInterceptors(registry);
        for (InterceptorRegistration registration : registry.getInterceptors()) {
            if (registration.getInterceptor() instanceof SaInterceptor) {
                return registration.getInterceptor();
            }
        }
        throw new IllegalStateException("SaInterceptor is not registered");
    }
}
