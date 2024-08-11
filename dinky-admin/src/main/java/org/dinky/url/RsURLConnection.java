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

import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cn.hutool.core.io.IoUtil;

public class RsURLConnection extends URLConnection {
    private byte[] context;

    @Override
    public void connect() {
        context = JobManager.build(new JobConfig()).readFIle(getURL().getPath());
    }

    @Override
    public InputStream getInputStream() {
        connect();
        return IoUtil.toStream(context);
    }

    public RsURLConnection(URL url) {
        super(url);
    }
}
