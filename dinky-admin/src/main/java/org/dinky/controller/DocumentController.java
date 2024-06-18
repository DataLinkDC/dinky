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
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.Document;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.DocumentService;

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

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** DocumentController */
@Slf4j
@RestController
@Api(tags = "Document Controller")
@RequestMapping("/api/document")
@RequiredArgsConstructor
@SaCheckLogin
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
    @Log(title = "Insert Or Update Document", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Insert Or Update Document")
    @ApiImplicitParam(
            name = "document",
            value = "Document",
            required = true,
            dataType = "Document",
            paramType = "body",
            dataTypeClass = Document.class)
    @SaCheckPermission(
            value = {PermissionConstants.REGISTRATION_DOCUMENT_ADD, PermissionConstants.REGISTRATION_DOCUMENT_EDIT},
            mode = SaMode.OR)
    public Result<Void> saveOrUpdateDocument(@RequestBody Document document) throws Exception {
        if (documentService.saveOrUpdate(document)) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * query documents
     *
     * @param para {@link JsonNode}
     * @return {@link ProTableResult} of {@link Document}
     */
    @PostMapping
    @ApiOperation("Document Query List")
    @ApiImplicitParam(
            name = "para",
            value = "Query Condition",
            required = true,
            dataType = "JsonNode",
            paramType = "body",
            dataTypeClass = JsonNode.class)
    public ProTableResult<Document> listDocuments(@RequestBody JsonNode para) {
        return documentService.selectForProTable(para);
    }

    /**
     * delete document by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @DeleteMapping("/delete")
    @Log(title = "Document Delete By id", businessType = BusinessType.DELETE)
    @ApiOperation("Document Delete By id")
    @ApiImplicitParam(
            name = "id",
            value = "Document Id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class,
            example = "1")
    @SaCheckPermission(PermissionConstants.REGISTRATION_DOCUMENT_DELETE)
    public Result<Void> deleteById(@RequestParam Integer id) {
        if (documentService.removeById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    /**
     * delete document by id
     *
     * @param id {@link Integer}
     * @return {@link Result} of {@link Void}
     */
    @PutMapping("/enable")
    @Log(title = "Update Document Status", businessType = BusinessType.UPDATE)
    @ApiOperation("Update Document Status")
    @ApiImplicitParam(
            name = "id",
            value = "Document Id",
            required = true,
            dataType = "Integer",
            paramType = "query",
            dataTypeClass = Integer.class,
            example = "1")
    @SaCheckPermission(PermissionConstants.REGISTRATION_DOCUMENT_EDIT)
    public Result<Void> modifyDocumentStatus(@RequestParam Integer id) {
        if (documentService.modifyDocumentStatus(id)) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        } else {
            return Result.failed(Status.MODIFY_FAILED);
        }
    }

    /**
     * get document by version
     *
     * @param version {@link String}
     * @return {@link Result} of {@link Document}
     * @throws {@link Exception}
     */
    @GetMapping("/getFillAllByVersion")
    @ApiOperation("Get Document By Version")
    @ApiImplicitParam(
            name = "version",
            value = "Document Version",
            required = true,
            dataType = "String",
            paramType = "query",
            dataTypeClass = String.class,
            example = "1.0.0")
    public Result<List<Document>> getFillAllByVersion(@RequestParam String version) {
        return Result.succeed(documentService.getFillAllByVersion(version));
    }
}
