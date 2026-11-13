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

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;

import lombok.extern.slf4j.Slf4j;

/** @since 0.7.0 */
@Slf4j
public class ZipUtils {

    private static final String INVALID_ENTRY_MESSAGE = "zip entry name would resolve outside the target directory";

    public static void unzip(String zipFile, String dir) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            File targetDir = new File(dir).getCanonicalFile();
            if (!targetDir.exists() && !targetDir.mkdirs()) {
                throw new IOException("failed to create target directory");
            }
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry zipArchiveEntry = entries.nextElement();
                File file = resolveInsideDirectory(targetDir, zipArchiveEntry.getName());
                if (zipArchiveEntry.isDirectory()) {
                    if (!file.exists() && !file.mkdirs()) {
                        throw new IOException("failed to create directory");
                    }
                    continue;
                }
                try (InputStream inputStream = zip.getInputStream(zipArchiveEntry)) {
                    writeFile(file, inputStream);
                }
                log.info("======解压成功=======,file:{}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("压缩包处理异常，异常信息:", e);
        }
    }

    /**
     * Resolve a zip entry name under the target directory and reject names that would leave it.
     */
    static File resolveInsideDirectory(File targetDir, String name) throws IOException {
        if (targetDir == null || !isSafeRelativeName(name)) {
            throw new IOException(INVALID_ENTRY_MESSAGE);
        }
        File canonicalTarget = targetDir.getCanonicalFile();
        File resolved = new File(canonicalTarget, name).getCanonicalFile();
        if (!isInsideDirectory(canonicalTarget, resolved)) {
            throw new IOException(INVALID_ENTRY_MESSAGE);
        }
        return resolved;
    }

    /**
     * Whether {@code file} is the target directory or a file under it.
     */
    static boolean isInsideDirectory(File root, File file) {
        if (root == null || file == null) {
            return false;
        }
        String rootPath = root.getPath();
        String filePath = file.getPath();
        return filePath.equals(rootPath) || filePath.startsWith(rootPath + File.separator);
    }

    /**
     * Reject blank names, parent-directory segments, absolute paths, and Windows drive prefixes.
     */
    static boolean isSafeRelativeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (new File(name).isAbsolute()) {
            return false;
        }
        if (name.startsWith("/") || name.startsWith("\\")) {
            return false;
        }
        if (hasWindowsDrivePrefix(name)) {
            return false;
        }
        String[] segments = name.split("[/\\\\]+");
        if (segments.length == 0) {
            return false;
        }
        boolean hasRealSegment = false;
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            if (segment.isEmpty() || ".".equals(segment)) {
                continue;
            }
            if ("..".equals(segment)) {
                return false;
            }
            hasRealSegment = true;
        }
        return hasRealSegment;
    }

    private static boolean hasWindowsDrivePrefix(String name) {
        if (name.length() < 2) {
            return false;
        }
        return Character.isLetter(name.charAt(0)) && name.charAt(1) == ':';
    }

    private static void writeFile(File file, InputStream inputStream) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (file.isDirectory()) {
            return;
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] bytes = new byte[4096];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        // unzip("/Users/zackyoung/Downloads/85.zip", "/Users/zackyoung/Downloads/85");

        Files.move(Paths.get("/Users/zackyoung/Downloads/85/jar/"), Paths.get("/Users/zackyoung/Downloads/85/123/"));
    }
}
