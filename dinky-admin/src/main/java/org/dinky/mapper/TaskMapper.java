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
import com.dlink.model.Task;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;

/**
 * 作业 Mapper 接口
 *
 * @author wenmo
 * @since 2021-05-28
 */
@Mapper
public interface TaskMapper extends SuperMapper<Task> {

    Integer queryAllSizeByName(String name);

    List<Task> queryOnLineTaskByDoneStatus(@Param("parentIds") List<Integer> parentIds,
            @Param("stepIds") List<Integer> stepIds,
            @Param("includeNull") boolean includeNull,
            @Param("jobStatuses") List<String> jobStatuses);

    @InterceptorIgnore(tenantLine = "true")
    Task getTaskByNameAndTenantId(@Param("name") String name, @Param("tenantId") Integer tenantId);

    @InterceptorIgnore(tenantLine = "true")
    Integer getTenantByTaskId(@Param("id") Integer id);
}
