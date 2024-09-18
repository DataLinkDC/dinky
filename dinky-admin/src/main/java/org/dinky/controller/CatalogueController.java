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

import org.dinky.data.annotations.CatalogueId;
import org.dinky.data.annotations.CheckTaskOwner;
import org.dinky.data.annotations.Log;
import org.dinky.data.annotations.TaskId;
import org.dinky.data.constant.DirConstant;
import org.dinky.data.dto.CatalogueTaskDTO;
import org.dinky.data.dto.CatalogueTreeQueryDTO;
import org.dinky.data.dto.ImportCatalogueDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Catalogue;
import org.dinky.data.result.Result;
import org.dinky.data.vo.ExportCatalogueVO;
import org.dinky.data.vo.TreeVo;
import org.dinky.service.TaskService;
import org.dinky.service.catalogue.CatalogueService;

import java.io.File;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@Api(tags = "Catalogue Controller")
@RequestMapping("/api/catalogue")
@RequiredArgsConstructor
@SaCheckLogin
public class CatalogueController {

    private final CatalogueService catalogueService;

    @PostMapping("/upload/{id}")
    @Log(title = "Upload Catalogue", businessType = BusinessType.UPLOAD)
    @ApiOperation("Upload Zip Package And Create Catalogue")
    public Result<String> upload(MultipartFile file, @PathVariable Integer id) {
        // 获取上传的路径
        String filePath = DirConstant.getRootPath();
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
    @ApiImplicitParam(
            name = "catalogue",
            value = "catalogue",
            required = true,
            dataType = "Catalogue",
            dataTypeClass = Catalogue.class)
    public Result<Void> saveOrUpdateCatalogue(@RequestBody Catalogue catalogue) {
        if (catalogueService.saveOrUpdateOrRename(catalogue)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * query catalogue tree data
     *
     * @param catalogueTreeQueryDto {@link CatalogueTreeQueryDTO}
     * @return {@link Result}< {@link List}< {@link Catalogue}>>}
     */
    @PostMapping("/getCatalogueTreeData")
    @ApiOperation("Get Catalogue Tree Data")
    public Result<List<Catalogue>> getCatalogueTree(@RequestBody CatalogueTreeQueryDTO catalogueTreeQueryDto) {
        List<Catalogue> catalogues = catalogueService.getCatalogueTree(catalogueTreeQueryDto);
        return Result.succeed(catalogues);
    }

    /**
     * query catalogue sort type
     * @return {@link Result}< {@link List}< {@link TreeVo}>>}
     */
    @PostMapping("/getCatalogueSortType")
    @ApiOperation("Get Catalogue Sort Type")
    public Result<List<TreeVo>> getCatalogueSortType() {
        List<TreeVo> catalogueSortType = catalogueService.getCatalogueSortType();
        return Result.succeed(catalogueSortType);
    }

    /**
     * create catalogue and task
     * @param catalogueTaskDTO {@link CatalogueTaskDTO}
     * @return {@link Result}< {@link Catalogue}>}
     */
    @PutMapping("/saveOrUpdateCatalogueAndTask")
    @Log(title = "Create Catalogue And Task", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Create Catalogue And Task")
    @ApiImplicitParam(
            name = "catalogueTaskDTO",
            value = "catalogueTaskDTO",
            required = true,
            dataType = "CatalogueTaskDTO",
            dataTypeClass = CatalogueTaskDTO.class)
    @CheckTaskOwner(checkParam = TaskId.class, checkInterface = TaskService.class)
    public Result<Catalogue> createTask(@RequestBody CatalogueTaskDTO catalogueTaskDTO) {
        if (catalogueService.checkCatalogueTaskNameIsExistById(catalogueTaskDTO.getName(), catalogueTaskDTO.getId())) {
            return Result.failed(Status.TASK_IS_EXIST);
        }
        Catalogue catalogue = catalogueService.saveOrUpdateCatalogueAndTask(catalogueTaskDTO);
        if (catalogue.getId() != null) {
            return Result.succeed(catalogue, Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     *  move catalogue
     * @param originCatalogueId origin catalogue id
     * @param targetParentId target parent id
     * @return  {@link Result}< {@link Boolean}>}
     */
    @PutMapping("/moveCatalogue")
    @Log(title = "Move Catalogue", businessType = BusinessType.UPDATE)
    @ApiOperation("Move Catalogue")
    @ApiImplicitParams({
        @ApiImplicitParam(
                name = "originCatalogueId",
                value = "originCatalogueId",
                required = true,
                dataType = "Integer",
                dataTypeClass = Integer.class),
        @ApiImplicitParam(
                name = "targetParentId",
                value = "targetParentId",
                required = true,
                dataType = "Integer",
                dataTypeClass = Integer.class)
    })
    @CheckTaskOwner(checkParam = CatalogueId.class, checkInterface = CatalogueService.class)
    public Result<Boolean> moveCatalogue(
            @CatalogueId @RequestParam("originCatalogueId") Integer originCatalogueId,
            @RequestParam("targetParentId") Integer targetParentId) {
        if (catalogueService.moveCatalogue(originCatalogueId, targetParentId)) {
            return Result.succeed(true, Status.MOVE_SUCCESS);
        } else {
            return Result.failed(false, Status.MOVE_FAILED);
        }
    }

    /**
     * copy task
     * @param catalogue {@link Catalogue}
     * @return {@link Result}< {@link Catalogue}>}
     */
    @PostMapping("/copyTask")
    @Log(title = "Copy Task", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiImplicitParam(
            name = "catalogue",
            value = "catalogue",
            required = true,
            dataType = "Catalogue",
            dataTypeClass = Catalogue.class)
    @ApiOperation("Copy Task")
    @CheckTaskOwner(checkParam = TaskId.class, checkInterface = TaskService.class)
    public Result<Void> copyTask(@RequestBody Catalogue catalogue) {
        if (catalogueService.copyTask(catalogue)) {
            return Result.succeed(Status.COPY_SUCCESS);
        } else {
            return Result.failed(Status.COPY_FAILED);
        }
    }

    /**
     * delete catalogue by id
     * @param id catalogue id
     * @return {@link Result}< {@link Void}>}
     */
    @DeleteMapping("deleteCatalogueById")
    @Log(title = "Delete Catalogue By Id", businessType = BusinessType.DELETE)
    @ApiOperation("Delete Catalogue By Id")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", dataTypeClass = Integer.class)
    @CheckTaskOwner(checkParam = CatalogueId.class, checkInterface = CatalogueService.class)
    public Result<Void> deleteCatalogueById(@CatalogueId @RequestParam Integer id) {
        return catalogueService.deleteCatalogueById(id);
    }

    /**
     * export catalogue by id
     *
     * @param id catalogue id
     * @return {@link ResponseEntity}
     */
    @GetMapping("/export")
    @Log(title = "Export Catalogue", businessType = BusinessType.EXPORT)
    @ApiOperation("Export Catalogue")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", dataTypeClass = Integer.class)
    public ResponseEntity<?> exportCatalogue(@RequestParam Integer id) {
        ExportCatalogueVO exportCatalogueVo = catalogueService.exportCatalogue(id);
        // convert the return value to file at the interface level
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + exportCatalogueVo.getFileName());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return ResponseEntity.ok().headers(headers).body(exportCatalogueVo.getDataJson());
    }

    /**
     * import catalogue by parent id
     *
     * @return {@link Result}< {@link Void}>}
     */
    @PostMapping("/import")
    @Log(title = "Import Catalogue", businessType = BusinessType.IMPORT)
    @ApiOperation("Import Catalogue")
    public Result<Void> importCatalogue(MultipartHttpServletRequest request) {
        // assemble dto objects and shield service requests
        ImportCatalogueDTO importCatalogueDto = ImportCatalogueDTO.build(request);
        catalogueService.importCatalogue(importCatalogueDto);
        return Result.succeed();
    }
}
