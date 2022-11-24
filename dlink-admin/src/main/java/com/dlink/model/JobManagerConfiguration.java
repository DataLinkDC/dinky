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

package com.dlink.model;

import java.util.Map;

import lombok.Data;

/**
 * @program: dlink
 * @description: JobManager 配置信息
 * @author: zhumingye
 * @create: 2022-06-26 10:53
 */

@Data
public class JobManagerConfiguration {

    private Map<String, String> metrics;

    private Map<String, String> jobManagerConfig;

    private String jobManagerLog;

    private String jobManagerStdout;

}
