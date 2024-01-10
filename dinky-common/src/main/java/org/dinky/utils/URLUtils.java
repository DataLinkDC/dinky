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

import org.dinky.assertion.Asserts;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;

/**
 * @since 0.7.0
 */
public class URLUtils {
    private static final String TMP_PATH = StrUtil.join(File.separator, System.getProperty("user.dir"), "tmp");

    /**
     * url download file to local
     *
     * @param urlPath urlPath
     * @return file
     */
    public static File toFile(String urlPath) {
        try {
            URL url = new URL(urlPath);
            URLConnection urlConnection = url.openConnection();
            if ("rs".equals(url.getProtocol())) {
                String path = StrUtil.join(File.separator, TMP_PATH, "rs", url.getPath());
                return FileUtil.writeFromStream(urlConnection.getInputStream(), path);
            } else if ("file".equals(url.getProtocol())) {
                return new File(url.getPath());
            }
            throw new RuntimeException(StrFormatter.format(
                    "The path {} unsupported protocol: {},please use rs:// or file://", urlPath, url.getProtocol()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param files URL对应的文件对象
     * @return URL
     */
    public static URL[] getURLs(File... files) {
        final URL[] urls = new URL[files.length];
        try {
            for (int i = 0; i < files.length; i++) {
                urls[i] = files[i].toURI().toURL();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return urls;
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param files URL对应的文件对象
     * @return URL
     */
    public static URL[] getURLs(List<File> files) {
        return getURLs(files.stream().filter(File::exists).toArray(File[]::new));
    }

    public static URL[] getURLs(Set<File> files) {
        return getURLs(files.stream().filter(File::exists).toArray(File[]::new));
    }

    public static String toString(URL[] urls) {
        return Arrays.stream(urls).map(URL::toString).collect(Collectors.joining(","));
    }

    public static String formatAddress(String webURL) {
        if (Asserts.isNotNullString(webURL)) {
            return webURL.replaceAll("http://", "");
        } else {
            return "";
        }
    }

    public static int getRandomPort() {
        Random random = new Random();
        return 30000 + random.nextInt(35536);
    }
}
