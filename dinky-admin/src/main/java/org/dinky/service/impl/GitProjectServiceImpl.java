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

import org.dinky.data.dto.GitAnalysisJarDTO;
import org.dinky.data.dto.GitProjectDTO;
import org.dinky.data.dto.GitProjectSortJarDTO;
import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.model.GitProject;
import org.dinky.function.pool.UdfCodePool;
import org.dinky.mapper.GitProjectMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.GitProjectService;
import org.dinky.utils.GitRepository;
import org.dinky.utils.TreeUtil;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@Service
public class GitProjectServiceImpl extends SuperServiceImpl<GitProjectMapper, GitProject> implements GitProjectService {

    @Override
    public void saveOrUpdate(GitProjectDTO gitProjectDTO) {
        //        select * from d where name == '' and id <> 1
        Long id = gitProjectDTO.getId();
        LambdaQueryWrapper<GitProject> isExistsSameName = new LambdaQueryWrapper<GitProject>()
                .eq(GitProject::getName, gitProjectDTO.getName())
                .ne(id != null, GitProject::getId, id);
        if (baseMapper.exists(isExistsSameName)) {
            throw new DinkyException("project name is exists.");
        }

        GitProject gitProject = BeanUtil.toBean(gitProjectDTO, GitProject.class);
        if (gitProject.getOrderLine() == null) {
            Integer maxOrderLine = Opt.ofNullable(baseMapper
                            .selectOne(new LambdaQueryWrapper<GitProject>()
                                    .orderByAsc(GitProject::getOrderLine)
                                    .last(" limit 1"))
                            .getOrderLine())
                    .orElse(999);
            gitProject.setOrderLine(maxOrderLine + 1);
        }
        BeanUtil.copyProperties(gitProjectDTO, gitProject);

        gitProject.insertOrUpdate();

        ThreadUtil.execAsync(() -> UdfCodePool.updateGitPool(getGitPool()));
    }

    /** @param gitProjectDTOList */
    @Override
    public void dragendSortProject(Map gitProjectDTOList) {
        List<GitProject> gitProjectList =
                BeanUtil.copyToList(((List<?>) gitProjectDTOList.get("sortList")), GitProject.class);
        for (GitProject gitProject : gitProjectList) {
            updateById(gitProject);
        }
    }

    /** @param gitProjectSortJarDTO */
    @Override
    public Boolean dragendSortJar(GitProjectSortJarDTO gitProjectSortJarDTO) {
        GitProject gitProject = getById(gitProjectSortJarDTO.getProjectId());
        if (gitProject == null) {
            return false;
        } else {
            String jarClasses = JSONUtil.toJsonStr(gitProjectSortJarDTO.getJars());
            gitProject.setUdfClassMapList(jarClasses);
            return updateById(gitProject);
        }
    }

    @Override
    public Map<String, String> getGitPool() {
        List<GitProject> list = list();
        Map<String, String> gitPool = new LinkedHashMap<>();
        Opt.ofEmptyAble(list).ifPresent(l -> {
            for (GitProject gitProject : list) {
                List<GitAnalysisJarDTO> gitAnalysisJarList =
                        JSONUtil.toList(gitProject.getUdfClassMapList(), GitAnalysisJarDTO.class);
                for (GitAnalysisJarDTO analysisJarDTO : gitAnalysisJarList) {
                    analysisJarDTO.getClassList().forEach(udf -> {
                        gitPool.computeIfAbsent(udf, k -> analysisJarDTO.getJarPath());
                    });
                }
            }
        });
        return gitPool;
    }

    @Override
    public List<GitProject> list() {
        return list(new LambdaQueryWrapper<GitProject>().orderByAsc(GitProject::getOrderLine));
    }

    @Override
    public Boolean modifyGitProjectStatus(Integer id) {
        GitProject gitProject = baseMapper.selectById(id);
        gitProject.setEnabled(!gitProject.getEnabled());
        return updateById(gitProject);
    }

    /**
     * get project code tree
     *
     * @param id {@link Integer}
     * @return {@link List < GitProjectCodeTreeDTO >}
     */
    @Override
    public List<TreeNodeDTO> getProjectCode(Integer id) {
        GitProject gitProject = getById(id);
        File projectDir = new File(GitRepository.getProjectDir(gitProject.getName()), gitProject.getBranch());
        return TreeUtil.treeNodeData(projectDir, true);
    }

    /**
     * remove project and code cascade by id
     *
     * @param id {@link Integer}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeProjectAndCodeCascade(Integer id) {
        GitProject gitProject = getById(id);
        File projectDir = GitRepository.getProjectDir(gitProject.getName());
        Boolean gitProjectDel = removeById(gitProject);
        if (!gitProjectDel) {
            return false;
        } else {
            return FileUtil.del(projectDir);
        }
    }

    /**
     * match step by code type
     *
     * @param codeType {@link Integer}
     * @param step {@link Integer}
     * @return {@link String}
     */
    private String matchStepByCodeType(Integer codeType, Integer step) {
        String stepMatch = "";
        switch (codeType) {
            case 1:
                stepMatch = matchStepByStepToJava(step);
                break;
            case 2:
                stepMatch = matchStepByStepToJava(step);
                break;
            default:
                break;
        }
        return stepMatch;
    }

    /**
     * match step by step to java type
     *
     * @param step {@link Integer}
     * @return {@link String}
     */
    private String matchStepByStepToJava(Integer step) {
        String stepMatch = "";
        switch (step) {
            case 1:
                stepMatch = "Check Env";
                break;
            case 2:
                stepMatch = "Git Clone";
                break;
            case 3:
                stepMatch = "Maven Build";
                break;
            case 4:
                stepMatch = "Get Jars";
                break;
            case 5:
                stepMatch = "Analysis UDF";
                break;
            case 6:
                stepMatch = "Finish";
                break;
            default:
                break;
        }
        return stepMatch;
    }
}
