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

package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.JobHistory;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;

/**
 * JobHistoryMapper
 *
 * @author wenmo
 * @since 2022/3/2 19:50
 **/
@Mapper
public interface JobHistoryMapper extends SuperMapper<JobHistory> {

    @InterceptorIgnore(tenantLine = "true")
    JobHistory getByIdWithoutTenant(Integer id);
}
