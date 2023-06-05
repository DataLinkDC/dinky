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

import org.dinky.data.model.History;
import org.dinky.data.result.ResultPool;
import org.dinky.mapper.HistoryMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.HistoryService;

import org.springframework.stereotype.Service;

/**
 * HistoryServiceImpl
 *
 * @since 2021/6/26 23:08
 */
@Service
public class HistoryServiceImpl extends SuperServiceImpl<HistoryMapper, History>
        implements HistoryService {

    @Override
    public boolean removeHistoryById(Integer id) {
        History history = getById(id);
        if (history != null) {
            ResultPool.remove(history.getJobId());
        }
        return removeById(id);
    }
}
