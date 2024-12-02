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

package org.dinky.service.catalogue.impl;

import static org.dinky.assertion.Asserts.isNull;

import org.dinky.assertion.Asserts;
import org.dinky.data.bo.catalogue.export.ExportCatalogueBO;
import org.dinky.data.bo.catalogue.export.ExportTaskBO;
import org.dinky.data.dto.CatalogueTaskDTO;
import org.dinky.data.dto.CatalogueTreeQueryDTO;
import org.dinky.data.dto.ImportCatalogueDTO;
import org.dinky.data.enums.CatalogueSortValueEnum;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.enums.JobStatus;
import org.dinky.data.enums.SortTypeEnum;
import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.Catalogue;
import org.dinky.data.model.Metrics;
import org.dinky.data.model.Task;
import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.data.model.job.History;
import org.dinky.data.model.job.JobHistory;
import org.dinky.data.model.job.JobInstance;
import org.dinky.data.result.Result;
import org.dinky.data.vo.ExportCatalogueVO;
import org.dinky.data.vo.TreeVo;
import org.dinky.mapper.CatalogueMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.HistoryService;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.MonitorService;
import org.dinky.service.TaskService;
import org.dinky.service.catalogue.CatalogueService;
import org.dinky.service.catalogue.factory.CatalogueFactory;
import org.dinky.service.catalogue.factory.CatalogueTreeSortFactory;
import org.dinky.service.catalogue.strategy.CatalogueTreeSortStrategy;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.BiMap;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CatalogueServiceImpl
 *
 * @since 2021/5/28 14:02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogueServiceImpl extends SuperServiceImpl<CatalogueMapper, Catalogue> implements CatalogueService {

    private final TaskService taskService;

    private final JobInstanceService jobInstanceService;

    private final HistoryService historyService;

    private final JobHistoryService jobHistoryService;

    private final MonitorService monitorService;

    private final CatalogueTreeSortFactory catalogueTreeSortFactory;

    private final CatalogueFactory catalogueFactory;

    /**
     * @return
     */
    @Override
    public List<Catalogue> getCatalogueTree(CatalogueTreeQueryDTO catalogueTreeQueryDto) {
        List<Catalogue> catalogueTree = buildCatalogueTree(this.list());
        // sort
        CatalogueTreeSortStrategy strategy = catalogueTreeSortFactory.getStrategy(catalogueTreeQueryDto.getSortValue());
        return strategy.sort(catalogueTree, SortTypeEnum.getByName(catalogueTreeQueryDto.getSortType()));
    }

    /**
     * build catalogue tree
     *
     * @param catalogueList catalogue list
     * @return catalogue tree
     */
    public List<Catalogue> buildCatalogueTree(List<Catalogue> catalogueList) {
        // sort
        if (CollectionUtil.isNotEmpty(catalogueList)) {
            catalogueList = catalogueList.stream()
                    .sorted(Comparator.comparing(Catalogue::getId))
                    .collect(Collectors.toList());
        }
        List<Task> taskList = taskService.list();
        List<Catalogue> returnList = new ArrayList<>();
        for (Catalogue catalogue : catalogueList) {
            //  get all child catalogue of parent catalogue id , the 0 is root catalogue
            if (catalogue.getParentId() == 0) {
                recursionBuildCatalogueAndChildren(catalogueList, catalogue, taskList);
                returnList.add(catalogue);
            }
        }
        if (returnList.isEmpty()) {
            returnList = catalogueList;
        }
        return returnList;
    }

    /**
     * recursion build catalogue and children
     *
     * @param list
     * @param catalogues
     */
    private void recursionBuildCatalogueAndChildren(List<Catalogue> list, Catalogue catalogues, List<Task> taskList) {
        // 得到子节点列表
        List<Catalogue> childList = getChildList(list, catalogues);
        catalogues.setChildren(childList);
        for (Catalogue tChild : childList) {
            if (hasChild(list, tChild)) {
                // Determine whether there are child nodes
                for (Catalogue children : childList) {
                    recursionBuildCatalogueAndChildren(list, children, taskList);
                }
            } else {
                if (tChild.getIsLeaf() || null != tChild.getTaskId()) {
                    taskList.stream()
                            .filter(t -> t.getId().equals(tChild.getTaskId()))
                            .findFirst()
                            .ifPresent(tChild::setTaskAndNote);
                }
            }
        }
    }

    /**
     * Determine whether there are child nodes
     *
     * @param list
     * @param catalogue
     * @return
     */
    private boolean hasChild(List<Catalogue> list, Catalogue catalogue) {
        return !getChildList(list, catalogue).isEmpty();
    }

    /**
     * get child list
     *
     * @param list
     * @param catalogue
     * @return
     */
    private List<Catalogue> getChildList(List<Catalogue> list, Catalogue catalogue) {
        List<Catalogue> childList = new ArrayList<>();
        for (Catalogue n : list) {
            if (n.getParentId().longValue() == catalogue.getId().longValue()) {
                childList.add(n);
            }
        }
        return childList;
    }

    /**
     * Get the catalogue sort type
     *
     * @return A list of {@link TreeVo} objects representing the catalogue sort type.
     */
    @Override
    public List<TreeVo> getCatalogueSortType() {
        List<TreeVo> treeVoList = Lists.newArrayList();
        for (CatalogueSortValueEnum catalogueSortValueEnum : CatalogueSortValueEnum.values()) {
            String catalogueSortValueEnumName = catalogueSortValueEnum.getName();
            String catalogueSortValueEnumI18nValue = catalogueSortValueEnum.getI18nValue();
            TreeVo treeVo = TreeVo.builder()
                    .name(catalogueSortValueEnumI18nValue)
                    .value(catalogueSortValueEnumName)
                    .build();
            List<TreeVo> subTreeVoList = Arrays.stream(SortTypeEnum.values())
                    .map(sortTypeEnum -> {
                        String sortTypeEnumName = sortTypeEnum.getName();
                        String sortTypeEnumI18nValue = sortTypeEnum.getI18nValue();
                        return TreeVo.builder()
                                .name(catalogueSortValueEnumI18nValue + " " + sortTypeEnumI18nValue)
                                .value(catalogueSortValueEnumName + "_" + sortTypeEnumName)
                                .build();
                    })
                    .collect(Collectors.toList());
            treeVo.setChildren(subTreeVoList);
            treeVoList.add(treeVo);
        }
        return treeVoList;
    }

    @Override
    public Catalogue findByParentIdAndName(Integer parentId, String name) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Catalogue>()
                .eq(Catalogue::getParentId, parentId)
                .eq(Catalogue::getName, name));
    }

    /**
     * check catalogue task name is exist
     *
     * @param name name
     * @param id   id
     * @return true if exist , otherwise false
     */
    @Override
    public boolean checkCatalogueTaskNameIsExistById(String name, Integer id) {
        return getBaseMapper()
                .exists(new LambdaQueryWrapper<Catalogue>()
                        .eq(Catalogue::getName, name)
                        .ne(id != null, Catalogue::getId, id));
    }

    /**
     * init some value
     *
     * @param catalogueTask {@link CatalogueTaskDTO}
     * @return {@link Task}
     */
    private Task initTaskValue(CatalogueTaskDTO catalogueTask) {
        Task task = new Task();
        if (Opt.ofNullable(catalogueTask.getTask()).isPresent()) {
            task = catalogueTask.getTask().buildTask();
        } else {
            String dialect = catalogueTask.getType();
            catalogueFactory.resetTask(task, dialect);
        }
        if (!Opt.ofNullable(task.getStep()).isPresent()) {
            task.setStep(JobLifeCycle.DEVELOP.getValue());
        }
        return task;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Catalogue saveOrUpdateCatalogueAndTask(CatalogueTaskDTO catalogueTaskDTO) {
        Task task = null;
        Catalogue catalogue = null;
        if (catalogueTaskDTO.getId() == null) {
            task = initTaskValue(catalogueTaskDTO);
            catalogue = new Catalogue();
        } else {
            catalogue = baseMapper.selectById(catalogueTaskDTO.getId());
            task = taskService.getById(catalogue.getTaskId());
        }
        task.setName(catalogueTaskDTO.getName());
        task.setDialect(catalogueTaskDTO.getType());
        task.setConfigJson(catalogueTaskDTO.getConfigJson());
        task.setNote(catalogueTaskDTO.getNote());
        task.setFirstLevelOwner(catalogueTaskDTO.getFirstLevelOwner());
        task.setSecondLevelOwners(catalogueTaskDTO.getSecondLevelOwners());
        taskService.saveOrUpdateTask(task);

        catalogue.setTenantId(catalogueTaskDTO.getTenantId());
        catalogue.setName(catalogueTaskDTO.getName());
        catalogue.setIsLeaf(true);
        catalogue.setTaskId(task.getId());
        catalogue.setType(catalogueTaskDTO.getType());
        catalogue.setParentId(catalogueTaskDTO.getParentId());
        this.saveOrUpdate(catalogue);
        return catalogue;
    }

    @Override
    public Catalogue createCatalogAndFileTask(CatalogueTaskDTO catalogueTaskDTO, String ment) {
        Task task = new Task();
        task.setName(catalogueTaskDTO.getName());
        task.setDialect(catalogueTaskDTO.getDialect());
        task.setStatement(ment);
        task.setEnabled(true);
        taskService.saveOrUpdateTask(task);
        Catalogue catalogue = new Catalogue();
        catalogue.setName(catalogueTaskDTO.getName());
        catalogue.setIsLeaf(true);
        catalogue.setTaskId(task.getId());
        catalogue.setType(catalogueTaskDTO.getDialect());
        catalogue.setParentId(catalogueTaskDTO.getParentId());
        this.save(catalogue);
        return catalogue;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean toRename(Catalogue catalogue) {
        Catalogue oldCatalogue = this.getById(catalogue.getId());
        if (isNull(oldCatalogue)) {
            return false;
        } else {
            Task task = new Task();
            task.setId(oldCatalogue.getTaskId());
            task.setName(catalogue.getName());
            taskService.updateById(task);
            this.updateById(catalogue);
            return true;
        }
    }

    private void findAllCatalogueInDir(Integer id, List<Catalogue> all, Set<Catalogue> del) {
        List<Catalogue> relatedList = all.stream()
                .filter(catalogue -> id.equals(catalogue.getId()) || id.equals(catalogue.getParentId()))
                .collect(Collectors.toList());
        List<Catalogue> subDirCatalogue = relatedList.stream()
                .filter(catalogue -> catalogue.getType() == null)
                .collect(Collectors.toList());
        subDirCatalogue.forEach(catalogue -> {
            if (!id.equals(catalogue.getId())) {
                findAllCatalogueInDir(catalogue.getId(), all, del);
            }
        });
        del.addAll(relatedList);
    }

    private List<String> analysisActiveCatalogues(Set<Catalogue> del) {
        List<Integer> actives = jobInstanceService.listJobInstanceActive().stream()
                .map(JobInstance::getTaskId)
                .collect(Collectors.toList());
        List<Catalogue> activeCatalogue = del.stream()
                .filter(catalogue -> catalogue.getTaskId() != null && actives.contains(catalogue.getTaskId()))
                .collect(Collectors.toList());
        return activeCatalogue.stream()
                .map(catalogue -> taskService.getById(catalogue.getTaskId()).getName())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean moveCatalogue(Integer id, Integer parentId) {
        Catalogue catalogue = this.getById(id);
        if (isNull(catalogue)) {
            return false;
        } else {
            catalogue.setParentId(parentId);
            return updateById(catalogue);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean copyTask(Catalogue catalogue) {

        if (ObjectUtil.isNull(catalogue.getTaskId())) {
            return false;
        }

        Task oldTask = taskService.getById(catalogue.getTaskId());

        if (ObjectUtil.isNull(oldTask)) {
            return false;
        }
        // 查询作业名称
        int size = taskService.queryAllSizeByName(oldTask.getName());
        // 设置复制后的作业名称为：原名称+自增序列
        String newTaskName = oldTask.getName() + "-" + (++size);
        Task newTask = catalogueFactory.getNewTask(oldTask, newTaskName);
        taskService.save(newTask);

        Catalogue singleCatalogue =
                this.getOne(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getTaskId, catalogue.getTaskId()));
        Catalogue newCatalogue = catalogueFactory.getNewCatalogue(catalogue, singleCatalogue, newTask);
        return this.save(newCatalogue);
    }

    @Override
    public Integer addDependCatalogue(String[] catalogueNames) {
        Integer parentId = 0;
        for (int i = 0; i < catalogueNames.length - 1; i++) {
            String catalogueName = catalogueNames[i];
            Catalogue catalogue = getOne(new QueryWrapper<Catalogue>()
                    .eq("name", catalogueName)
                    .eq("parent_id", parentId)
                    .last(" limit 1"));
            if (Asserts.isNotNull(catalogue)) {
                parentId = catalogue.getId();
                continue;
            }
            catalogue = new Catalogue();
            catalogue.setName(catalogueName);
            catalogue.setParentId(parentId);
            catalogue.setIsLeaf(false);
            this.save(catalogue);
            parentId = catalogue.getId();
        }
        return parentId;
    }

    @Override
    public void traverseFile(String sourcePath, Catalogue catalog) {
        File file = new File(sourcePath);
        File[] fs = file.listFiles();
        if (fs == null) {
            throw new RuntimeException("the dir is error");
        }
        for (File fl : fs) {
            if (fl.isFile()) {
                CatalogueTaskDTO dto = getCatalogueTaskDTO(
                        fl.getName(),
                        findByParentIdAndName(catalog.getParentId(), catalog.getName())
                                .getId());
                String fileText = getFileText(fl);
                createCatalogAndFileTask(dto, fileText);
            } else {
                Catalogue newCata = getCatalogue(
                        findByParentIdAndName(catalog.getParentId(), catalog.getName())
                                .getId(),
                        fl.getName());
                traverseFile(fl.getPath(), newCata);
            }
        }
    }

    private String getFileText(File sourceFile) {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(Files.newInputStream(sourceFile.toPath()));
                BufferedReader br = new BufferedReader(isr)) {
            if (sourceFile.isFile() && sourceFile.exists()) {

                String lineText;
                while ((lineText = br.readLine()) != null) {
                    sb.append(lineText).append("\n");
                }
            }
        } catch (Exception e) {
            log.error("read file error, {} ", e);
        }
        return sb.toString();
    }

    @Override
    public Catalogue getCatalogue(Integer parentId, String name) {
        Catalogue subcata = new Catalogue();
        subcata.setTaskId(null);
        subcata.setName(name);
        subcata.setType("null");
        subcata.setParentId(parentId);
        subcata.setIsLeaf(false);
        saveOrUpdate(subcata);
        return subcata;
    }

    /**
     * @param catalogueId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteCatalogueById(Integer catalogueId) {
        List<Catalogue> catalogues = list(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getParentId, catalogueId));
        if (!catalogues.isEmpty()) {
            return Result.failed(Status.FOLDER_NOT_EMPTY);
        }
        // 获取 catalogue 表中的作业
        Catalogue catalogue = getById(catalogueId);

        // 判断书否为空 且 是否为叶子节点
        if (BeanUtil.isNotEmpty(catalogue) && catalogue.getIsLeaf()) {
            // doing: cascade delete jobInstance && jobHistory && history && statement
            // 获取 task 表中的作业
            Task task = taskService.getById(catalogue.getTaskId());
            if (task != null) {
                if (task.getStep().equals(JobLifeCycle.PUBLISH.getValue())) {
                    throw new BusException(Status.TASK_IS_PUBLISH_CANNOT_DELETE);
                }
                if (task.getJobInstanceId() != null) {
                    // 获取当前 job instance
                    JobInstance currentJobInstance = jobInstanceService.getById(task.getJobInstanceId());
                    if (currentJobInstance != null) {
                        // 获取前 先强制刷新一下, 避免获取任务信息状态不准确
                        JobInfoDetail jobInfoDetail =
                                jobInstanceService.refreshJobInfoDetail(task.getJobInstanceId(), task.getId(), true);
                        if (jobInfoDetail.getInstance().getStatus().equals(JobStatus.RUNNING.getValue())) {
                            throw new BusException(Status.TASK_IS_RUNNING_CANNOT_DELETE);
                        }
                    }
                }
            }

            // 获取 metrics 表中的监控数据
            List<Metrics> metricListByTaskId = monitorService.getMetricsLayoutByTaskId(catalogue.getTaskId());

            // 获取 job instance 表中的作业
            List<JobInstance> jobInstanceList = jobInstanceService.list(
                    new LambdaQueryWrapper<JobInstance>().eq(JobInstance::getTaskId, catalogue.getTaskId()));
            //  获取 history 表中的作业
            List<History> historyList = historyService.list(
                    new LambdaQueryWrapper<History>().eq(History::getTaskId, catalogue.getTaskId()));
            historyList.forEach(history -> {
                // 查询 job history 表中的作业 通过 id 关联查询 // TODO npe
                JobHistory historyServiceById = jobHistoryService.getById(history.getId());
                if (historyServiceById != null) {
                    // 删除 job history 表中的作业
                    jobHistoryService.removeById(historyServiceById.getId());
                }
                // 删除 history 表中的作业
                historyService.removeById(history.getId());
            });

            // 删除 job instance 表中的作业
            jobInstanceList.forEach(jobInstance -> jobInstanceService.removeById(jobInstance.getId()));
            // 删除 task 表中的作业
            if (task != null) {
                taskService.removeById(task.getId());
            }

            if (CollUtil.isNotEmpty(metricListByTaskId)) {
                metricListByTaskId.forEach(metrics -> monitorService.removeById(metrics.getId()));
                // todo: 需要删除 paimon 中的监控数据, 但是 paimon 中没有提供删除接口
            }
        }

        // 如果是文件夹 , 且下边没有子文件夹 , 则删除
        return Result.succeed(removeById(catalogueId) ? Status.DELETE_SUCCESS : Status.DELETE_FAILED);
    }

    /**
     * <p>
     * 1. save catalogue
     * 2. save task
     * 3. save statement
     * 4. rename
     *
     * @param catalogue
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdateOrRename(Catalogue catalogue) {
        if (taskService.getById(catalogue.getTaskId()) != null) {
            toRename(catalogue);
        }
        return saveOrUpdate(catalogue);
    }

    /**
     * Check if the catalogue name is exist
     * @param catalogue catalogue
     * @return true if the catalogue name is exist
     */
    @Override
    public Boolean checkNameIsExistByParentId(Catalogue catalogue) {
        return getBaseMapper()
                .exists(new LambdaQueryWrapper<Catalogue>()
                        .eq(Catalogue::getName, catalogue.getName())
                        .ne(catalogue.getParentId() != null, Catalogue::getParentId, catalogue.getParentId()));
    }

    private CatalogueTaskDTO getCatalogueTaskDTO(String name, Integer parentId) {
        CatalogueTaskDTO catalogueTaskDTO = new CatalogueTaskDTO();
        catalogueTaskDTO.setName(UUID.randomUUID().toString().substring(0, 6) + name);
        catalogueTaskDTO.setId(null);
        catalogueTaskDTO.setParentId(parentId);
        catalogueTaskDTO.setLeaf(true);
        return catalogueTaskDTO;
    }

    @Override
    public Boolean checkTaskOperatePermission(Integer catalogueId) {
        Catalogue catalogue = getById(catalogueId);
        if (Objects.nonNull(catalogue) && catalogue.getIsLeaf() && Objects.nonNull(catalogue.getTaskId())) {
            return taskService.checkTaskOperatePermission(catalogue.getTaskId());
        }
        return null;
    }

    /**
     * Export catalogue by id
     *
     * @param catalogueId catalogue id
     * @return export catalogue vo
     */
    @Override
    public ExportCatalogueVO exportCatalogue(Integer catalogueId) {
        log.info("Export catalogue: {}", catalogueId);
        ExportCatalogueBO exportCatalogueBo = getAllCatalogue(catalogueId);
        String dataJson = JSONUtil.toJsonPrettyStr(exportCatalogueBo);
        return ExportCatalogueVO.builder()
                .fileName(getExportCatalogueFileName(catalogueId))
                .dataJson(dataJson)
                .build();
    }

    /**
     * Import catalogue
     *
     * @param importCatalogueDto ImportCatalogueDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importCatalogue(ImportCatalogueDTO importCatalogueDto) {
        log.info("Import Catalogue start. importCatalogueDto: {}", importCatalogueDto);
        Integer parentCatalogueId = importCatalogueDto.getParentCatalogueId();
        ExportCatalogueBO exportCatalogue = importCatalogueDto.getExportCatalogue();
        if (Objects.isNull(exportCatalogue)) {
            throw new BusException(Status.FAILED);
        }
        Catalogue parentCatalogue = this.getById(parentCatalogueId);
        // check param
        BiMap<String, String> existsNameMap = checkImportCatalogueParam(parentCatalogue, exportCatalogue);

        // create catalogue and task
        List<Task> createTasks = Lists.newArrayList();
        List<Catalogue> createCatalogues = Lists.newArrayList();
        List<ExportCatalogueBO> searchCatalogues = Lists.newArrayList(exportCatalogue);

        // ExportCatalogueBO -> Task mapping
        Map<ExportCatalogueBO, Task> exportCatalogueTaskMap = Maps.newHashMap();
        // ExportCatalogueBO -> Catalogue mapping
        Map<ExportCatalogueBO, Catalogue> exportCatalogueMap = Maps.newHashMap();
        // ExportCatalogueBO -> Parent Catalogue ID mapping
        Map<ExportCatalogueBO, Integer> exportCatalogueParentIdMap = Maps.newHashMap();
        exportCatalogueParentIdMap.put(exportCatalogue, parentCatalogueId);

        Integer currentUserId = getCurrentUserId();
        while (CollectionUtil.isNotEmpty(searchCatalogues)) {
            List<ExportCatalogueBO> nextSearchCatalogues = Lists.newArrayList();
            // create task
            for (ExportCatalogueBO searchCatalogue : searchCatalogues) {
                ExportTaskBO exportTaskBO = searchCatalogue.getTask();
                if (Objects.nonNull(exportTaskBO)) {
                    Task task = catalogueFactory.getTask(exportTaskBO, currentUserId);
                    if (existsNameMap.containsKey(task.getName())) {
                        task.setName(existsNameMap.get(task.getName()));
                    }
                    createTasks.add(task);
                    exportCatalogueTaskMap.put(searchCatalogue, task);
                }
            }
            taskService.saveBatch(createTasks);
            // create catalogue
            for (ExportCatalogueBO searchCatalogue : searchCatalogues) {
                Task task = exportCatalogueTaskMap.get(searchCatalogue);
                Integer taskId = Objects.nonNull(task) ? task.getId() : null;
                Integer parentId = exportCatalogueParentIdMap.get(searchCatalogue);
                if (Objects.isNull(parentId)) {
                    log.error("Not found parent id. searchCatalogue: {}", searchCatalogue);
                    throw new BusException(Status.FAILED);
                }
                Catalogue catalogue = catalogueFactory.getCatalogue(searchCatalogue, parentId, taskId);
                if (existsNameMap.containsKey(catalogue.getName())) {
                    catalogue.setName(existsNameMap.get(catalogue.getName()));
                }
                createCatalogues.add(catalogue);
                exportCatalogueMap.put(searchCatalogue, catalogue);
                List<ExportCatalogueBO> children = searchCatalogue.getChildren();
                if (CollectionUtil.isNotEmpty(children)) {
                    nextSearchCatalogues.addAll(children);
                }
            }
            this.saveBatch(createCatalogues);
            // put parent id
            for (ExportCatalogueBO searchCatalogue : searchCatalogues) {
                List<ExportCatalogueBO> children = searchCatalogue.getChildren();
                if (CollectionUtil.isEmpty(children)) {
                    continue;
                }
                Catalogue catalogue = exportCatalogueMap.get(searchCatalogue);
                for (ExportCatalogueBO child : children) {
                    exportCatalogueParentIdMap.put(child, catalogue.getId());
                }
            }
            createTasks.clear();
            createCatalogues.clear();
            searchCatalogues = nextSearchCatalogues;
        }
        log.info("Import Catalogue success. The number of Catalogue created is: {}", exportCatalogueMap.size());
    }

    @VisibleForTesting
    protected Integer getCurrentUserId() {
        return StpUtil.getLoginIdAsInt();
    }

    private BiMap<String, String> checkImportCatalogueParam(
            Catalogue parentCatalogue, ExportCatalogueBO exportCatalogue) {
        // verify that the parent directory exists
        if (Objects.isNull(parentCatalogue)) {
            throw new BusException(Status.CATALOGUE_NOT_EXIST);
        }
        List<String> catalogueNames = getCatalogueNames(exportCatalogue);
        // check if a catalogue with the same name exists
        BiMap<String, String> existsNameMap = new BiMap<>(new HashMap<>());
        getNotExistsCatalogueName(catalogueNames, existsNameMap);
        // verify that the task name and parent catalogue name are consistent
        List<ExportCatalogueBO> searchExportCatalogues = Lists.newArrayList(exportCatalogue);
        while (CollectionUtil.isNotEmpty(searchExportCatalogues)) {
            List<ExportCatalogueBO> nextSearchExportCatalogues = Lists.newArrayList();
            for (ExportCatalogueBO searchExportCatalogue : searchExportCatalogues) {
                List<ExportCatalogueBO> children = searchExportCatalogue.getChildren();
                if (CollectionUtil.isNotEmpty(children)) {
                    nextSearchExportCatalogues.addAll(children);
                }
                ExportTaskBO task = searchExportCatalogue.getTask();
                if (Objects.isNull(task)) {
                    continue;
                }
                String catalogueName = searchExportCatalogue.getName();
                String taskName = task.getName();
                if (!StringUtils.equals(catalogueName, taskName)) {
                    throw new BusException(Status.TASK_NAME_NOT_MATCH_CATALOGUE_NAME, catalogueName, taskName);
                }
            }
            searchExportCatalogues = nextSearchExportCatalogues;
        }
        return existsNameMap;
    }

    /**
     * Modify the name of the Catalogue. Duplicate additions to copy data and increments
     *
     * @param catalogueNames catalogue names
     * @return List<String> 不重复的目录名称
     */
    private void getNotExistsCatalogueName(List<String> catalogueNames, BiMap<String, String> existsNameMap) {
        List<Catalogue> existCatalogues =
                this.list(new LambdaQueryWrapper<Catalogue>().in(Catalogue::getName, catalogueNames));
        if (CollectionUtil.isEmpty(existCatalogues)) {
            return;
        }
        List<String> existCataloguesList = existCatalogues.stream()
                .map(Catalogue::getName)
                .map(name -> {
                    String key = name;
                    if (existsNameMap.containsValue(name)) {
                        key = existsNameMap.getKey(name);
                    }
                    // Configure the suffix - copy\(\d+\), match \d+1 if it exists, add the suffix - copy(1) if it does
                    // not exist.
                    String regex = ".*-copy\\((\\d+)\\)";
                    if (name.matches(regex)) {
                        String[] split = name.split("\\(");
                        String num = split[1].split("\\)")[0];
                        int i = Integer.parseInt(num) + 1;
                        existsNameMap.put(key, name.replace(num, String.valueOf(i)));
                    } else {
                        existsNameMap.put(key, name + "-copy(1)");
                    }
                    return existsNameMap.get(key);
                })
                .collect(Collectors.toList());

        getNotExistsCatalogueName(existCataloguesList, existsNameMap);
    }

    private List<String> getCatalogueNames(ExportCatalogueBO exportCatalogue) {
        if (Objects.isNull(exportCatalogue)) {
            return Lists.newArrayList();
        }
        List<String> catalogueNameList = Lists.newArrayList();
        String catalogueName = exportCatalogue.getName();
        catalogueNameList.add(catalogueName);
        List<ExportCatalogueBO> children = exportCatalogue.getChildren();
        if (CollectionUtil.isEmpty(children)) {
            return catalogueNameList;
        }
        for (ExportCatalogueBO child : children) {
            catalogueNameList.addAll(getCatalogueNames(child));
        }
        return catalogueNameList;
    }

    private String getExportCatalogueFileName(Integer catalogueId) {
        return String.format("export_catalogue_%s_%s.json", catalogueId, System.currentTimeMillis());
    }

    private ExportCatalogueBO getAllCatalogue(Integer catalogueId) {
        Catalogue catalogue = this.getById(catalogueId);
        if (Objects.isNull(catalogue)) {
            return null;
        }
        Boolean isLeaf = catalogue.getIsLeaf();
        // only leaf nodes have tasks
        Task task = isLeaf ? taskService.getById(catalogue.getTaskId()) : null;
        ExportCatalogueBO exportCatalogueBo = catalogueFactory.getExportCatalogueBo(catalogue, task);
        List<Catalogue> subCatalogues =
                this.list(new LambdaQueryWrapper<Catalogue>().eq(Catalogue::getParentId, catalogueId));
        if (CollectionUtil.isNotEmpty(subCatalogues)) {
            List<ExportCatalogueBO> subExportCatalogueBo = subCatalogues.stream()
                    .map(Catalogue::getId)
                    .map(this::getAllCatalogue)
                    .collect(Collectors.toList());
            exportCatalogueBo.setChildren(subExportCatalogueBo);
        }
        return exportCatalogueBo;
    }
}
