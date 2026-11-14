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

import org.dinky.data.exception.BusException;
import org.dinky.data.model.ResourcesModelEnum;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.result.Result;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.spring.SaTokenContextForSpring;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;

/**
 * Tests local resource upload and download request handling.
 */
public class ResourceUploadHandlingTest {

    private static final String TEST_TOKEN = "test-resource-token-0001";
    private static final String OTHER_TOKEN = "test-resource-token-0002";

    private SystemConfiguration configuration;
    private String originalToken;
    private Boolean originalResourcesEnable;
    private ResourcesModelEnum originalResourcesModel;
    private String originalBasePath;
    private Path tempDir;

    @Before
    public void setUp() throws Exception {
        configuration = SystemConfiguration.getInstances();
        originalToken = configuration.getDinkyToken().getValue();
        originalResourcesEnable = configuration.getResourcesEnable().getValue();
        originalResourcesModel = configuration.getResourcesModel().getValue();
        originalBasePath = configuration.getResourcesUploadBasePath().getValue();
        tempDir = Files.createTempDirectory("dinky-resource-upload-test");
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
        if (configuration != null) {
            configuration.getDinkyToken().setValue(originalToken);
            configuration.getResourcesEnable().setValue(originalResourcesEnable);
            configuration.getResourcesModel().setValue(originalResourcesModel);
            configuration.getResourcesUploadBasePath().setValue(originalBasePath);
        }
        if (tempDir != null) {
            FileUtil.del(tempDir.toFile());
        }
    }

    @Test
    public void uploadWithoutLoginOrTokenIsRejected() {
        prepareLocalResourceDirectory();
        bindRequest("/download/uploadFromRsByLocal");

        Result<Void> result =
                new DownloadController().uploadFromRs(insidePath("sample.txt"), sampleFile(), null);

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("token is not correct", result.getMsg());
        Assert.assertFalse(new File(insidePath("sample.txt")).exists());
    }

    @Test
    public void uploadWithFactoryDefaultTokenIsRejected() {
        prepareLocalResourceDirectory();
        bindRequest("/download/uploadFromRsByLocal");
        String factoryToken = configuration.getDinkyToken().getDefaultValue();
        configuration.getDinkyToken().setValue(factoryToken);

        Result<Void> result = new DownloadController()
                .uploadFromRs(insidePath("sample.txt"), sampleFile(), factoryToken);

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("token is not correct", result.getMsg());
        Assert.assertFalse(new File(insidePath("sample.txt")).exists());
        Assert.assertEquals(factoryToken, configuration.getDinkyToken().getValue());
    }

    @Test
    public void uploadWithInvalidTokenIsRejected() {
        prepareLocalResourceDirectory();
        bindRequest("/download/uploadFromRsByLocal");
        configuration.getDinkyToken().setValue(TEST_TOKEN);

        Result<Void> result = new DownloadController()
                .uploadFromRs(insidePath("sample.txt"), sampleFile(), OTHER_TOKEN);

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("token is not correct", result.getMsg());
        Assert.assertFalse(new File(insidePath("sample.txt")).exists());
        Assert.assertEquals(TEST_TOKEN, configuration.getDinkyToken().getValue());
    }

    @Test
    public void uploadWithLoginSessionAllowsPathInsideDirectory() throws Exception {
        prepareLocalResourceDirectory();
        bindRequest("/download/uploadFromRsByLocal");
        StpUtil.login(1);
        Assert.assertTrue(StpUtil.isLogin());

        File dest = new File(insidePath("login-sample.txt"));
        Result<Void> result = new DownloadController().uploadFromRs(dest.getPath(), sampleFile(), null);

        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(dest.isFile());
        Assert.assertEquals("hello-resource", FileUtil.readUtf8String(dest));
    }

    @Test
    public void uploadWithConfiguredTokenAllowsPathInsideDirectory() throws Exception {
        prepareLocalResourceDirectory();
        bindRequest("/download/uploadFromRsByLocal");
        configuration.getDinkyToken().setValue(TEST_TOKEN);

        File dest = new File(insidePath("token-sample.txt"));
        Result<Void> result =
                new DownloadController().uploadFromRs(dest.getPath(), sampleFile(), TEST_TOKEN);

        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(dest.isFile());
        Assert.assertEquals("hello-resource", FileUtil.readUtf8String(dest));
        Assert.assertEquals(TEST_TOKEN, configuration.getDinkyToken().getValue());
    }

