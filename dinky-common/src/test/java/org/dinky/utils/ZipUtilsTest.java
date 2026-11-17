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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests zip extraction path handling.
 */
public class ZipUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    public void unzipInsideTargetDirectoryIsAllowed() throws IOException {
        File targetDir = tempDir.resolve("target").toFile();
        File outsideDir = tempDir.resolve("outside").toFile();
        assertTrue(targetDir.mkdirs());
        assertTrue(outsideDir.mkdirs());

        File zipFile = new File(targetDir, "dep.zip");
        Files.write(zipFile.toPath(), zipWithEntry("jar/dep.jar", "jar-content"));

        ZipUtils.unzip(zipFile.getAbsolutePath(), targetDir.getAbsolutePath());

        File extracted = new File(targetDir, "jar/dep.jar");
        assertTrue(extracted.isFile());
        assertEquals("jar-content", new String(Files.readAllBytes(extracted.toPath()), StandardCharsets.UTF_8));
        assertTrue(extracted.getCanonicalPath().startsWith(targetDir.getCanonicalPath() + File.separator));
        assertTrue(isDirectoryEmpty(outsideDir));
    }

    @Test
    public void unzipWithParentDirectoryEntryIsRejected() throws IOException {
        File targetDir = tempDir.resolve("target").toFile();
        File outsideDir = tempDir.resolve("outside").toFile();
        assertTrue(targetDir.mkdirs());
        assertTrue(outsideDir.mkdirs());

        File zipFile = new File(targetDir, "dep.zip");
        Files.write(zipFile.toPath(), zipWithEntry("../outside/escaped.txt", "should-not-be-written"));
        File escaped = new File(outsideDir, "escaped.txt");

        ZipUtils.unzip(zipFile.getAbsolutePath(), targetDir.getAbsolutePath());

        assertFalse(escaped.exists());
        assertFalse(new File(tempDir.toFile(), "outside/escaped.txt").exists());
        assertTrue(isDirectoryEmpty(outsideDir));
        assertFalse(new File(targetDir, "escaped.txt").exists());
    }

    @Test
    public void unzipWithAbsoluteEntryIsRejected() throws IOException {
        File targetDir = tempDir.resolve("target").toFile();
        File outsideDir = tempDir.resolve("outside").toFile();
        assertTrue(targetDir.mkdirs());
        assertTrue(outsideDir.mkdirs());

        File escaped = new File(outsideDir, "absolute.txt");
        File zipFile = new File(targetDir, "dep.zip");
        Files.write(zipFile.toPath(), zipWithEntry(escaped.getAbsolutePath(), "should-not-be-written"));

        ZipUtils.unzip(zipFile.getAbsolutePath(), targetDir.getAbsolutePath());

        assertFalse(escaped.exists());
        assertTrue(isDirectoryEmpty(outsideDir));
    }

    @Test
    public void unzipWithWindowsDrivePrefixEntryIsRejected() throws IOException {
        File targetDir = tempDir.resolve("target").toFile();
        File outsideDir = tempDir.resolve("outside").toFile();
        assertTrue(targetDir.mkdirs());
        assertTrue(outsideDir.mkdirs());

        File zipFile = new File(targetDir, "dep.zip");
        Files.write(zipFile.toPath(), zipWithEntry("C:\\outside\\drive.txt", "should-not-be-written"));

        ZipUtils.unzip(zipFile.getAbsolutePath(), targetDir.getAbsolutePath());

        assertFalse(new File(outsideDir, "drive.txt").exists());
        assertTrue(isDirectoryEmpty(outsideDir));
        File[] leftover = targetDir.listFiles((dir, name) -> !"dep.zip".equals(name));
        assertTrue(leftover == null || leftover.length == 0);
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
