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

package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.TaskVersion;

import java.util.List;

/**
 * @author huang
 */
public interface TaskVersionService extends ISuperService<TaskVersion> {


    /**
     * @description 通过作业Id查询版本数据
     * @param taskId
     * @return java.util.List<com.dlink.model.TaskVersion>
     * @author huang
     * @date 2022/6/22 17:17
     */
    List<TaskVersion> getTaskVersionByTaskId(Integer taskId);

}
