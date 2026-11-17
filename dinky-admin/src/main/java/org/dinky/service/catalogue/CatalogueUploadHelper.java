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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;

/**
 * Keeps files written during catalogue upload inside the intended target directory.
 */
public final class CatalogueUploadHelper {

    public static final String INVALID_NAME_MESSAGE = "file name is invalid";

    private CatalogueUploadHelper() {}

    /**
     * Resolved zip and extract locations under a target directory.
     */
    public static final class ResolvedUploadPaths {
        private final File targetDir;
        private final File zipFile;
        private final File unzipDir;
        private final String unzipFileName;

        private ResolvedUploadPaths(File targetDir, File zipFile, File unzipDir, String unzipFileName) {
            this.targetDir = targetDir;
            this.zipFile = zipFile;
            this.unzipDir = unzipDir;
            this.unzipFileName = unzipFileName;
        }

        public File getTargetDir() {
            return targetDir;
        }

        public File getZipFile() {
            return zipFile;
        }

        public File getUnzipDir() {
            return unzipDir;
        }

        public String getUnzipFileName() {
            return unzipFileName;
        }
    }

    /**
     * Resolve the zip file and extract directory for an uploaded name, keeping both under the target
     * directory.
     */
    public static ResolvedUploadPaths resolveUploadPaths(File targetDir, String originalFileName) throws IOException {
        File zipFile = resolveInsideDirectory(targetDir, originalFileName);
        String unzipFileName = FileUtil.mainName(originalFileName);
        if (StrUtil.isBlank(unzipFileName)) {
            throw new IOException(INVALID_NAME_MESSAGE);
        }
        File unzipDir = resolveInsideDirectory(targetDir, unzipFileName);
        return new ResolvedUploadPaths(targetDir.getCanonicalFile(), zipFile, unzipDir, unzipFileName);
    }

    /**
     * Write an uploaded zip and extract its contents, keeping every written path under the target
     * directory.
     */
    public static ResolvedUploadPaths uploadZipToDirectory(File targetDir, String originalFileName, byte[] content)
            throws IOException {
        ResolvedUploadPaths paths = resolveUploadPaths(targetDir, originalFileName);
        FileUtil.writeBytes(content, paths.getZipFile());
        unzipInsideDirectory(paths.getZipFile(), paths.getTargetDir());
        return paths;
    }

    /**
     * Resolve a name under the target directory and reject names that would leave it.
     */
    public static File resolveInsideDirectory(File targetDir, String name) throws IOException {
        if (targetDir == null || !isSafeRelativeName(name)) {
            throw new IOException(INVALID_NAME_MESSAGE);
        }
        File canonicalTarget = targetDir.getCanonicalFile();
        File resolved = new File(canonicalTarget, name).getCanonicalFile();
        if (!isInsideDirectory(canonicalTarget, resolved)) {
            throw new IOException(INVALID_NAME_MESSAGE);
        }
        return resolved;
    }

    /**
     * Extract a zip file into the target directory, rejecting entry names that would leave it.
     */
    public static void unzipInsideDirectory(File zipFile, File targetDir) throws IOException {
        if (zipFile == null || targetDir == null) {
            throw new IOException(INVALID_NAME_MESSAGE);
        }
        File canonicalTarget = targetDir.getCanonicalFile();
        if (!canonicalTarget.exists() && !canonicalTarget.mkdirs()) {
            throw new IOException("failed to create target directory");
        }
        ZipFile zip = ZipUtil.toZipFile(zipFile, CharsetUtil.defaultCharset());
        try {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File dest = resolveInsideDirectory(canonicalTarget, entry.getName());
                if (entry.isDirectory()) {
                    if (!dest.exists() && !dest.mkdirs()) {
                        throw new IOException("failed to create directory");
                    }
                    continue;
                }
                File parent = dest.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    throw new IOException("failed to create directory");
                }
                InputStream in = zip.getInputStream(entry);
                try {
                    OutputStream out = Files.newOutputStream(dest.toPath());
                    try {
                        IoUtil.copy(in, out);
                    } finally {
                        out.close();
                    }
                } finally {
                    in.close();
                }
            }
        } finally {
            zip.close();
        }
    }

    /**
     * Delete a file only when it resolves inside the target directory.
     */
    public static void deleteInsideDirectory(File targetDir, File file) throws IOException {
        if (file == null || !file.exists()) {
            return;
        }
        if (targetDir == null) {
            throw new IOException(INVALID_NAME_MESSAGE);
        }
        File canonicalTarget = targetDir.getCanonicalFile();
        File canonicalFile = file.getCanonicalFile();
        if (!isInsideDirectory(canonicalTarget, canonicalFile)) {
            throw new IOException(INVALID_NAME_MESSAGE);
        }
        FileUtil.del(canonicalFile);
    }

    /**
     * Whether {@code file} is the target directory or a file under it.
     */
    public static boolean isInsideDirectory(File root, File file) {
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
        if (StrUtil.isBlank(name)) {
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
}
