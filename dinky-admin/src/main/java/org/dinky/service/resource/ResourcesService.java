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

package org.dinky.service.resource;

import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.model.Resources;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;

public interface ResourcesService extends IService<Resources> {

    TreeNodeDTO createFolder(Integer pid, String fileName, String desc);

    void rename(Integer id, String fileName, String desc);

    List<TreeNodeDTO> showByTree(Integer pid, Integer showFloorNum);

    void createTree(
            List<TreeNodeDTO> data, Integer pid, Integer showFloorNum, Integer currentFloor);

    String getContentByResourceId(Integer id);

    void uploadFile(Integer pid, String desc, MultipartFile file);

    void remove(Integer id);

    /**
     * 递归获取所有的资源，从pid到0
     *
     * @param resourcesList data
     * @param pid pid
     * @return data
     */
    List<Resources> getResourceByPidToParent(List<Resources> resourcesList, Integer pid);

    /**
     * 递归获取所有的资源，从id往下穿
     *
     * @param resourcesList data
     * @param pid pid
     * @return data
     */
    List<Resources> getResourceByPidToChildren(List<Resources> resourcesList, Integer pid);
}
