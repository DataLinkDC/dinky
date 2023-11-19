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

import org.dinky.data.model.job.History;
import org.dinky.data.model.job.JobInstance;
import org.dinky.service.HistoryService;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.Builder;

@Builder
public class ClearJobHistoryHandler {
    private JobInstanceService jobInstanceService;
    private JobHistoryService jobHistoryService;
    private HistoryService historyService;

    /**
     * Clears job history records based on the specified criteria.
     * @param maxRetainDays The maximum number of days to retain job history.
     * @param maxRetainCount The maximum count to retain job history.
     */
    public void clearJobHistory(Integer maxRetainDays, Integer maxRetainCount) {
        // Query job instance records, grouped by task ID
        List<JobInstance> jobInstanceList = jobInstanceService
                .lambdaQuery()
                .select(JobInstance::getTaskId, JobInstance::getCount)
                .groupBy(JobInstance::getTaskId)
                .list();

        // Iterate over job instance records
        for (JobInstance jobInstance : jobInstanceList) {
            // Check if the count exceeds the maximum retain count
            if (jobInstance.getCount() > maxRetainCount) {
                // Create a query wrapper to delete job instances older than the maximum retain days
                QueryWrapper<JobInstance> deleteWrapper = new QueryWrapper<>();
                deleteWrapper
                        .lambda()
                        .eq(JobInstance::getTaskId, jobInstance.getTaskId())
                        .lt(JobInstance::getCreateTime, LocalDateTime.now().minusDays(maxRetainDays));
                // Retrieve the list of job instances to be deleted
                List<JobInstance> deleteList = jobInstanceService.list(deleteWrapper);
                List<Integer> historyDeleteIds = deleteList.stream()
                        .map(JobInstance::getHistoryId)
                        .collect(Collectors.toList());
                jobHistoryService.removeBatchByIds(historyDeleteIds);
                jobInstanceService.remove(deleteWrapper);
            }
        }
    }

    /**
     * Clears dinky history records based on the specified criteria.
     * @param maxRetainDays The maximum number of days to retain dinky history.
     * @param maxRetainCount The maximum count to retain dinky history.
     */
    public void clearDinkyHistory(Integer maxRetainDays, Integer maxRetainCount) {
        // Query history records, grouped by task ID
        List<History> historyList = historyService
                .lambdaQuery()
                .select(History::getTaskId, History::getCount)
                .groupBy(History::getTaskId)
                .list();

        // Iterate over history records
        for (History history : historyList) {
            // Check if the count exceeds the maximum retain count
            if (history.getCount() > maxRetainCount) {
                // Create a query wrapper to delete history records older than the maximum retain days
                QueryWrapper<History> deleteWrapper = new QueryWrapper<>();
                deleteWrapper
                        .lambda()
                        .eq(History::getTaskId, history.getTaskId())
                        .lt(History::getStartTime, LocalDateTime.now().minusDays(maxRetainDays));
                historyService.remove(deleteWrapper);
            }
        }
    }

}
