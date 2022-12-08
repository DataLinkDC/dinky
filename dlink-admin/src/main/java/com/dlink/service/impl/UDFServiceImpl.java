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

package com.dlink.service.impl;

import com.dlink.classloader.DinkyClassLoader;
import com.dlink.context.DinkyClassLoaderContextHolder;
import com.dlink.job.JobConfig;
import com.dlink.service.UDFService;
import com.dlink.utils.UDFUtils;

import org.springframework.stereotype.Service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
@Service
public class UDFServiceImpl implements UDFService {

    /**
     * 获取所有udf相关的代码
     *
     * @param statement sql语句
     * @return jobManage
     */
    @Override
    public void init(String statement, JobConfig config) {
        initClassLoader(config);
        config.setUdfList(UDFUtils.getUDF(statement));

    }

    private void initClassLoader(JobConfig config) {
        DinkyClassLoader classLoader = new DinkyClassLoader(null, Thread.currentThread().getContextClassLoader());
        DinkyClassLoaderContextHolder.set(classLoader);
        if (CollUtil.isNotEmpty(config.getConfig())) {
            String pipelineJars = config.getConfig().get("pipeline.jars");
            // add custom jar path
            if (StrUtil.isNotBlank(pipelineJars)) {
                String[] paths = pipelineJars.split(",");
                DinkyClassLoaderContextHolder.get().addURL(paths);
            }
        }
    }

}
