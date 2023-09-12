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

import org.dinky.alert.Alert;
import org.dinky.alert.AlertConfig;
import org.dinky.alert.AlertResult;
import org.dinky.data.constant.BaseConstant;
import org.dinky.data.model.AlertGroup;
import org.dinky.data.model.AlertInstance;
import org.dinky.data.result.Result;
import org.dinky.mapper.AlertInstanceMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.AlertGroupService;
import org.dinky.service.AlertInstanceService;
import org.dinky.utils.JSONUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/** AlertInstanceServiceImpl */
@Service
@RequiredArgsConstructor
public class AlertInstanceServiceImpl extends SuperServiceImpl<AlertInstanceMapper, AlertInstance>
        implements AlertInstanceService {

    private final AlertGroupService alertGroupService;

    @Override
    public List<AlertInstance> listEnabledAll() {
        return list(new LambdaQueryWrapper<AlertInstance>().eq(AlertInstance::getEnabled, 1));
    }

    @Override
    public AlertResult testAlert(AlertInstance alertInstance) {
        AlertConfig alertConfig = AlertConfig.build(
                alertInstance.getName(), alertInstance.getType(), JSONUtil.toMap(alertInstance.getParams()));
        Alert alert = Alert.buildTest(alertConfig);
        String currentDateTime =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(BaseConstant.YYYY_MM_DD_HH_MM_SS));

        String msg = "## Checkpoint Failed\n" +
                "If the current task fails to check the checkpoint, check the task configuration and status\n" +
                "> **Task Name :** Test Job\n" +
                " **Alert Time :** 2023-01-01\n" +
                " **Job Id :** jhgf1fuf2f3kfjh43jh\n" +
                " **Job Duration :** 20h\n" +
                " **Dinky Task :**  [Click to vist task page](http://cdh1:8081/#/job/)";

        return alert.send("Fei Shu Alert Test", msg);
    }

    /**
     * delete alert instance
     *
     * @param id {@link Integer}
     * @return {@link Result<Void>}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAlertInstance(Integer id) {
        final Map<Integer, Set<Integer>> alertGroupInformation = getAlertGroupInformation();
        if (!this.removeById(id)) {
            return false;
        }
        alertGroupInformation.remove(id);
        writeBackGroupInformation(alertGroupInformation);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyAlertInstanceStatus(Integer id) {
        AlertInstance alertInstance = getById(id);
        alertInstance.setEnabled(!alertInstance.getEnabled());
        return updateById(alertInstance);
    }

    private void writeBackGroupInformation(Map<Integer, Set<Integer>> alertGroupInformation) {
        if (MapUtils.isEmpty(alertGroupInformation)) {
            return;
        }
        final Map<Integer, String> result = new HashMap<>(8);
        for (Map.Entry<Integer, Set<Integer>> entry : alertGroupInformation.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            final Set<Integer> groupIdSet = entry.getValue();
            for (Integer groupId : groupIdSet) {
                final String instanceIdString = result.get(groupId);
                result.put(
                        groupId,
                        instanceIdString == null ? "" + entry.getKey() : instanceIdString + "," + entry.getKey());
            }
        }
        updateAlertGroupInformation(result, alertGroupInformation.get(null));
    }

    private void updateAlertGroupInformation(Map<Integer, String> result, Set<Integer> groupIdSet) {
        final LocalDateTime now = LocalDateTime.now();
        final List<AlertGroup> list = groupIdSet.stream()
                .filter(Objects::nonNull)
                .map(groupId -> {
                    final AlertGroup alertGroup = new AlertGroup();
                    alertGroup.setId(groupId);
                    final String groupIds = result.get(groupId);
                    alertGroup.setAlertInstanceIds(groupIds == null ? "" : groupIds);
                    return alertGroup;
                })
                .collect(Collectors.toList());
        alertGroupService.updateBatchById(list);
    }

    private Map<Integer, Set<Integer>> getAlertGroupInformation() {
        final LambdaQueryWrapper<AlertGroup> select =
                new LambdaQueryWrapper<AlertGroup>().select(AlertGroup::getId, AlertGroup::getAlertInstanceIds);
        final List<AlertGroup> list = alertGroupService.list(select);
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>(0);
        }
        final Map<Integer, Set<Integer>> map = new HashMap<>(list.size());
        final Set<Integer> groupIdSet = new HashSet<>();
        for (AlertGroup alertGroup : list) {
            buildGroup(map, alertGroup);
            groupIdSet.add(alertGroup.getId());
        }
        map.put(null, groupIdSet);
        return map;
    }

    private void buildGroup(Map<Integer, Set<Integer>> map, AlertGroup alertGroup) {
        if (StringUtils.isBlank(alertGroup.getAlertInstanceIds())) {
            return;
        }

        for (String instanceId : alertGroup.getAlertInstanceIds().split(StrUtil.COMMA)) {
            if (StringUtils.isBlank(instanceId)) {
                continue;
            }
            final Integer instanceIdInt = Integer.valueOf(instanceId);
            Set<Integer> groupIdSet = map.get(instanceIdInt);
            if (CollectionUtils.isEmpty(groupIdSet)) {
                groupIdSet = new HashSet<>();
                map.put(instanceIdInt, groupIdSet);
            }
            groupIdSet.add(alertGroup.getId());
        }
    }
}
