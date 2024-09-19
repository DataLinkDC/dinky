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

import static org.dinky.data.constant.MonitorTableConstant.HEART_TIME;
import static org.dinky.data.constant.MonitorTableConstant.JOB_ID;

import org.dinky.data.MetricsLayoutVo;
import org.dinky.data.constant.MonitorTableConstant;
import org.dinky.data.dto.MetricsLayoutDTO;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.model.Metrics;
import org.dinky.data.model.job.JobInstance;
import org.dinky.data.vo.CascaderVO;
import org.dinky.data.vo.MetricsVO;
import org.dinky.mapper.MetricsMapper;
import org.dinky.service.JobInstanceService;
import org.dinky.service.MonitorService;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.SqliteUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorServiceImpl extends ServiceImpl<MetricsMapper, Metrics> implements MonitorService {

    @PostConstruct
    private void init() {
        String sql = String.format(
                "%s BIGINT, %s TEXT, %s TEXT, %s INTEGER",
                JOB_ID, MonitorTableConstant.VALUE, MonitorTableConstant.HEART_TIME, MonitorTableConstant.DATE);
        SqliteUtil.INSTANCE.createTable(MonitorTableConstant.DINKY_METRICS, sql);
    }

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
        try (SqliteUtil.PreparedResultSet ps =
                SqliteUtil.INSTANCE.read(MonitorTableConstant.DINKY_METRICS, condition)) {
            ResultSet read = ps.getRs();
            while (read.next()) {
                MetricsVO metricsVO = new MetricsVO();
                metricsVO.setModel(read.getString(MonitorTableConstant.JOB_ID));
                metricsVO.setContent(read.getString(MonitorTableConstant.VALUE));
                metricsVO.setHeartTime(
                        DateUtil.parse(read.getString(HEART_TIME)).toLocalDateTime());
                metricsVO.setDate(read.getString(MonitorTableConstant.DATE));
                metricsVOList.add(metricsVO);
            }

        } catch (SQLException e) {
            throw new DinkyException("Failed to get data from the database", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return metricsVOList;
    }

    public static String getUtcCondition(Date startTime, Date endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime startLdt = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime endLdt = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());
        return MessageFormat.format(
                "''{0}'' <= {2} AND {2} <= ''{1}''", startLdt.format(formatter), endLdt.format(formatter), HEART_TIME);
    }

    @Override
    public SseEmitter sendJvmInfo() {
        //        while (true) {
        //            sendTopic(sessionKey, Jvm.of());
        //            ThreadUtil.sleep(10000);
        //        }
        //        return sseEmitter;
        // todo ws修改
        return null;
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
            if (tupleMap == null) {
                return;
            }
            tupleMap.keySet().forEach(y -> {
                Map<String, String> jsonObject = JsonUtils.toMap(x.getContent().toString(), String.class, Map.class)
                        .get(y);
                List<Tuple> tupleList = tupleMap.get(y);
                for (Tuple tuple : tupleList) {
                    String metricsName = tuple.get(0);
                    Integer d = MapUtil.getInt(jsonObject, metricsName);
                    Dict dict = Dict.create().set("time", x.getHeartTime()).set(MonitorTableConstant.VALUE, d);
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
