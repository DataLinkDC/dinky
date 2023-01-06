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

import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.AlertGroupMapper;
import com.dlink.model.AlertGroup;
import com.dlink.model.AlertInstance;
import com.dlink.service.AlertGroupService;
import com.dlink.service.AlertInstanceService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * AlertGroupServiceImpl
 *
 * @author wenmo
 * @since 2022/2/24 20:01
 **/
@Service
public class AlertGroupServiceImpl extends SuperServiceImpl<AlertGroupMapper, AlertGroup> implements AlertGroupService {

    @Autowired
    private AlertInstanceService alertInstanceService;

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
            if (Asserts.isNullString(alertInstanceId) || alertInstanceId.equals("0")) {
                continue;
            }
            alertInstanceList.add(alertInstanceService.getById(Integer.valueOf(alertInstanceId)));
        }
        alertGroup.setInstances(alertInstanceList);
        return alertGroup;
    }
}
