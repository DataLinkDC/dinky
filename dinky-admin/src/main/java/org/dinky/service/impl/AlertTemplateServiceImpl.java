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

import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.alert.AlertRule;
import org.dinky.data.model.alert.AlertTemplate;
import org.dinky.mapper.AlertTemplateMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.AlertRuleService;
import org.dinky.service.AlertTemplateService;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertTemplateServiceImpl extends SuperServiceImpl<AlertTemplateMapper, AlertTemplate>
        implements AlertTemplateService {

    private final AlertRuleService alertRuleService;

    /**
     * 根据实体(ID)删除
     *
     * @param entity 实体
     * @since 3.4.4
     */
    @Override
    public boolean removeAlertTemplateById(Integer entity) {
        if (hasRelationShip(entity)) {
            throw new BusException(Status.ALERT_TEMPLATE_EXIST_RELATIONSHIP);
        }
        return super.removeById(entity);
    }

    /**
     * check alert template has relationship
     *
     * @param id {@link Integer} alert template id
     * @return {@link Boolean} true: has relationship, false: no relationship
     */
    @Override
    public boolean hasRelationShip(Integer id) {
        return !alertRuleService
                .list(new LambdaQueryWrapper<AlertRule>().eq(AlertRule::getTemplateId, id))
                .isEmpty();
    }
}
