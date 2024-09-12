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

package org.dinky.job.handler;

import org.dinky.context.SpringContextUtils;
import org.dinky.data.model.job.History;
import org.dinky.data.result.SelectResult;
import org.dinky.job.JobReadHandler;
import org.dinky.service.HistoryService;
import org.dinky.utils.JsonUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import org.springframework.context.annotation.DependsOn;

import lombok.extern.slf4j.Slf4j;

/**
 * JobReadMysqlHandler.
 *
 * @since 2024/5/31 11:41
 */
@Slf4j
@DependsOn("springContextUtils")
public class JobReadMysqlHandler implements JobReadHandler {

    private static final HistoryService historyService;

    static {
        historyService = SpringContextUtils.getBean("historyServiceImpl", HistoryService.class);
    }

    /**
     * Read result data from mysql.
     *
     * @param jobId job id
     * @return result data
     */
    @Override
    public SelectResult readResultDataFromStorage(Integer jobId) {
        History history = historyService.getById(jobId);
        if (Objects.isNull(history)) {
            return SelectResult.buildFailed();
        }
        String result = history.getResult();
        if (StringUtils.isBlank(result)) {
            return SelectResult.buildFailed();
        }
        return JsonUtils.toBean(result, SelectResult.class);
    }
}
