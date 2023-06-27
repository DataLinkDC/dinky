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

import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.Resources;
import org.dinky.data.properties.OssProperties;
import org.dinky.mapper.ResourcesMapper;
import org.dinky.utils.OssTemplate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;

@Service
public class ResourcesService extends ServiceImpl<ResourcesMapper, Resources> {
    OssTemplate ossTemplate;

    {
        OssProperties ossProperties = new OssProperties();
        ossProperties.setAccessKey("minioadmin");
        ossProperties.setSecretKey("minioadmin");
        ossProperties.setEndpoint("http://10.8.16.137:9000");
        ossProperties.setBucketName("test");
        ossTemplate = new OssTemplate(ossProperties);
    }

    public void createFolder(Integer pid, String fileName, String desc) {
        long count =
                count(
                        new LambdaQueryWrapper<Resources>()
                                .eq(Resources::getPid, pid)
                                .eq(Resources::getFileName, fileName));
        if (count > 0) {
            throw new BusException("folder is exists!");
        }
        String path = "/" + fileName;
        Resources resources = new Resources();
        resources.setPid(pid);
        resources.setFileName(fileName);
        resources.setIsDirectory(true);
        resources.setType(0);
        resources.setFullName(pid < 1 ? path : getById(pid).getFullName() + path);
        resources.setDescription(desc);
        save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void rename(Integer id, String fileName, String desc) {
        Resources byId = getById(id);
        Assert.notNull(byId, () -> new BusException("resource is not exists!"));
        long count =
                count(
                        new LambdaQueryWrapper<Resources>()
                                .eq(Resources::getPid, byId.getPid())
                                .eq(Resources::getFileName, fileName));
        Assert.isFalse(count > 0, () -> new BusException("folder is exists!"));
        List<String> split = StrUtil.split(byId.getFullName(), "/");
        split.remove(split.size() - 1);
        split.add(fileName);
        String fullName = StrUtil.join("/", split);
        if (!byId.getIsDirectory()) {
            List<Resources> list =
                    list(new LambdaQueryWrapper<Resources>().eq(Resources::getPid, byId.getId()));
            if (CollUtil.isNotEmpty(list)) {
                list.forEach(x -> x.setFullName(fullName + "/" + x.getFileName()));
                updateBatchById(list);
            }
        }
        byId.setDescription(desc);
        byId.setFileName(fileName);
        byId.setFullName(fullName);
        updateById(byId);
    }

    public List<TreeNodeDTO> showByTree(Integer pid, Integer showFloorNum) {
        if (pid < 0) {
            pid = 0;
        }
        showFloorNum = Opt.ofNullable(showFloorNum).orElse(2);
        List<TreeNodeDTO> data = new ArrayList<>();
        createTree(data, pid, showFloorNum, 0);
        return data;
    }

    public void createTree(
            List<TreeNodeDTO> data, Integer pid, Integer showFloorNum, Integer currentFloor) {
        if (currentFloor > showFloorNum) {
            return;
        }
        List<Resources> list = list(new LambdaQueryWrapper<Resources>().eq(Resources::getPid, pid));
        for (Resources resources : list) {
            TreeNodeDTO tree = convertTree(resources);
            if (resources.getIsDirectory()) {
                List<TreeNodeDTO> children = new ArrayList<>();
                tree.setChildren(children);
                createTree(children, resources.getId(), showFloorNum, currentFloor++);
            }
            data.add(tree);
        }
    }

    private static TreeNodeDTO convertTree(Resources resources) {
        TreeNodeDTO treeNodeDTO = new TreeNodeDTO();
        treeNodeDTO.setId(resources.getId());
        treeNodeDTO.setName(resources.getFileName());
        treeNodeDTO.setSize(resources.getSize());
        treeNodeDTO.setParentId(resources.getPid());
        treeNodeDTO.setPath(resources.getFullName());
        treeNodeDTO.setLeaf(resources.getIsDirectory());
        return treeNodeDTO;
    }

    public String getContentByResourceId(Integer id) {

        Resources resources = getById(id);
        // todo: 读取文件内容
        return "text";
    }
}
