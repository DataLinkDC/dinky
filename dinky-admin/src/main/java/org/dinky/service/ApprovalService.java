package org.dinky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;
import org.dinky.data.dto.ApprovalDTO;
import org.dinky.data.enums.ApprovalEvent;
import org.dinky.data.model.Approval;
import org.dinky.data.result.ProTableResult;

import java.util.List;

public interface ApprovalService extends IService<Approval> {

    /**
     * get all approvals submitted by current user
     * @return approval list submitted by current user
     */
    ProTableResult<Approval> getSubmittedApproval(JsonNode params);

    /**
     * get all approvals current user need to review
     * @return Approval list current user need to review
     */
    ProTableResult<Approval> getApprovalToBeReviewed(JsonNode params);

    /**
     * create a new approval for task
     * @param taskId task id linked to approval
     * @return if an approval for this task has an approval already in created status, return it, otherwise return a new one
     */
    Approval createTaskApproval(Integer taskId);

    /**
     * handle approval
     * @param event operation event
     * @param approvalDTO approval DTO
     */
    void handleApproveEvent(ApprovalEvent event, ApprovalDTO approvalDTO);

    /**
     * check if a task is approved
     * @param taskId task id
     * @return true if current task is approved
     */
    boolean isTaskApproved(Integer taskId);
}
