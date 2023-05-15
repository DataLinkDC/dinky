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

import org.dinky.process.model.ProcessEntity;

import java.util.List;

/** ProcessService */
public interface ProcessService {

    /**
     * List all process
     *
     * @param active true: list active process, false: list inactive process {@link Boolean}
     * @return {@link List}<{@link ProcessEntity}>
     */
    List<ProcessEntity> listAllProcess(boolean active);

    /**
     * get log by user id
     *
     * @param userId user id {@link Integer}
     * @return {@link String}
     */
    String getConsoleByUserId(Integer userId);

    /**
     * clear log by user id
     *
     * @param userId user id {@link Integer}
     */
    void clearConsoleByUserId(Integer userId);
}
