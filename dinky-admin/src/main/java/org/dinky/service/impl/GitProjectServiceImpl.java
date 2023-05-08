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
import org.dinky.dto.GitProjectTreeNodeDTO;
import org.dinky.dto.JarClassesDTO;
import org.dinky.mapper.GitProjectMapper;
import org.dinky.model.GitProject;
import org.dinky.process.exception.DinkyException;
import org.dinky.service.GitProjectService;
import org.dinky.utils.GitProjectStepSseFactory;
import org.dinky.utils.GitRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@Service
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
    public void updateState(Integer id) {
        GitProject gitProject = baseMapper.selectById(id);
        gitProject.setEnabled(!gitProject.getEnabled());
        gitProject.updateById();
    }

    /**
     * get project code tree
     *
     * @param id {@link Integer}
     * @return {@link List < GitProjectCodeTreeDTO >}
     */
    @Override
    public List<GitProjectTreeNodeDTO> getProjectCode(Integer id) {
        GitProject gitProject = getById(id);
        File projectDir = GitRepository.getProjectDir(gitProject.getName());
        return treeNodeData(projectDir);
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
     * get project build all logs
     *
     * @param id {@link Integer}
     * @return {@link String}
     */
    @Override
    public String getAllBuildLog(Integer id) {
        GitProject gitProject = getById(id);
        StringBuilder sb = new StringBuilder();
        File logDir =
                GitProjectStepSseFactory.getLogDir(gitProject.getName(), gitProject.getBranch());
        if (logDir.exists() && logDir.isDirectory()) {
            File[] files = logDir.listFiles();
            // sort by file name asc order to read log
            Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
            if (files != null) {
                for (File file : files) {
                    try {
                        buildAllStepLogs(file, sb, gitProject.getCodeType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * get project build step logs of build result
     *
     * @param file {@link File}
     * @param sb {@link StringBuilder}
     * @param codeType {@link Integer}
     * @throws IOException
     */
    private void buildAllStepLogs(File file, StringBuilder sb, Integer codeType)
            throws IOException {
        Integer step = Integer.valueOf(file.getName().split("\\.")[0]);
        String buildStepTitle = matchStepByCodeType(codeType, step);
        sb.append(
                String.format(
                        "########################### Step: %s ###########################\n",
                        buildStepTitle));
        if (step == 5) {
            buildStep5Logs(file, sb);
            sb.append("\n\n\n\n");
        } else {
            sb.append(readFile(file));
            sb.append("\n\n\n\n");
        }
    }

    /**
     * build tree node data
     *
     * @param projectDir {@link File}
     * @return {@link List < GitProjectTreeNodeDTO >}
     */
    private static List<GitProjectTreeNodeDTO> treeNodeData(File projectDir) {
        List<GitProjectTreeNodeDTO> nodes = new ArrayList<>();
        if (projectDir.exists() && projectDir.isDirectory()) {

            GitProjectTreeNodeDTO rootNode =
                    new GitProjectTreeNodeDTO(
                            projectDir.getName(),
                            projectDir.getAbsolutePath(),
                            true,
                            new ArrayList<>(),
                            0L);
            buildTree(projectDir, rootNode);
            nodes.add(rootNode);
        }
        return nodes;
    }

    /**
     * build tree
     *
     * @param file {@link File}
     * @param parentNode {@link GitProjectTreeNodeDTO}
     */
    private static void buildTree(File file, GitProjectTreeNodeDTO parentNode) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {

                    GitProjectTreeNodeDTO childNode =
                            new GitProjectTreeNodeDTO(
                                    child.getName(),
                                    child.getAbsolutePath(),
                                    child.isDirectory(),
                                    new ArrayList<>(),
                                    child.length());
                    parentNode.getChildren().add(childNode);
                    buildTree(child, childNode);
                }
            }
        } else {
            try {
                String content = readFile(file);
                parentNode.setLeaf(file.isDirectory());
                parentNode.setContent(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * read file
     *
     * @param file {@link File}
     * @return {@link String}
     * @throws IOException
     */
    private static String readFile(File file) throws IOException {
        return FileUtil.readUtf8String(file);
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

    /**
     * build step 5 logs , jar and udf class list , because jar and udf class is a list
     *
     * @param file
     * @param sb
     * @throws IOException
     */
    private void buildStep5Logs(File file, StringBuilder sb) throws IOException {
        String step5Log = readFile(file);
        sb.append("Jar And UDF Class List:\n");
        List<JarClassesDTO> jarClassesDTOList = JSONUtil.toList(step5Log, JarClassesDTO.class);
        jarClassesDTOList.stream()
                .forEach(
                        jarClassesDTO -> {
                            sb.append(jarClassesDTO.getJarPath());
                            jarClassesDTO.getClassList().stream()
                                    .forEach(
                                            item -> {
                                                sb.append("\n\t");
                                                sb.append(item);
                                            });
                            sb.append("\n");
                        });
    }
}
