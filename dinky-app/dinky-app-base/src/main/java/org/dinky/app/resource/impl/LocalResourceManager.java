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

package org.dinky.app.resource.impl;

import org.dinky.app.resource.BaseResourceManager;
import org.dinky.data.model.SystemConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalResourceManager implements BaseResourceManager {
    SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();

    @Override
    public InputStream readFile(String path) {
        try {
            return new URL("http://" + systemConfiguration.getDinkyAddr().getValue() + "/download/downloadFromRs?path="
                            + URLUtil.encode(path))
                    .openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
