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

import org.dinky.data.model.Dashboard;
import org.dinky.mapper.DashboardMapper;
import org.dinky.service.DashboardService;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class DashboardServiceImpl extends ServiceImpl<DashboardMapper, Dashboard> implements DashboardService {
    @Override
    public boolean saveOrUpdate(Dashboard entity) {
        // 更新和保存，保证name不重复
        if (entity.getId() == null) {
            Dashboard dashboard = getOne(Wrappers.<Dashboard>lambdaQuery().eq(Dashboard::getName, entity.getName()));
            if (dashboard != null) {
                throw new RuntimeException("name already exists");
            }
        } else {
            Dashboard dashboard = getOne(Wrappers.<Dashboard>lambdaQuery().eq(Dashboard::getName, entity.getName()));
            if (dashboard != null && !dashboard.getId().equals(entity.getId())) {
                throw new RuntimeException("name already exists");
            }
        }
        return super.saveOrUpdate(entity);
    }
}
