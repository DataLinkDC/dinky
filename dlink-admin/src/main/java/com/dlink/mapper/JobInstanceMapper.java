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
import com.dlink.model.JobInstance;
import com.dlink.model.JobInstanceCount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * JobInstanceMapper
 *
 * @author wenmo
 * @since 2022/2/2 13:02
 */
@Mapper
public interface JobInstanceMapper extends SuperMapper<JobInstance> {

    List<JobInstanceCount> countStatus();

    List<JobInstanceCount> countHistoryStatus();

    List<JobInstance> listJobInstanceActive();

    JobInstance getJobInstanceByTaskId(Integer id);

}
