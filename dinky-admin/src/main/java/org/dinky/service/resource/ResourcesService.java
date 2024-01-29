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
import org.dinky.data.result.Result;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;

public interface ResourcesService extends IService<Resources> {

    boolean syncRemoteDirectoryStructure();

    /**
     * Create a new folder with the specified parameters.
     *
     * @param pid The ID of the parent folder.
     * @param fileName The name of the new folder.
     * @param desc A description of the new folder.
     * @return A {@link TreeNodeDTO} object representing the newly created folder.
     */
    TreeNodeDTO createFolder(Integer pid, String fileName, String desc);

    TreeNodeDTO createFolderOrGet(Integer pid, String fileName, String desc);

    /**
     * Rename an existing folder with the specified parameters.
     *
     * @param id The ID of the folder to rename.
     * @param fileName The new name for the folder.
     * @param desc A new description for the folder.
     */
    void rename(Integer id, String fileName, String desc);

    /**
     * Get a list of all folders under a specified parent folder, up to a certain number of floors.
     *
     * @param pid The ID of the parent folder.
     * @param showFloorNum The maximum number of floors to show.
     * @return A list of {@link TreeNodeDTO} objects representing the folders under the specified parent folder.
     */
    List<TreeNodeDTO> showByTree(Integer pid, Integer showFloorNum);

    /**
     * Create a tree structure based on the specified data.
     *
     * @param data A list of {@link TreeNodeDTO} objects representing the nodes in the tree.
     * @param pid The ID of the parent folder.
     * @param showFloorNum The maximum number of floors to show.
     * @param currentFloor The current floor being processed.
     */
    void createTree(List<TreeNodeDTO> data, Integer pid, Integer showFloorNum, Integer currentFloor);

    /**
     * Get the content of a file with the specified resource ID.
     *
     * @param id The ID of the file to get the content for.
     * @return A string representing the content of the file.
     */
    String getContentByResourceId(Integer id);

    /**
     * Download files from explorer（从资源管理器下载文件）
     * @param id resource id
     * @return {@link File}
     */
    File getFile(Integer id);

    @Transactional(rollbackFor = Exception.class)
    void uploadFile(Integer pid, String desc, File file);

    /**
     * Upload a file to the specified folder.
     *
     * @param pid The ID of the parent folder.
     * @param desc A description of the file being uploaded.
     * @param file A {@link MultipartFile} object representing the file to be uploaded.
     */
    void uploadFile(Integer pid, String desc, MultipartFile file);

    boolean remove(Integer id);

    /**
     * 递归获取所有的资源，从pid到0
     *
     * @param resourcesList data
     * @param pid pid
     * @return data
     */
    @Deprecated
    List<Resources> getResourceByPidToParent(List<Resources> resourcesList, Integer pid);

    /**
     * 递归获取所有的资源，从id往下穿
     *
     * @param resourcesList data
     * @param pid pid
     * @return data
     */
    @Deprecated
    List<Resources> getResourceByPidToChildren(List<Resources> resourcesList, Integer pid);

    /**
     * query Resources tree data
     * @return {@link Result}< {@link List}< {@link Resources}>>}
     */
    List<Resources> getResourcesTree();

    /**
     * query Resources tree data by filter
     * @param filterFunction filter function
     * @return {@link Result}< {@link List}< {@link Resources}>>}
     */
    List<Resources> getResourcesTreeByFilter(Function<Resources, Boolean> filterFunction);
}
