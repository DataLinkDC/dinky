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

import org.dinky.data.dto.GitProjectDTO;
import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.model.GitProject;
import org.dinky.data.params.GitProjectSortJarParams;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;
import java.util.Map;

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
     * drag sort project level
     *
     * @param gitProjectDTOList
     */
    void dragendSortProject(Map gitProjectDTOList);

    /**
     * drag sort jar level
     *
     * @param gitProjectSortJarParams
     * @return
     */
    Boolean dragendSortJar(GitProjectSortJarParams gitProjectSortJarParams);

    Map<String, String> getGitPool();

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
}
