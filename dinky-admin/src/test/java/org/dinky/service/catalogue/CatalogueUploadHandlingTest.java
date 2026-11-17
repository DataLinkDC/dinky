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

package org.dinky.service.catalogue;

import org.dinky.service.catalogue.CatalogueUploadHelper.ResolvedUploadPaths;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.hutool.core.io.FileUtil;

/**
 * Tests catalogue upload path handling.
 */
public class CatalogueUploadHandlingTest {

    private Path tempDir;
    private File targetDir;
    private File outsideDir;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("dinky-catalogue-upload-test");
        targetDir = tempDir.resolve("upload").toFile();
        Assert.assertTrue(targetDir.mkdirs());
        outsideDir = tempDir.resolve("outside").toFile();
        Assert.assertTrue(outsideDir.mkdirs());
    }

    @After
    public void tearDown() {
        if (tempDir != null) {
            FileUtil.del(tempDir.toFile());
        }
    }

    @Test
    public void uploadWithParentDirectoryNameIsRejected() throws IOException {
        File outsideZip = new File(outsideDir, "escaped.zip");
        byte[] content = zipWithEntry("project/demo.sql", "select 1;");

        try {
            CatalogueUploadHelper.uploadZipToDirectory(targetDir, "../outside/escaped.zip", content);
            Assert.fail("Parent-directory names should be rejected");
        } catch (IOException ex) {
            Assert.assertEquals(CatalogueUploadHelper.INVALID_NAME_MESSAGE, ex.getMessage());
        }

        Assert.assertFalse(outsideZip.exists());
        Assert.assertTrue(isDirectoryEmpty(targetDir));
        Assert.assertTrue(isDirectoryEmpty(outsideDir));
    }

    @Test
    public void uploadWithAbsoluteNameIsRejected() throws IOException {
        File outsideZip = new File(outsideDir, "absolute.zip");
        byte[] content = zipWithEntry("project/demo.sql", "select 1;");

        try {
            CatalogueUploadHelper.uploadZipToDirectory(targetDir, outsideZip.getAbsolutePath(), content);
            Assert.fail("Absolute names should be rejected");
        } catch (IOException ex) {
            Assert.assertEquals(CatalogueUploadHelper.INVALID_NAME_MESSAGE, ex.getMessage());
        }

        Assert.assertFalse(outsideZip.exists());
        Assert.assertTrue(isDirectoryEmpty(targetDir));
        Assert.assertTrue(isDirectoryEmpty(outsideDir));
    }

    @Test
    public void uploadWithWindowsDrivePrefixNameIsRejected() throws IOException {
        byte[] content = zipWithEntry("project/demo.sql", "select 1;");

        try {
            CatalogueUploadHelper.uploadZipToDirectory(targetDir, "C:\\fakepath\\drive.zip", content);
            Assert.fail("Windows drive prefix names should be rejected");
        } catch (IOException ex) {
            Assert.assertEquals(CatalogueUploadHelper.INVALID_NAME_MESSAGE, ex.getMessage());
        }

        Assert.assertTrue(isDirectoryEmpty(targetDir));
        Assert.assertTrue(isDirectoryEmpty(outsideDir));
    }

    @Test
    public void uploadInsideTargetDirectoryIsAllowed() throws IOException {
        byte[] content = zipWithEntry("project/demo.sql", "select 1;");

        ResolvedUploadPaths paths = CatalogueUploadHelper.uploadZipToDirectory(targetDir, "project.zip", content);

        File extracted = new File(paths.getUnzipDir(), "demo.sql");
        Assert.assertTrue(paths.getZipFile().isFile());
        Assert.assertTrue(
                paths.getZipFile().getCanonicalPath().startsWith(targetDir.getCanonicalPath() + File.separator));
        Assert.assertTrue(extracted.isFile());
        Assert.assertEquals("select 1;", FileUtil.readUtf8String(extracted));
        Assert.assertTrue(isDirectoryEmpty(outsideDir));
    }

    @Test
    public void unzipWithParentDirectoryEntryIsRejected() throws IOException {
        File zipFile = new File(targetDir, "project.zip");
        FileUtil.writeBytes(zipWithEntry("../outside/escaped.sql", "should-not-be-written"), zipFile);
        File escaped = new File(outsideDir, "escaped.sql");

        try {
            CatalogueUploadHelper.unzipInsideDirectory(zipFile, targetDir);
            Assert.fail("Entry names that leave the target directory should be rejected");
        } catch (IOException ex) {
            Assert.assertEquals(CatalogueUploadHelper.INVALID_NAME_MESSAGE, ex.getMessage());
        }

        Assert.assertFalse(escaped.exists());
        Assert.assertFalse(new File(tempDir.toFile(), "outside/escaped.sql").exists());
        Assert.assertTrue(isDirectoryEmpty(outsideDir));
    }

    @Test
    public void unzipWithAbsoluteEntryIsRejected() throws IOException {
        File zipFile = new File(targetDir, "project.zip");
        File escaped = new File(outsideDir, "absolute.sql");
        FileUtil.writeBytes(zipWithEntry(escaped.getAbsolutePath(), "should-not-be-written"), zipFile);

        try {
            CatalogueUploadHelper.unzipInsideDirectory(zipFile, targetDir);
            Assert.fail("Absolute entry names should be rejected");
        } catch (IOException ex) {
            Assert.assertEquals(CatalogueUploadHelper.INVALID_NAME_MESSAGE, ex.getMessage());
        }

        Assert.assertFalse(escaped.exists());
        Assert.assertTrue(isDirectoryEmpty(outsideDir));
    }

    private static byte[] zipWithEntry(String entryName, String content) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(buffer);
        try {
            zip.putNextEntry(new ZipEntry(entryName));
            zip.write(content.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        } finally {
            zip.close();
        }
        return buffer.toByteArray();
    }

    private static boolean isDirectoryEmpty(File directory) {
        File[] files = directory.listFiles();
        return files != null && files.length == 0;
    }
}