    @Test
    public void uploadWithPathOutsideDirectoryIsRejected() throws Exception {
        prepareLocalResourceDirectory();
        bindRequest("/download/uploadFromRsByLocal");
        configuration.getDinkyToken().setValue(TEST_TOKEN);
        File outside = outsideFile();

        Result<Void> result =
                new DownloadController().uploadFromRs(outside.getPath(), sampleFile(), TEST_TOKEN);

        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals("path is invalid", result.getMsg());
        Assert.assertFalse(outside.exists());
        Assert.assertEquals(TEST_TOKEN, configuration.getDinkyToken().getValue());
    }

    @Test
    public void downloadWithoutLoginOrTokenIsRejected() {
        prepareLocalResourceDirectory();
        bindRequest("/download/downloadFromRs");
        MockHttpServletResponse response = currentResponse();

        try {
            new DownloadController().downloadFromRs(insidePath("sample.txt"), response, null);
            Assert.fail("Download requests should require a login session or a configured token");
        } catch (BusException ex) {
            Assert.assertEquals("token is not correct", ex.getMessage());
        }
    }

    @Test
    public void downloadWithFactoryDefaultTokenIsRejected() {
        prepareLocalResourceDirectory();
        bindRequest("/download/downloadFromRs");
        String factoryToken = configuration.getDinkyToken().getDefaultValue();
        configuration.getDinkyToken().setValue(factoryToken);
        MockHttpServletResponse response = currentResponse();

        try {
            new DownloadController().downloadFromRs(insidePath("sample.txt"), response, factoryToken);
            Assert.fail("Download requests should not accept the factory default token");
        } catch (BusException ex) {
            Assert.assertEquals("token is not correct", ex.getMessage());
        }
        Assert.assertEquals(factoryToken, configuration.getDinkyToken().getValue());
    }

    @Test
    public void downloadWithLoginSessionAllowsPathInsideDirectory() throws Exception {
        prepareLocalResourceDirectory();
        File source = new File(insidePath("download-sample.txt"));
        FileUtil.writeUtf8String("download-resource", source);
        bindRequest("/download/downloadFromRs");
        StpUtil.login(1);
        MockHttpServletResponse response = currentResponse();

        new DownloadController().downloadFromRs(source.getPath(), response, null);

        Assert.assertEquals("download-resource", response.getContentAsString());
    }

    @Test
    public void downloadWithConfiguredTokenAllowsPathInsideDirectory() throws Exception {
        prepareLocalResourceDirectory();
        File source = new File(insidePath("download-token-sample.txt"));
        FileUtil.writeUtf8String("download-resource", source);
        bindRequest("/download/downloadFromRs");
        configuration.getDinkyToken().setValue(TEST_TOKEN);
        MockHttpServletResponse response = currentResponse();

        new DownloadController().downloadFromRs(source.getPath(), response, TEST_TOKEN);

        Assert.assertEquals("download-resource", response.getContentAsString());
        Assert.assertEquals(TEST_TOKEN, configuration.getDinkyToken().getValue());
    }

    @Test
    public void downloadWithPathOutsideDirectoryIsRejected() throws Exception {
        prepareLocalResourceDirectory();
        File outside = outsideFile();
        FileUtil.writeUtf8String("outside-resource", outside);
        bindRequest("/download/downloadFromRs");
        configuration.getDinkyToken().setValue(TEST_TOKEN);
        MockHttpServletResponse response = currentResponse();

        try {
            new DownloadController().downloadFromRs(outside.getPath(), response, TEST_TOKEN);
            Assert.fail("Download requests should stay inside the resource directory");
        } catch (BusException ex) {
            Assert.assertEquals("path is invalid", ex.getMessage());
        }
        Assert.assertEquals(0, response.getContentAsByteArray().length);
        Assert.assertEquals(TEST_TOKEN, configuration.getDinkyToken().getValue());
        FileUtil.del(outside);
    }

    private void prepareLocalResourceDirectory() {
        configuration.getResourcesEnable().setValue(true);
        configuration.getResourcesModel().setValue(ResourcesModelEnum.LOCAL);
        configuration.getResourcesUploadBasePath().setValue(tempDir.toAbsolutePath().toString());
    }

    private String insidePath(String name) {
        return new File(tempDir.toFile(), name).getPath();
    }

    private File outsideFile() throws Exception {
        File parent = tempDir.toFile().getCanonicalFile().getParentFile();
        return new File(parent, "dinky-resource-upload-outside.txt").getCanonicalFile();
    }

    private static MockMultipartFile sampleFile() {
        return new MockMultipartFile(
                "file", "sample.txt", "text/plain", "hello-resource".getBytes(StandardCharsets.UTF_8));
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
}
