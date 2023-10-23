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

import org.dinky.data.dto.TaskDTO;
import org.dinky.data.model.Savepoints;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

/**
 * Savepoints
 *
 * @since 2021/11/21
 */
public interface SavepointsService extends ISuperService<Savepoints> {

    /**
     * Get a list of savepoints for a specified task ID.
     *
     * @param taskId The ID of the task to get the savepoints for.
     * @return A list of {@link Savepoints} objects representing the savepoints for the specified task ID.
     */
    List<Savepoints> listSavepointsByTaskId(Integer taskId);

    /**
     * Get the latest savepoint for a specified task ID.
     *
     * @param taskId The ID of the task to get the latest savepoint for.
     * @return A {@link Savepoints} object representing the latest savepoint for the specified task ID.
     */
    Savepoints getLatestSavepointByTaskId(Integer taskId);

    /**
     * Get the earliest savepoint for a specified task ID.
     *
     * @param taskId The ID of the task to get the earliest savepoint for.
     * @return A {@link Savepoints} object representing the earliest savepoint for the specified task ID.
     */
    Savepoints getEarliestSavepointByTaskId(Integer taskId);

    /**
     * Get a savepoint for a specified task using the specified strategy.
     *
     * @param task A {@link TaskDTO} object representing the task to get the savepoint for.
     * @return A {@link Savepoints} object representing the savepoint for the specified task.
     */
    Savepoints getSavePointWithStrategy(TaskDTO task);
}
