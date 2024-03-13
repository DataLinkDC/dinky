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

package org.dinky.service;

import org.dinky.scheduler.model.DinkyTaskRequest;
import org.dinky.scheduler.model.TaskDefinition;
import org.dinky.scheduler.model.TaskMainInfo;

import java.util.List;

public interface SchedulerService {

    /**
     * Pushes the specified DinkyTaskRequest to the task queue.
     *
     * @param  dinkyTaskRequest  the DinkyTaskRequest to be added to the task queue
     * @return                  true if the task was successfully added, false otherwise
     */
    boolean pushAddTask(DinkyTaskRequest dinkyTaskRequest);

    /**
     * A description of the entire Java function.
     *
     * @param  projectCode   description of parameter
     * @param  processCode   description of parameter
     * @param  taskCode      description of parameter
     * @param  dinkyTaskRequest  description of parameter
     * @return         	description of return value
     */
    boolean pushUpdateTask(long projectCode, long processCode, long taskCode, DinkyTaskRequest dinkyTaskRequest);

    /**
     * Retrieves a list of TaskMainInfo objects based on the provided dinkyTaskId.
     *
     * @param  dinkyTaskId  the ID of the task
     * @return              a list of TaskMainInfo objects
     */
    List<TaskMainInfo> getTaskMainInfos(long dinkyTaskId);

    /**
     * Retrieves the task definition information for a given dinky task ID.
     *
     * @param  dinkyTaskId  the ID of the dinky task
     * @return              the task definition information
     */
    TaskDefinition getTaskDefinitionInfo(long dinkyTaskId);
}
