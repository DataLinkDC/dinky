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

package org.dinky.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.hutool.core.io.FileUtil;

/**
 * Tests system file reading path handling.
 */
public class SystemFileReadHelperTest {

    private static final String INSIDE_CONTENT = "inside-log-content";
    private static final String OUTSIDE_CONTENT = "outside-file-content";

    private Path tempDir;
    private File rootDir;
    private File insideFile;
    private File outsideDir;
    private File outsideFile;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("dinky-system-read-file-test");
        rootDir = tempDir.resolve("logs").toFile();
        Assert.assertTrue(rootDir.mkdirs());
        insideFile = new File(rootDir, "dinky.log");
        Files.write(insideFile.toPath(), INSIDE_CONTENT.getBytes(StandardCharsets.UTF_8));
        outsideDir = tempDir.resolve("outside").toFile();
        Assert.assertTrue(outsideDir.mkdirs());
        outsideFile = new File(outsideDir, "outside.log");
        Files.write(outsideFile.toPath(), OUTSIDE_CONTENT.getBytes(StandardCharsets.UTF_8));
    }

    @After
    public void tearDown() {
        if (tempDir != null) {
            FileUtil.del(tempDir.toFile());
        }
    }

    @Test
    public void readFileInsideIntendedDirectoryIsAllowed() throws IOException {
        String relativeResult = SystemFileReadHelper.readFile(rootDir, "dinky.log");
        Assert.assertTrue(relativeResult.contains(INSIDE_CONTENT));
        Assert.assertFalse(relativeResult.contains(OUTSIDE_CONTENT));

        String absoluteResult = SystemFileReadHelper.readFile(rootDir, insideFile.getCanonicalPath());
        Assert.assertTrue(absoluteResult.contains(INSIDE_CONTENT));
        Assert.assertFalse(absoluteResult.contains(OUTSIDE_CONTENT));
    }

    @Test
    public void readFileWithParentDirectoryPathIsRejected() {
        try {
            SystemFileReadHelper.readFile(rootDir, "../outside/outside.log");
            Assert.fail("Parent-directory paths should be rejected");
        } catch (Exception ex) {
            Assert.assertEquals(SystemFileReadHelper.INVALID_PATH_MESSAGE, ex.getMessage());
        }
        Assert.assertTrue(outsideFile.isFile());
    }

    @Test
    public void readFileWithAbsolutePathOutsideRootIsRejected() throws IOException {
        try {
            SystemFileReadHelper.readFile(rootDir, outsideFile.getCanonicalPath());
            Assert.fail("Absolute paths outside the intended directory should be rejected");
        } catch (Exception ex) {
            Assert.assertEquals(SystemFileReadHelper.INVALID_PATH_MESSAGE, ex.getMessage());
        }
        Assert.assertTrue(outsideFile.isFile());
    }

    @Test
    public void readFileWithWindowsDrivePrefixIsRejected() {
        try {
            SystemFileReadHelper.readFile(rootDir, "C:\\fakepath\\drive.log");
            Assert.fail("Windows drive prefix paths should be rejected");
        } catch (Exception ex) {
            Assert.assertEquals(SystemFileReadHelper.INVALID_PATH_MESSAGE, ex.getMessage());
        }
        Assert.assertTrue(outsideFile.isFile());
        Assert.assertTrue(insideFile.isFile());
    }

    @Test
    public void readFileWithBlankPathIsRejected() {
        try {
            SystemFileReadHelper.readFile(rootDir, "  ");
            Assert.fail("Blank paths should be rejected");
        } catch (Exception ex) {
            Assert.assertEquals(SystemFileReadHelper.INVALID_PATH_MESSAGE, ex.getMessage());
        }
    }
}
