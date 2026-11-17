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

import org.dinky.data.exception.BusException;
import org.dinky.utils.DirUtil;

import java.io.File;
import java.io.IOException;

import cn.hutool.core.util.StrUtil;

/**
 * Keeps system file reads inside the intended directory.
 */
public final class SystemFileReadHelper {

    public static final String INVALID_PATH_MESSAGE = "file path is invalid";

    private SystemFileReadHelper() {}

    /**
     * Read a file after resolving the path inside the intended directory.
     */
    public static String readFile(File rootDir, String path) {
        try {
            File resolved = resolveInsideDirectory(rootDir, path);
            return DirUtil.readFile(resolved.getPath());
        } catch (IOException e) {
            throw new BusException(INVALID_PATH_MESSAGE);
        }
    }

    /**
     * Resolve a path under the intended directory and reject paths that would leave it.
     */
    public static File resolveInsideDirectory(File rootDir, String path) throws IOException {
        if (rootDir == null || !isAcceptablePath(path)) {
            throw new IOException(INVALID_PATH_MESSAGE);
        }
        File canonicalRoot = rootDir.getCanonicalFile();
        File given = new File(path);
        File resolved;
        if (given.isAbsolute()) {
            resolved = given.getCanonicalFile();
        } else {
            resolved = new File(canonicalRoot, path).getCanonicalFile();
        }
        if (!isInsideDirectory(canonicalRoot, resolved)) {
            throw new IOException(INVALID_PATH_MESSAGE);
        }
        return resolved;
    }

    /**
     * Whether {@code file} is the intended directory or a file under it.
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
     * Reject blank names, parent-directory segments, and Windows drive prefixes that are not
     * absolute paths on this system.
     */
    static boolean isAcceptablePath(String path) {
        if (StrUtil.isBlank(path)) {
            return false;
        }
        if (hasWindowsDrivePrefix(path) && !new File(path).isAbsolute()) {
            return false;
        }
        String[] segments = path.split("[/\\\\]+");
        for (int i = 0; i < segments.length; i++) {
            if ("..".equals(segments[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasWindowsDrivePrefix(String path) {
        if (path.length() < 2) {
            return false;
        }
        return Character.isLetter(path.charAt(0)) && path.charAt(1) == ':';
    }
}
