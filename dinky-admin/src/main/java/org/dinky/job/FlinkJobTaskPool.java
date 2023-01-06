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

package com.dlink.job;

import com.dlink.model.JobInfoDetail;
import com.dlink.pool.AbstractPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FlinkJobTaskPool
 *
 * @author wenmo
 * @since 2022/5/28 16:39
 */
public class FlinkJobTaskPool extends AbstractPool<JobInfoDetail> {

    private static volatile Map<String, JobInfoDetail> flinkJobTaskEntityMap = new ConcurrentHashMap<>();

    private static FlinkJobTaskPool instance = new FlinkJobTaskPool();

    public static FlinkJobTaskPool getInstance() {
        return instance;
    }

    @Override
    public Map<String, JobInfoDetail> getMap() {
        return flinkJobTaskEntityMap;
    }

    @Override
    public void refresh(JobInfoDetail entity) {
        entity.refresh();
    }
}
