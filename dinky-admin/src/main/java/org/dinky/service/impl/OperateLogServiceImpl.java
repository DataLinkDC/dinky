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

import org.dinky.data.model.OperateLog;
import org.dinky.data.result.ProTableResult;
import org.dinky.mapper.OperateLogMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.OperateLogService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class OperateLogServiceImpl extends SuperServiceImpl<OperateLogMapper, OperateLog> implements OperateLogService {
    /** @param operateLog */
    @Override
    public void saveLog(OperateLog operateLog) {
        getBaseMapper().insert(operateLog);
    }

    @Override
    public ProTableResult<OperateLog> operateRecord(JsonNode para, Integer userId) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("userId", userId);
        return this.selectForProTable(para, paraMap);
    }
}
