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

package org.dinky.service.impl;

import org.dinky.db.service.impl.SuperServiceImpl;
import org.dinky.dto.GitProjectDTO;
import org.dinky.mapper.GitProjectMapper;
import org.dinky.model.GitProject;
import org.dinky.process.exception.DinkyException;
import org.dinky.service.GitProjectService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.bean.BeanUtil;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
public class GitProjectServiceImpl extends SuperServiceImpl<GitProjectMapper, GitProject>
        implements GitProjectService {
    @Override
    public void saveOrUpdate(GitProjectDTO gitProjectDTO) {
        //        select * from d where name == '' and id <> 1
        Long id = gitProjectDTO.getId();
        LambdaQueryWrapper<GitProject> isExistsSameName =
                new LambdaQueryWrapper<GitProject>()
                        .eq(GitProject::getName, gitProjectDTO.getName())
                        .ne(id != null, GitProject::getId, id);
        if (baseMapper.exists(isExistsSameName)) {
            throw new DinkyException("project name is exists.");
        }

        GitProject gitProject = BeanUtil.toBean(gitProjectDTO, GitProject.class);
        BeanUtil.copyProperties(gitProjectDTO, gitProject);

        gitProject.insertOrUpdate();
    }

    @Override
    public void updateState(Long id, boolean state) {
        GitProject gitProject = baseMapper.selectById(id);
        gitProject.setEnabled(state);
        gitProject.updateById();
    }
}
