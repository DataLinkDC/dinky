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

package org.dinky.url;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Profile;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;

@Profile("!test")
public class RsURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private final List<String> notContains = Arrays.asList("jar", "file", "http", "https");

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("rs".equals(protocol)) {
            return new RsURLStreamHandler();
        }
        for (String tempProtocol : notContains) {
            if (tempProtocol.equals(StrUtil.sub(protocol, 0, tempProtocol.length()))) {
                return null;
            }
        }

        try {
            Class.forName("org.apache.hadoop.fs.FsUrlStreamHandlerFactory");
        } catch (Exception e) {
            return null;
        }
        return Singleton.get(FsUrlStreamHandlerFactory.class).createURLStreamHandler(protocol);
    }
}
