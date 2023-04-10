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
import org.dinky.db.service.impl.SuperServiceImpl;
import org.dinky.mapper.AlertGroupMapper;
import org.dinky.model.AlertGroup;
import org.dinky.model.AlertInstance;
import org.dinky.service.AlertGroupService;
import org.dinky.service.AlertInstanceService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * AlertGroupServiceImpl
 *
 * @since 2022/2/24 20:01
 */
@Service
public class AlertGroupServiceImpl extends SuperServiceImpl<AlertGroupMapper, AlertGroup>
        implements AlertGroupService {

    @Lazy @Resource private AlertInstanceService alertInstanceService;

    @Override
    public List<AlertGroup> listEnabledAll() {
        return list(new QueryWrapper<AlertGroup>().eq("enabled", 1));
    }

    @Override
    public AlertGroup getAlertGroupInfo(Integer id) {
        AlertGroup alertGroup = getById(id);
        if (Asserts.isNull(alertGroup) || Asserts.isNullString(alertGroup.getAlertInstanceIds())) {
            return alertGroup;
        }
        String[] alertInstanceIds = alertGroup.getAlertInstanceIds().split(",");
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
}
