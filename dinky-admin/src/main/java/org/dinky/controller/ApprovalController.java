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

import org.dinky.data.annotations.CheckTaskOwner;
import org.dinky.data.annotations.ProcessId;
import org.dinky.data.annotations.TaskId;
import org.dinky.data.dto.ApprovalDTO;
import org.dinky.data.enums.ApprovalEvent;
import org.dinky.data.model.Approval;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.ApprovalService;
import org.dinky.service.TaskService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(tags = "Approval Controller", hidden = true)
@RequestMapping("/api/approval")
@SaCheckLogin
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @PostMapping("/getSubmittedApproval")
    @ApiOperation("Get all approvals submitted by current user")
    ProTableResult<Approval> getSubmittedApproval(@RequestBody JsonNode para) {
        return approvalService.getSubmittedApproval(para);
    }

    @PostMapping("/getApprovalToBeReviewed")
    @ApiOperation("Get all approvals current user is required for review")
    ProTableResult<Approval> getApprovalToBeReviewed(@RequestBody JsonNode para) {
        return approvalService.getApprovalToBeReviewed(para);
    }

    @CheckTaskOwner(checkParam = TaskId.class, checkInterface = TaskService.class)
    @PutMapping("/createTaskApproval")
    Result<Approval> createTaskApproval(@TaskId @ProcessId @RequestParam Integer taskId) {
        return Result.succeed(approvalService.createTaskApproval(taskId));
    }

    @PostMapping("/submit")
    @ApiOperation("Submit approval")
    Result<Void> submit(@RequestBody ApprovalDTO approvalDTO) {
        approvalService.handleApproveEvent(ApprovalEvent.SUBMIT, approvalDTO);
        return Result.succeed();
    }

    @PostMapping("/withdraw")
    @ApiOperation("Withdraw approval")
    Result<Void> withdraw(@RequestBody ApprovalDTO approvalDTO) {
        approvalService.handleApproveEvent(ApprovalEvent.WITHDRAW, approvalDTO);
        return Result.succeed();
    }

    @PostMapping("/approve")
    @ApiOperation("Approve approval")
    Result<Void> approve(@RequestBody ApprovalDTO approvalDTO) {
        approvalService.handleApproveEvent(ApprovalEvent.APPROVE, approvalDTO);
        return Result.succeed();
    }

    @PostMapping("/reject")
    @ApiOperation("Reject approval")
    Result<Void> reject(@RequestBody ApprovalDTO approvalDTO) {
        approvalService.handleApproveEvent(ApprovalEvent.REJECT, approvalDTO);
        return Result.succeed();
    }

    @PostMapping("/cancel")
    @ApiOperation("Reject approval")
    Result<Void> cancel(@RequestBody ApprovalDTO approvalDTO) {
        approvalService.handleApproveEvent(ApprovalEvent.CANCEL, approvalDTO);
        return Result.succeed();
    }

    @GetMapping("/getReviewers")
    @ApiOperation("Get reviewers that from current tenant")
    Result<List<User>> getReviewers(@RequestParam Integer tenantId) {
        return Result.succeed(approvalService.getTaskReviewerList(tenantId));
    }
}
