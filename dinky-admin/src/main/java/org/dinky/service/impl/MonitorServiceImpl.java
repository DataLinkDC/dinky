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
import org.dinky.data.vo.CascaderVO;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
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
        Timestamp startTS = Timestamp.fromLocalDateTime(DateUtil.toLocalDateTime(startTime));
        Timestamp endTS = Timestamp.fromLocalDateTime(DateUtil.toLocalDateTime(endTime));

        if (endTime.compareTo(startTime) < 1) {
            throw new DinkyException("The end date must be greater than the start date!");
        }

        Function<PredicateBuilder, List<Predicate>> filter = p -> {
            int heartTime = p.indexOf("heart_time");
            Predicate greaterOrEqual = p.greaterOrEqual(heartTime, startTS);
            Predicate lessOrEqual = p.lessOrEqual(heartTime, endTS);
            Predicate local = p.in(
                    p.indexOf("model"),
                    models.stream().map(BinaryString::fromString).collect(Collectors.toList()));
            return CollUtil.newArrayList(PredicateBuilder.and(local, greaterOrEqual, lessOrEqual));
        };
        List<MetricsVO> metricsVOList =
                PaimonUtil.batchReadTable(PaimonTableConstant.DINKY_METRICS, MetricsVO.class, filter);
        return metricsVOList.stream()
                // todo 这里再次过滤，是因为paimon查询的bug问题导致
                .filter(x -> x.getHeartTime().isAfter(startTS.toLocalDateTime()))
                .filter(x -> x.getHeartTime().isBefore(endTS.toLocalDateTime()))
                .peek(vo -> vo.setContent(JsonUtils.parseObject(vo.getContent().toString())))
                .collect(Collectors.toList());
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

    /**
     * @param startTime
     * @param endTime
     * @param flinkMetricsIdList
     * @return
     */
    @Override
    public Map<Integer, List<Dict>> getFlinkDataByDashboard(Long startTime, Long endTime, String flinkMetricsIdList) {
        Map<Integer, String> cacheMap = new HashMap<>();
        List<Metrics> metrics = listByIds(Arrays.asList(flinkMetricsIdList.split(",")));
        metrics.forEach(x -> {
            String jid = cacheMap.computeIfAbsent(x.getTaskId(), k -> SpringUtil.getBean(JobInstanceService.class)
                    .getJobInstanceByTaskId(k)
                    .getJid());
            x.setJobId(jid);
        });

        List<String> flinkJobIdList =
                metrics.stream().map(Metrics::getJobId).distinct().collect(Collectors.toList());

        Map<String, Map<String, List<Tuple>>> map = metrics.stream()
                .collect(Collectors.groupingBy(
                        Metrics::getJobId,
                        Collectors.toMap(
                                Metrics::getVertices,
                                x -> Collections.singletonList(new Tuple(x.getMetrics(), x.getId())),
                                CollUtil::unionAll)));

        Map<Integer, List<Dict>> resultData = new HashMap<>();
        List<MetricsVO> data = getData(
                DateUtil.date(startTime),
                DateUtil.date(Opt.ofNullable(endTime).orElse(DateUtil.date().getTime())),
                flinkJobIdList);
        data.forEach(x -> {
            Map<String, List<Tuple>> tupleMap = map.get(x.getModel());
            tupleMap.keySet().forEach(y -> {
                JsonNode jsonObject = ((ObjectNode) x.getContent()).get(y);
                List<Tuple> tupleList = tupleMap.get(y);
                for (Tuple tuple : tupleList) {
                    String metricsName = tuple.get(0);
                    String d = jsonObject.get(metricsName).asText();
                    Dict dict = Dict.create().set("time", x.getHeartTime()).set("value", d);
                    Integer id = tuple.get(1);
                    resultData.computeIfAbsent(id, k -> new ArrayList<>()).add(dict);
                }
            });
        });
        return resultData;
    }

    /**
     * Get the metrics layout by cascader.
     *
     * @return the list of cascader vo
     */
    @Override
    public List<CascaderVO> getMetricsLayoutByCascader() {
        return getMetricsLayout().stream()
                .map(x -> {
                    CascaderVO cascaderVO = new CascaderVO();
                    cascaderVO.setLabel(x.getLayoutName());
                    cascaderVO.setValue(x.getLayoutName());
                    cascaderVO.setChildren(new ArrayList<>());

                    List<List<Metrics>> vertices = CollUtil.groupByField(x.getMetrics(), "vertices");
                    vertices.forEach(y -> {
                        CascaderVO cascader1 = new CascaderVO();
                        cascader1.setLabel(y.get(0).getVertices());
                        cascader1.setValue(y.get(0).getVertices());
                        cascader1.setChildren(new ArrayList<>());

                        cascaderVO.getChildren().add(cascader1);
                        y.forEach(z -> {
                            CascaderVO cascader2 = new CascaderVO();
                            cascader2.setLabel(z.getMetrics());
                            cascader2.setValue(z.getId().toString());
                            cascader1.getChildren().add(cascader2);
                        });
                    });
                    return cascaderVO;
                })
                .collect(Collectors.toList());
    }
}
