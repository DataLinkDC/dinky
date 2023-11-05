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

import org.dinky.data.model.History;
import org.dinky.mybatis.service.ISuperService;

/**
 * HistoryService
 *
 * @since 2021/6/26 23:07
 */
public interface HistoryService extends ISuperService<History> {

    /**
     * Remove the history of a Git project based on its ID.
     *
     * @param id The ID of the Git project to remove the history for.
     * @return A boolean value indicating whether the removal was successful.
     */
    @Deprecated
    boolean removeHistoryById(Integer id);

    /**
     * Get latest history info by task id.
     *
     * @param id The ID of the task.
     * @return History info.
     */
    History getLatestHistoryById(Integer id);
}
