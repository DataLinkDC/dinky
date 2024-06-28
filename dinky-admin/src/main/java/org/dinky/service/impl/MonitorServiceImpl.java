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

package org.dinky.service.impl;

import org.dinky.context.SseSessionContextHolder;
import org.dinky.data.MetricsLayoutVo;
import org.dinky.data.constant.PaimonTableConstant;
import org.dinky.data.dto.MetricsLayoutDTO;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.metrics.Jvm;
import org.dinky.data.model.Metrics;
import org.dinky.data.model.job.JobInstance;
import org.dinky.data.vo.MetricsVO;
import org.dinky.mapper.MetricsMapper;
import org.dinky.service.JobInstanceService;
import org.dinky.service.MonitorService;
import org.dinky.shaded.paimon.data.BinaryString;
import org.dinky.shaded.paimon.data.Timestamp;
import org.dinky.shaded.paimon.predicate.Predicate;
import org.dinky.shaded.paimon.predicate.PredicateBuilder;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.PaimonUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dinky.utils.SqliteUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.thread.ThreadUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorServiceImpl extends ServiceImpl<MetricsMapper, Metrics> implements MonitorService {

    private final Executor scheduleRefreshMonitorDataExecutor;
    private final JobInstanceService jobInstanceService;

    @Override
    public List<MetricsVO> getData(Date startTime, Date endTime, List<String> models) {
        if (models.isEmpty()) {
            throw new DinkyException("Please provide at least one monitoring ID");
        }
        endTime = Opt.ofNullable(endTime).orElse(DateUtil.date());
        if (endTime.compareTo(startTime) < 1) {
            throw new DinkyException("The end date must be greater than the start date!");
        }

        String condition = getUtcCondition(startTime, endTime);
        List<MetricsVO> metricsVOList = new ArrayList<>();
        try (ResultSet read = SqliteUtil.INSTANCE.read(PaimonTableConstant.DINKY_METRICS, condition)) {
            while (read.next()) {
                MetricsVO metricsVO = new MetricsVO();
                metricsVO.setModel(read.getString("job_id"));
                metricsVO.setContent(read.getString("value"));
                metricsVO.setHeartTime(convertToLocalDateTime(read.getString("heart_time")));
                metricsVO.setDate(read.getString("date"));
                metricsVOList.add(metricsVO);
            }
        } catch (SQLException e) {
            log.error("Failed to read metrics data from sqlite: " + e.getMessage());
        }
        return metricsVOList;
    }

    public static LocalDateTime convertToLocalDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public static String getUtcCondition(Date startTime, Date endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime startLdt = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime endLdt = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());
        return String.format("'%s' <= heart_time AND heart_time <= '%s'", startLdt.format(formatter), endLdt.format(formatter));
    }

    @Override
    public SseEmitter sendJvmInfo() {
        String sessionKey = UUID.randomUUID().toString();
        SseEmitter sseEmitter = SseSessionContextHolder.connectSession(sessionKey);
        SseSessionContextHolder.subscribeTopic(sessionKey, CollUtil.newArrayList(sessionKey));
        scheduleRefreshMonitorDataExecutor.execute(() -> {
            try {
                while (true) {
                    SseSessionContextHolder.sendTopic(sessionKey, Jvm.of());
                    ThreadUtil.sleep(10000);
                }
            } catch (Exception e) {
                log.error("send jvm info failed, complete sse emiter :" + e.getMessage());
            }
        });
        return sseEmitter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFlinkMetricLayout(String layout, List<MetricsLayoutDTO> metricsList) {
        QueryWrapper<Metrics> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Metrics::getLayoutName, layout);
        remove(wrapper);
        if (CollUtil.isEmpty(metricsList)) {
            return;
        }
        List<MetricsLayoutDTO> list =
                metricsList.stream().peek(m -> m.setLayoutName(layout)).collect(Collectors.toList());
        saveBatch(BeanUtil.copyToList(list, Metrics.class));
    }

    @Override
    public List<MetricsLayoutVo> getMetricsLayout() {
        Map<String, List<Metrics>> collect = list().stream().collect(Collectors.groupingBy(Metrics::getLayoutName));

        List<MetricsLayoutVo> result = new ArrayList<>();
        for (Map.Entry<String, List<Metrics>> entry : collect.entrySet()) {
            // It is derived from a group, so the value must have a value,
            // and a layout name only corresponds to a task ID, so only the first one can be taken
            Integer taskId = entry.getValue().get(0).getTaskId();
            JobInstance jobInstance = jobInstanceService.getJobInstanceByTaskId(taskId);
            MetricsLayoutVo metricsLayoutVo = MetricsLayoutVo.builder()
                    .layoutName(entry.getKey())
                    .metrics(entry.getValue())
                    .flinkJobId(jobInstance == null ? null : jobInstance.getJid())
                    .taskId(taskId)
                    .showInDashboard(true)
                    .build();
            result.add(metricsLayoutVo);
        }
        return result;
    }

    @Override
    public List<Metrics> getMetricsLayoutByName(String layoutName) {
        QueryWrapper<Metrics> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Metrics::getLayoutName, layoutName);
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public List<Metrics> getMetricsLayoutByTaskId(Integer taskId) {
        QueryWrapper<Metrics> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Metrics::getTaskId, taskId);
        return this.baseMapper.selectList(wrapper);
    }

    /**
     * Delete the metrics layout.
     *
     * @param taskId the task id
     * @return if the delete is successful.
     */
    @Override
    public boolean deleteMetricsLayout(Integer taskId) {
        List<Metrics> metricsList = getMetricsLayoutByTaskId(taskId);
        int deleted = this.baseMapper.deleteBatchIds(
                metricsList.stream().map(Metrics::getId).collect(Collectors.toList()));
        return deleted > 0;
    }
}
