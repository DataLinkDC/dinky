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

import org.dinky.data.annotation.Log;
import org.dinky.data.dto.CatalogueTaskDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Catalogue;
import org.dinky.data.result.Result;
import org.dinky.function.constant.PathConstant;
import org.dinky.service.CatalogueService;

import java.io.File;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CatalogueController
 *
 * @since 2021/5/28 14:03
 */
@Slf4j
@RestController
@RequestMapping("/api/catalogue")
@RequiredArgsConstructor
public class CatalogueController {

    private final CatalogueService catalogueService;

    @PostMapping("/upload/{id}")
    @Log(title = "Upload Catalogue", businessType = BusinessType.UPLOAD)
    @ApiOperation("Upload Zip Package And Create Catalogue")
    public Result<String> upload(MultipartFile file, @PathVariable Integer id) {
        // 获取上传的路径
        String filePath = PathConstant.WORK_DIR;
        // 获取源文件的名称
        String fileName = file.getOriginalFilename();
        String zipPath = filePath + File.separator + fileName;
        String unzipFileName = FileUtil.mainName(fileName);
        String unzipPath = filePath + File.separator + unzipFileName;
        File unzipFile = new File(unzipPath);
        File zipFile = new File(zipPath);
        if (unzipFile.exists()) {
            FileUtil.del(zipFile);
            return Result.failed("工程已存在");
        }
        try {
            // 文件写入上传的路径
            FileUtil.writeBytes(file.getBytes(), zipPath);
            Thread.sleep(1L);
            if (!unzipFile.exists()) {
                ZipUtil.unzip(zipPath, filePath);
                Catalogue cata = catalogueService.getCatalogue(id, unzipFileName);
                catalogueService.traverseFile(unzipPath, cata);
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        } finally {
            FileUtil.del(zipFile);
        }
        return Result.succeed("上传zip包并创建工程成功");
    }

    /**
     * insert or update catalogue
     *
     * @param catalogue
     * @return Result<Void>
     */
    @PutMapping("saveOrUpdateCatalogue")
    @Log(title = "Insert Or Update Catalogue", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Insert Or Update Catalogue")
    public Result<Void> saveOrUpdateCatalogue(@RequestBody Catalogue catalogue) {
        if (catalogueService.saveOrUpdateOrRename(catalogue)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }


    /** 获取所有目录 */
    @PostMapping("/getCatalogueTreeData")
    @ApiOperation("Get Catalogue Tree Data")
    public Result<List<Catalogue>> getCatalogueTree() {
        List<Catalogue> catalogues = catalogueService.getCatalogueTree();
        return Result.succeed(catalogues);
    }

    /** 创建节点和作业 */
    @PutMapping("/createTask")
    @Log(title = "Create Catalogue And Task", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Create Catalogue And Task")
    public Result<Catalogue> createTask(@RequestBody CatalogueTaskDTO catalogueTaskDTO) {
        Catalogue catalogue = catalogueService.saveOrUpdateCatalogueAndTask(catalogueTaskDTO);
        if (catalogue.getId() != null) {
            return Result.succeed(catalogue, Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /** 移动 */
    @PutMapping("/moveCatalogue")
    @Log(title = "Move Catalogue", businessType = BusinessType.UPDATE)
    @ApiOperation("Move Catalogue")
    public Result<Boolean> moveCatalogue(@RequestBody Catalogue catalogue) {
        if (catalogueService.moveCatalogue(catalogue.getId(), catalogue.getParentId())) {
            return Result.succeed(true, Status.MOVE_SUCCESS);
        } else {
            return Result.failed(false, Status.MOVE_FAILED);
        }
    }

    @PostMapping("/copyTask")
    @Log(title = "Copy Task", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Copy Task")
    public Result<Catalogue> copyTask(@RequestBody Catalogue catalogue) {
        if (catalogueService.copyTask(catalogue)) {
            return Result.succeed(Status.COPY_SUCCESS);
        } else {
            return Result.failed(Status.COPY_FAILED);
        }
    }

    @DeleteMapping("deleteCatalogueById")
    @Log(title = "Delete Catalogue By Id", businessType = BusinessType.DELETE)
    @ApiOperation("Delete Catalogue By Id")
    public Result<Void> deleteCatalogueById(@RequestParam Integer id) {
        return catalogueService.deleteCatalogueById(id);
    }

}
