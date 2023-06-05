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

import org.dinky.data.model.Savepoints;
import org.dinky.mapper.SavepointsMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.SavepointsService;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * SavepointsServiceImpl
 *
 * @since 2021/11/21
 */
@Service
public class SavepointsServiceImpl extends SuperServiceImpl<SavepointsMapper, Savepoints>
        implements SavepointsService {

    @Override
    public List<Savepoints> listSavepointsByTaskId(Integer taskId) {
        return list(new QueryWrapper<Savepoints>().eq("task_id", taskId));
    }

    @Override
    public Savepoints getLatestSavepointByTaskId(Integer taskId) {
        return baseMapper.getLatestSavepointByTaskId(taskId);
    }

    @Override
    public Savepoints getEarliestSavepointByTaskId(Integer taskId) {
        return baseMapper.getEarliestSavepointByTaskId(taskId);
    }
}
