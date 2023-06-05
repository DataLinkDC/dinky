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

import org.dinky.data.model.Document;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.DocumentService;
import org.dinky.utils.I18nMsgUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** DocumentController */
@Slf4j
@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * save or update
     *
     * @param document {@link Document}
     * @return {@link Result} of {@link Void}
     * @throws Exception {@link Exception}
     */
    @PutMapping
    public Result<Void> saveOrUpdate(@RequestBody Document document) throws Exception {
        if (documentService.saveOrUpdate(document)) {
            return Result.succeed(I18nMsgUtils.getMsg("save.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("save.failed"));
        }
    }

    /**
     * query documents
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} of {@link Document}
     */
    @PostMapping
    public ProTableResult<Document> listDocuments(@RequestBody JsonNode para) {
        return documentService.selectForProTable(para);
    }

    /**
     * batch delete, this method is deprecated, please use {@link #deleteMul(JsonNode)} instead.
     *
     * @param para
     * @return
     */
    @DeleteMapping
    @Deprecated
    public Result<Void> deleteMul(@RequestBody JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!documentService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * delete document by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteById(@RequestParam Integer id) {
        if (documentService.removeById(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("delete.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("delete.failed"));
        }
    }

    /**
     * delete document by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping("/enable")
    public Result<Void> enable(@RequestParam Integer id) {
        if (documentService.enable(id)) {
            return Result.succeed(I18nMsgUtils.getMsg("modify.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("modify.failed"));
        }
    }

    /**
     * get document by id
     *
     * @param document
     * @return {@link Result} of {@link Document}
     * @throws {@link Exception}
     */
    @PostMapping("/getOneById")
    public Result<Document> getOneById(@RequestBody Document document) throws Exception {
        document = documentService.getById(document.getId());
        return Result.succeed(document, I18nMsgUtils.getMsg("response.get.success"));
    }

    /**
     * get document by version
     *
     * @param version {@link String}
     * @return {@link Result} of {@link Document}
     * @throws {@link Exception}
     */
    @GetMapping("/getFillAllByVersion")
    public Result<List<Document>> getFillAllByVersion(@RequestParam String version) {
        return Result.succeed(
                documentService.getFillAllByVersion(version),
                I18nMsgUtils.getMsg("response.get.success"));
    }
}
