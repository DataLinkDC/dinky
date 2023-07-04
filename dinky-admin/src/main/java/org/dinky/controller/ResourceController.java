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

import org.dinky.data.dto.ResourcesDTO;
import org.dinky.data.dto.TreeNodeDTO;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourcesService resourcesService;

    @PostMapping("/createFolder")
    public Result<TreeNodeDTO> createFolder(@RequestBody ResourcesDTO resourcesDTO) {
        return Result.succeed(
                resourcesService.createFolder(
                        resourcesDTO.getId(),
                        resourcesDTO.getFileName(),
                        resourcesDTO.getDescription()));
    }

    @PostMapping("/rename")
    public Result<Void> rename(@RequestBody ResourcesDTO resourcesDTO) {
        resourcesService.rename(
                resourcesDTO.getId(), resourcesDTO.getFileName(), resourcesDTO.getDescription());
        return Result.succeed();
    }

    @GetMapping("/showByTree")
    public Result<List<TreeNodeDTO>> showByTree(Integer pid, Integer showFloorNum) {
        return Result.succeed(resourcesService.showByTree(pid, showFloorNum));
    }

    @GetMapping("/getContentByResourceId")
    public Result<String> getContentByResourceId(@RequestParam Integer id) {
        return Result.data(resourcesService.getContentByResourceId(id));
    }

    @PostMapping("/uploadFile")
    public Result<Void> uploadFile(
            Integer pid, String desc, @RequestParam("file") MultipartFile file) {
        resourcesService.uploadFile(pid, desc, file);
        return Result.succeed();
    }

    @DeleteMapping("/remove")
    public Result<Void> remove(Integer id) {
        resourcesService.remove(id);
        return Result.succeed();
    }
}
