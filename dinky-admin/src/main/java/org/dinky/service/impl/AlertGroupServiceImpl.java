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

import org.dinky.assertion.Asserts;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.Task;
import org.dinky.data.model.alert.AlertGroup;
import org.dinky.data.model.alert.AlertHistory;
import org.dinky.data.model.alert.AlertInstance;
import org.dinky.mapper.AlertGroupMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.AlertGroupService;
import org.dinky.service.AlertHistoryService;
import org.dinky.service.AlertInstanceService;
import org.dinky.service.TaskService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * AlertGroupServiceImpl
 */
@Service
@RequiredArgsConstructor
public class AlertGroupServiceImpl extends SuperServiceImpl<AlertGroupMapper, AlertGroup> implements AlertGroupService {

    @Lazy
    @Resource
    private AlertInstanceService alertInstanceService;

    @Lazy
    @Resource
    private AlertHistoryService alertHistoryService;

    @Lazy
    @Resource
    private TaskService taskService;

    @Override
    public List<AlertGroup> listEnabledAllAlertGroups() {
        return list(new LambdaQueryWrapper<AlertGroup>().eq(AlertGroup::getEnabled, 1));
    }

    @Override
    public AlertGroup getAlertGroupInfo(Integer id) {
        AlertGroup alertGroup = getById(id);
        if (Asserts.isNull(alertGroup) || Asserts.isNullString(alertGroup.getAlertInstanceIds())) {
            return alertGroup;
        }
        String[] alertInstanceIds = alertGroup.getAlertInstanceIds().split(StrUtil.COMMA);
        List<AlertInstance> alertInstanceList = new ArrayList<>();
        for (String alertInstanceId : alertInstanceIds) {
            if (Asserts.isNullString(alertInstanceId) || "0".equals(alertInstanceId)) {
                continue;
            }
            alertInstanceList.add(alertInstanceService.getById(Integer.valueOf(alertInstanceId)));
        }
        alertGroup.setInstances(alertInstanceList);
        return alertGroup;
    }

    @Override
    public Boolean modifyAlertGroupStatus(Integer id) {
        AlertGroup alertGroup = getById(id);
        alertGroup.setEnabled(!alertGroup.getEnabled());
        return updateById(alertGroup);
    }

    /**
     * delete alert group by id and cascade delete alert history
     *
     * @param id {@link Integer}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteGroupById(Integer id) {
        if (hasRelationShip(id)) {
            throw new BusException(Status.ALERT_GROUP_EXIST_RELATIONSHIP);
        }
        alertHistoryService
                .list(new LambdaQueryWrapper<AlertHistory>().eq(AlertHistory::getAlertGroupId, id))
                .forEach(alertHistory -> alertHistoryService.removeById(alertHistory.getId()));
        return removeById(id);
    }

    /**
     * @param keyword
     * @return
     */
    @Override
    public List<AlertGroup> selectListByKeyWord(String keyword) {
        return getBaseMapper()
                .selectList(new LambdaQueryWrapper<>(AlertGroup.class)
                        .like(AlertGroup::getName, keyword)
                        .or()
                        .like(AlertGroup::getNote, keyword));
    }

    /**
     * check alert group has relationship with other table
     *
     * @param id {@link Integer} alert group id
     * @return {@link Boolean} true: has relationship, false: no relationship
     */
    @Override
    public boolean hasRelationShip(Integer id) {
        return !taskService
                .list(new LambdaQueryWrapper<Task>().eq(Task::getAlertGroupId, id))
                .isEmpty();
    }
}
