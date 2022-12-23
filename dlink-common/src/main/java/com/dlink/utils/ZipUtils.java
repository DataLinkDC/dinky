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

package com.dlink.utils;

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

/**
 * @author ZackYoung
 * @since 0.7.0
 */
@Slf4j
public class ZipUtils {

    public static void unzip(String zipFile, String dir) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry zipArchiveEntry = entries.nextElement();
                File file = new File(dir, zipArchiveEntry.getName());
                writeFile(file, zip.getInputStream(zipArchiveEntry));
                log.info("======解压成功=======,file:{}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("压缩包处理异常，异常信息:", e);
        }

    }

    private static void writeFile(File file, InputStream inputStream) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
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
