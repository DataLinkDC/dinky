package org.dinky.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dinky.data.annotations.CheckTaskOwner;
import org.dinky.data.annotations.TaskId;
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.ApprovalDTO;
import org.dinky.data.enums.ApprovalEvent;
import org.dinky.data.model.Approval;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.ApprovalService;
import org.dinky.service.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    Result<Approval> createTaskApproval(@RequestParam Integer taskId) {
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
    @SaCheckPermission(value = {PermissionConstants.AUTH_APPROVAL})
    Result<Void> approve(@RequestBody ApprovalDTO approvalDTO) {
        approvalService.handleApproveEvent(ApprovalEvent.APPROVE, approvalDTO);
        return Result.succeed();
    }

    @PostMapping("/reject")
    @ApiOperation("Reject approval")
    @SaCheckPermission(value = {PermissionConstants.AUTH_APPROVAL})
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

    @GetMapping("/needApprove")
    @ApiOperation("If task is approved and published")
    Result<Boolean> needApprove(@RequestParam Integer taskId) {
        return Result.succeed(approvalService.needApprove(taskId));
    }

    @GetMapping("/getReviewers")
    @ApiOperation("Get reviewers that from current tenant")
    Result<List<User>> getReviewers(@RequestParam Integer tenantId) {
        return Result.succeed(approvalService.getTaskReviewerList(tenantId));
    }
}
