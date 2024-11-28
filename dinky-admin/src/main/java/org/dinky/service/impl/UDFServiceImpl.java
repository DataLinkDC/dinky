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

import org.dinky.config.Dialect;
import org.dinky.data.model.Resources;
import org.dinky.data.model.udf.UDFManage;
import org.dinky.data.vo.CascaderVO;
import org.dinky.data.vo.UDFManageVO;
import org.dinky.function.FlinkUDFDiscover;
import org.dinky.function.data.model.UDF;
import org.dinky.mapper.UDFManageMapper;
import org.dinky.service.UDFService;
import org.dinky.service.resource.ResourcesService;
import org.dinky.utils.UDFUtils;

import org.apache.flink.table.catalog.FunctionLanguage;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @since 0.6.8
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UDFServiceImpl extends ServiceImpl<UDFManageMapper, UDFManage> implements UDFService {
    private final ResourcesService resourcesService;

    @Override
    public boolean update(UDFManage entity) {
        Assert.notNull(entity, "Entity must be not null");
        Integer id = entity.getId();
        UDFManage byId = getById(id);
        Assert.notNull(byId, "UDFManage not found");
        byId.setName(entity.getName());
        return super.updateById(byId);
    }

    @Override
    public List<UDFManageVO> selectAll() {
        List<UDFManageVO> udfManageList = baseMapper.selectAll();
        return udfManageList.stream()
                .filter(x -> "resources".equals(x.getSource()))
                .peek(x -> {
                    String fileName = x.getFileName();
                    if ("jar".equals(FileUtil.getSuffix(fileName))) {
                        x.setDialect(Dialect.JAVA.getValue());
                        x.setLanguage(Dialect.JAVA.getValue());
                    } else {
                        x.setDialect(Dialect.PYTHON.getValue());
                        x.setLanguage(Dialect.JAVA.getValue());
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Resources> udfResourcesList() {
        return resourcesService.getResourcesTreeByFilter(x -> {
            String suffix = FileUtil.getSuffix(x.getFileName());
            return x.getIsDirectory() || "jar".equals(suffix) || "zip".equals(suffix) || "py".equals(suffix);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addOrUpdateByResourceId(List<Integer> resourceIds) {
        LambdaQueryWrapper<UDFManage> queryWrapper =
                new LambdaQueryWrapper<UDFManage>().and(x -> x.isNotNull(UDFManage::getResourcesId));
        List<UDFManage> udfManageList = baseMapper.selectList(queryWrapper);
        List<Integer> udfManageIdList =
                udfManageList.stream().map(UDFManage::getResourcesId).distinct().collect(Collectors.toList());
        // 1. Delete all UDFs that are not in the resourceIds list.
        List<UDFManage> needDeleteList = udfManageList.stream()
                .filter(x -> !resourceIds.contains(x.getResourcesId()))
                .collect(Collectors.toList());
        removeByIds(needDeleteList);
        // 2. Add all UDFs that are not in the UDFManage table.
        Collection<Integer> needAddList =
                resourceIds.stream().filter(x -> !udfManageIdList.contains(x)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needAddList)) {
            List<Resources> resources = resourcesService.listByIds(needAddList);
            List<UDFManage> manageList = resources.stream()
                    .flatMap(x -> {
                        String suffix = FileUtil.getSuffix(x.getFileName());
                        if ("jar".equals(suffix)) {
                            File file = resourcesService.getFile(x.getId());
                            List<Class<?>> classes = UDFUtils.getUdfClassByJar(file);
                            return classes.stream().map(clazz -> {
                                UDFManage udfManage = UDFManage.builder()
                                        .className(clazz.getName())
                                        .language(FunctionLanguage.JAVA.name())
                                        .resourcesId(x.getId())
                                        .build();
                                udfManage.setName(StrUtil.toUnderlineCase(getSimpleClassName(clazz.getName())));
                                return udfManage;
                            });
                        } else if ("py".equals(suffix) || "zip".equals(suffix)) {
                            File file = resourcesService.getFile(x.getId());
                            List<String> pythonUdfList = UDFUtils.getPythonUdfList(file.getAbsolutePath());
                            return pythonUdfList.stream().map(className -> {
                                UDFManage udfManage = UDFManage.builder()
                                        .className(className)
                                        .resourcesId(x.getId())
                                        .language(FunctionLanguage.PYTHON.name())
                                        .build();
                                udfManage.setName(StrUtil.toUnderlineCase(getSimpleClassName(className)));
                                return udfManage;
                            });
                        } else {
                            log.error("Unsupported file type to add UDFManage, extension: {}", suffix);
                        }
                        return Stream.of();
                    })
                    .collect(Collectors.toList());
            saveBatch(manageList);
        }
    }

    /**
     * @return
     */
    @Override
    public List<UDFManage> getUDFFromUdfManage() {
        // 1. get all resources
        List<Resources> resourcesList = resourcesService.list();
        // 2.  get all udf from udf manage  and then filter the udf by resources id in resources list
        List<UDFManage> collect = this.list().stream()
                .filter(udf -> resourcesList.stream()
                        .anyMatch(resources -> resources.getId().equals(udf.getResourcesId())))
                .collect(Collectors.toList());
        // 去重 根据 className 去重 || distinct by className
        return collect.stream()
                .collect(Collectors.toMap(UDFManage::getClassName, udf -> udf, (a, b) -> a))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    /**
     * get all udf to cascader list
     *
     * @return List
     */
    @Override
    public List<CascaderVO> getAllUdfsToCascader(List<UDF> userDefinedReleaseUdfs) {
        // Get all UDFs of static UDFs and dynamic UDFs
        List<UDF> staticUdfs = FlinkUDFDiscover.getCustomStaticUDFs();

        // get all UDFs of UDFManage table
        List<UDF> udfManageDynamic = getUDFFromUdfManage().stream()
                .map(UDFUtils::resourceUdfManageToUDF)
                .collect(Collectors.toList());

        CascaderVO staticUdfCascaderVO = new CascaderVO(
                "Flink Static UDF",
                staticUdfs.stream()
                        .map(udf -> new CascaderVO(udf.getClassName(), udf.getClassName()))
                        .collect(Collectors.toList()));
        CascaderVO userDefinedUdfCascaderVO = new CascaderVO(
                "User Defined Release UDF",
                userDefinedReleaseUdfs.stream()
                        .map(udf -> new CascaderVO(udf.getClassName(), udf.getClassName()))
                        .collect(Collectors.toList()));
        CascaderVO udfManageDynamicCascaderVO = new CascaderVO(
                "From UDF Manage",
                udfManageDynamic.stream()
                        .map(udf -> new CascaderVO(udf.getClassName(), udf.getClassName()))
                        .collect(Collectors.toList()));

        List<CascaderVO> result = new LinkedList<>();
        result.add(staticUdfCascaderVO);
        result.add(udfManageDynamicCascaderVO);
        result.add(userDefinedUdfCascaderVO);
        return result;
    }

    private static String getSimpleClassName(String className) {
        final List<String> packages = StrUtil.split(className, CharUtil.DOT);
        if (null == packages || packages.size() < 2) {
            return className;
        }
        return CollUtil.getLast(packages);
    }
}
