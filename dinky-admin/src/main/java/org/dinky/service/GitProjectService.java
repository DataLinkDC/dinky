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

import org.dinky.db.service.ISuperService;
import org.dinky.dto.GitProjectDTO;
import org.dinky.dto.TreeNodeDTO;
import org.dinky.model.GitProject;

import java.util.List;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
public interface GitProjectService extends ISuperService<GitProject> {
    /**
     * 保存或更新
     *
     * @param gitProjectDTO gitProjectDTO
     */
    void saveOrUpdate(GitProjectDTO gitProjectDTO);

    /**
     * 更新状态
     *
     * @param id id
     */
    void updateState(Integer id);

    /**
     * get project code tree
     *
     * @param id {@link Integer}
     * @return {@link List< TreeNodeDTO >}
     */
    List<TreeNodeDTO> getProjectCode(Integer id);

    /**
     * remove project and code cascade by id
     *
     * @param id {@link Integer}
     * @return {@link Boolean}
     */
    Boolean removeProjectAndCodeCascade(Integer id);

    /**
     * get project build all logs
     *
     * @param id {@link Integer}
     * @return {@link String}
     */
    String getAllBuildLog(Integer id);
}
