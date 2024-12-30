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

package org.dinky.service;

import org.dinky.data.dto.ApprovalDTO;
import org.dinky.data.enums.ApprovalEvent;
import org.dinky.data.model.Approval;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.ProTableResult;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;

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
     * check if a task need approve
     * @param taskId task id
     * @return true if current task need approve
     */
    boolean needApprove(Integer taskId);

    /**
     * get the reviewers of current tenant
     *
     * @param tenantId tenant Id
     * @return A list of {@link User} objects representing the users that can review current task
     */
    List<User> getTaskReviewerList(Integer tenantId);
}
