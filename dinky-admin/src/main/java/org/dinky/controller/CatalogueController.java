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

import org.dinky.data.dto.CatalogueTaskDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Catalogue;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.function.constant.PathConstant;
import org.dinky.service.CatalogueService;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
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
                Catalogue cata = getCatalogue(id, unzipFileName);
                traverseFile(unzipPath, cata);
            }
        } catch (Exception e) {
            return Result.failed(e.getMessage());
        } finally {
            FileUtil.del(zipFile);
        }
        return Result.succeed("上传zip包并创建工程成功");
    }

    private void traverseFile(String sourcePath, Catalogue catalog) {
        File file = new File(sourcePath);
        File[] fs = file.listFiles();
        if (fs == null) {
            throw new RuntimeException("目录层级有误");
        }
        for (File fl : fs) {
            if (fl.isFile()) {
                CatalogueTaskDTO dto =
                        getCatalogueTaskDTO(
                                fl.getName(),
                                catalogueService
                                        .findByParentIdAndName(
                                                catalog.getParentId(), catalog.getName())
                                        .getId());
                String fileText = getFileText(fl);
                catalogueService.createCatalogAndFileTask(dto, fileText);
            } else {
                Catalogue newCata =
                        getCatalogue(
                                catalogueService
                                        .findByParentIdAndName(
                                                catalog.getParentId(), catalog.getName())
                                        .getId(),
                                fl.getName());
                traverseFile(fl.getPath(), newCata);
            }
        }
    }

    private String getFileText(File sourceFile) {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader isr =
                        new InputStreamReader(Files.newInputStream(sourceFile.toPath()));
                BufferedReader br = new BufferedReader(isr)) {
            if (sourceFile.isFile() && sourceFile.exists()) {

                String lineText;
                while ((lineText = br.readLine()) != null) {
                    sb.append(lineText).append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private Catalogue getCatalogue(Integer parentId, String name) {
        Catalogue subcata = new Catalogue();
        subcata.setTaskId(null);
        subcata.setName(name);
        subcata.setType("null");
        subcata.setParentId(parentId);
        subcata.setIsLeaf(false);
        catalogueService.saveOrUpdate(subcata);
        return subcata;
    }

    private CatalogueTaskDTO getCatalogueTaskDTO(String name, Integer parentId) {
        CatalogueTaskDTO catalogueTaskDTO = new CatalogueTaskDTO();
        catalogueTaskDTO.setName(UUID.randomUUID().toString().substring(0, 6) + name);
        catalogueTaskDTO.setId(null);
        catalogueTaskDTO.setParentId(parentId);
        catalogueTaskDTO.setLeaf(true);
        return catalogueTaskDTO;
    }

    /** 新增或者更新 */
    @PutMapping
    public Result<Void> saveOrUpdate(@RequestBody Catalogue catalogue) throws Exception {
        if (catalogueService.saveOrUpdate(catalogue)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /** 动态查询列表 */
    @PostMapping
    public ProTableResult<Catalogue> listCatalogues(@RequestBody JsonNode para) {
        return catalogueService.selectForProTable(para);
    }

    /** 批量删除 */
    @DeleteMapping
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            boolean isAdmin = false;
            List<String> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                List<String> ids = catalogueService.removeCatalogueAndTaskById(id);
                if (!ids.isEmpty()) {
                    error.addAll(ids);
                }
            }
            if (error.size() == 0 && !isAdmin) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除失败，请检查作业" + error + "状态。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /** 获取指定ID的信息 */
    @PostMapping("/getOneById")
    public Result<Catalogue> getOneById(@RequestBody Catalogue catalogue) throws Exception {
        catalogue = catalogueService.getById(catalogue.getId());
        return Result.succeed(catalogue);
    }

    /** 获取所有目录 */
    @PostMapping("/getCatalogueTreeData")
    public Result<List<Catalogue>> getCatalogueTreeData() {
        List<Catalogue> catalogues = catalogueService.getAllData();
        return Result.succeed(catalogues);
    }

    /** 创建节点和作业 */
    @PutMapping("/createTask")
    public Result<Catalogue> createTask(@RequestBody CatalogueTaskDTO catalogueTaskDTO) {
        Catalogue catalogue = catalogueService.saveOrUpdateCatalogueAndTask(catalogueTaskDTO);
        if (catalogue.getId() != null) {
            return Result.succeed(catalogue, Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /** 重命名节点和作业 */
    @PutMapping("/toRename")
    public Result<Void> toRename(@RequestBody Catalogue catalogue) {
        if (catalogueService.toRename(catalogue)) {
            return Result.succeed(Status.RENAME_SUCCESS);
        } else {
            return Result.failed(Status.RENAME_FAILED);
        }
    }

    /** 重命名节点和作业 */
    @PutMapping("/moveCatalogue")
    public Result<Boolean> moveCatalogue(@RequestBody Catalogue catalogue) {
        if (catalogueService.moveCatalogue(catalogue.getId(), catalogue.getParentId())) {
            return Result.succeed(true, Status.MOVE_SUCCESS);
        } else {
            return Result.failed(false, Status.MOVE_FAILED);
        }
    }

    @PostMapping("/copyTask")
    public Result<Catalogue> copyTask(@RequestBody Catalogue catalogue) {
        if (catalogueService.copyTask(catalogue)) {
            return Result.succeed(Status.COPY_SUCCESS);
        } else {
            return Result.failed(Status.COPY_FAILED);
        }
    }
}
