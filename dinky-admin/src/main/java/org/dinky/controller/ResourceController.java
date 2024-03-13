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

package org.dinky.controller;

import org.dinky.data.annotations.Log;
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.ResourcesDTO;
import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Resources;
import org.dinky.data.result.Result;
import org.dinky.service.resource.ResourcesService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "Resource Controller")
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourcesService resourcesService;

    @GetMapping("/syncRemoteDirectory")
    @ApiOperation("Sync Remote Directory Structure")
    @Log(title = "Sync Remote Directory Structure", businessType = BusinessType.INSERT)
    @SaCheckPermission(PermissionConstants.REGISTRATION_RESOURCE_UPLOAD)
    public Result<TreeNodeDTO> syncRemoteDirectoryStructure() {
        if (resourcesService.syncRemoteDirectoryStructure()) {
            return Result.succeed(Status.SUCCESS);
        }
        return Result.failed(Status.FAILED);
    }

    @PostMapping("/createFolder")
    @ApiOperation("Create Folder")
    @Log(title = "Create Folder", businessType = BusinessType.INSERT)
    @ApiImplicitParam(
            name = "resourcesDTO",
            value = "ResourcesDTO",
            required = true,
            dataType = "ResourcesDTO",
            paramType = "body",
            dataTypeClass = ResourcesDTO.class)
    @SaCheckPermission(PermissionConstants.REGISTRATION_RESOURCE_ADD_FOLDER)
    public Result<TreeNodeDTO> createFolder(@RequestBody ResourcesDTO resourcesDTO) {
        return Result.succeed(resourcesService.createFolder(
                resourcesDTO.getId(), resourcesDTO.getFileName(), resourcesDTO.getDescription()));
    }

    @PostMapping("/rename")
    @ApiOperation("Rename Folder/File")
    @Log(title = "Rename Folder/File", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(
            name = "resourcesDTO",
            value = "ResourcesDTO",
            required = true,
            dataType = "ResourcesDTO",
            paramType = "body",
            dataTypeClass = ResourcesDTO.class)
    @SaCheckPermission(PermissionConstants.REGISTRATION_RESOURCE_RENAME)
    public Result<Void> rename(@RequestBody ResourcesDTO resourcesDTO) {
        resourcesService.rename(resourcesDTO.getId(), resourcesDTO.getFileName(), resourcesDTO.getDescription());
        return Result.succeed();
    }

    /**
     * query Resources tree data
     * @return {@link Result}< {@link List}< {@link Resources}>>}
     */
    @GetMapping("/getResourcesTreeData")
    @ApiOperation("Get Resources Tree Data")
    public Result<List<Resources>> getResourcesTree() {
        List<Resources> resources = resourcesService.getResourcesTree();
        return Result.succeed(resources);
    }

    @GetMapping("/getContentByResourceId")
    @ApiOperation("Query Resource Content")
    @ApiImplicitParam(name = "id", value = "Resource ID", required = true, dataType = "Integer", paramType = "query")
    public Result<String> getContentByResourceId(@RequestParam Integer id) {
        return Result.data(resourcesService.getContentByResourceId(id));
    }

    @PostMapping("/uploadFile")
    @ApiOperation("Upload File")
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "pid",
                value = "Parent ID",
                required = true,
                dataType = "Integer",
                paramType = "query"),
        @ApiImplicitParam(
                name = "desc",
                value = "Description",
                required = true,
                dataType = "String",
                paramType = "query"),
        @ApiImplicitParam(
                name = "file",
                value = "File",
                required = true,
                dataType = "MultipartFile",
                paramType = "query")
    })
    @Log(title = "Upload File To Resource", businessType = BusinessType.UPLOAD)
    @SaCheckPermission(PermissionConstants.REGISTRATION_RESOURCE_UPLOAD)
    public Result<Void> uploadFile(Integer pid, String desc, @RequestParam("file") MultipartFile file) {
        resourcesService.uploadFile(pid, desc, file);
        return Result.succeed();
    }

    @DeleteMapping("/remove")
    @ApiOperation("Remove Folder/File")
    @Log(title = "Remove Folder/File", businessType = BusinessType.DELETE)
    @ApiImplicitParam(name = "id", value = "Resource ID", required = true, dataType = "Integer", paramType = "query")
    @SaCheckPermission(PermissionConstants.REGISTRATION_RESOURCE_DELETE)
    public Result<Void> remove(Integer id) {
        return resourcesService.remove(id)
                ? Result.succeed(Status.DELETE_SUCCESS)
                : Result.failed(Status.DELETE_FAILED);
    }
}
