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

package org.dinky.service;

import org.dinky.data.MetricsLayoutVo;
import org.dinky.data.dto.MetricsLayoutDTO;
import org.dinky.data.model.Metrics;
import org.dinky.data.vo.CascaderVO;
import org.dinky.data.vo.MetricsVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.lang.Dict;

public interface MonitorService extends IService<Metrics> {
    /**
     * Get the metrics data for a specified time range and job IDs.
     *
     * @param startTime The start time of the time range.
     * @param endTime The end time of the time range.
     * @param jobIds A list of job IDs to get the metrics data for.
     * @return A list of {@link MetricsVO} objects representing the metrics data for the specified time range and job IDs.
     */
    List<MetricsVO> getData(Date startTime, Date endTime, List<String> jobIds);

    /**
     * Send the JVM information to the specified SSE emitter.
     *
     * @return {@link SseEmitter}
     */
    SseEmitter sendJvmInfo();

    /**
     * Save the Flink metric layout.
     *
     * @param layout The name of the layout to save.
     * @param metricsList A list of {@link MetricsLayoutDTO} objects representing the metrics to save.
     */
    @Transactional(rollbackFor = Exception.class)
    void saveFlinkMetricLayout(String layout, List<MetricsLayoutDTO> metricsList);

    /**
     * Get the metrics layout as a map.
     *
     * @return A map where the keys are layout names and the values are lists of {@link Metrics} objects representing the metrics in each layout.
     */
    List<MetricsLayoutVo> getMetricsLayout();

    /**
     * Get the metrics layout by name.
     *
     * @param layoutName The name of the layout to get.
     * @return A list of {@link Metrics} objects representing the metrics in the specified layout.
     */
    List<Metrics> getMetricsLayoutByName(String layoutName);

    /**
     * Get the job metrics for a specified task ID.
     *
     * @param taskId The ID of the task to get the job metrics for.
     * @return A list of {@link Metrics} objects representing the job metrics for the specified task ID.
     */
    List<Metrics> getMetricsLayoutByTaskId(Integer taskId);

    /**
     * Delete the metrics layout.
     * @param taskId the task id
     * @return if the delete is successful.
     */
    boolean deleteMetricsLayout(Integer taskId);

    /**
     *  Get the metrics dashboard data.
     * @param startTime the start time
     * @param endTime the end time
     * @param flinkMetricsIdList the flink metrics id list
     * @return the map of metrics layout.
     */
    Map<Integer, List<Dict>> getFlinkDataByDashboard(Long startTime, Long endTime, String flinkMetricsIdList);

    /**
     * Get the metrics layout by cascader.
     * @return the list of cascader vo
     */
    List<CascaderVO> getMetricsLayoutByCascader();
}
