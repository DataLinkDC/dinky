package org.dinky.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.dinky.assertion.Asserts;
import org.dinky.data.dto.ApprovalDTO;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.enums.ApprovalEvent;
import org.dinky.data.enums.ApprovalStatus;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.Approval;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.rbac.Role;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.ProTableResult;
import org.dinky.mapper.ApprovalMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.ApprovalService;
import org.dinky.service.RoleService;
import org.dinky.service.TaskService;
import org.dinky.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl extends SuperServiceImpl<ApprovalMapper, Approval> implements ApprovalService {

    private final TaskService taskService;
    private final UserService userService;
    private final RoleService roleService;
    private Map<ApprovalEvent, Set<ApprovalStatus>> validPreStatusMap;
    private Map<ApprovalEvent, ApprovalStatus> operationResultMap;

    @Override
    public ProTableResult<Approval> getSubmittedApproval(JsonNode params) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("submitter", StpUtil.getLoginIdAsInt());
        return super.selectForProTable(params, paraMap);
    }

    @Override
    public ProTableResult<Approval> getApprovalToBeReviewed(JsonNode params) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("reviewer", StpUtil.getLoginIdAsInt());
        return super.selectForProTable(params, paraMap);
    }

    @Override
    public Approval createTaskApproval(Integer taskId) {
        // return existed created approval
        List<Approval> previousApproval = baseMapper.getApprovalByTaskId(taskId);
        if (Asserts.isNotNullCollection(previousApproval)) {
            for (Approval approval : previousApproval) {
                if (ApprovalStatus.CREATED.equalVal(approval.getStatus())) {
                    return approval;
                }
            }
        }

        // build previous version
        Integer previousTaskVersion = null;
        LocalDateTime latestApprovalTime = null;
        for (Approval approval : previousApproval) {
            if (ApprovalStatus.APPROVED.equalVal(approval.getStatus())) {
                if (latestApprovalTime == null || latestApprovalTime.isBefore(approval.getUpdateTime())) {
                    latestApprovalTime = approval.getUpdateTime();
                    previousTaskVersion = approval.getCurrentTaskVersion();
                }
            }
        }

        TaskDTO taskDTO = taskService.getTaskInfoById(taskId);
        Approval createdApproval = Approval.builder()
                .taskId(taskId)
                .submitter(StpUtil.getLoginIdAsInt())
                .status(ApprovalStatus.CREATED.getValue())
                .previousTaskVersion(previousTaskVersion)
                .currentTaskVersion(taskDTO.getVersionId())
                .build();
        baseMapper.insert(createdApproval);
        return createdApproval;
    }

    @Override
    public boolean needApprove(Integer taskId) {
        if (!SystemConfiguration.getInstances().enableTaskSubmitApprove()) {
            return false;
        }
        // only published task can be approved
        TaskDTO byId = taskService.getTaskInfoById(taskId);
        if (!JobLifeCycle.PUBLISH.equalsValue(byId.getStep())) {
            return true;
        }
        // check approval version
        List<Approval> approvalList = baseMapper.getApprovalByTaskId(taskId);
        for (Approval approval : approvalList) {
            if (ApprovalStatus.APPROVED.equals(ApprovalStatus.fromValue(approval.getStatus()))
                    && approval.getCurrentTaskVersion().equals(byId.getVersionId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<User> getTaskReviewerList(Integer tenantId) {
        // get reviewer roles
        Set<String> reviewerRoles = SystemConfiguration.getInstances().getReviewerRoles();
        List<Role> roles = roleService.list(new LambdaQueryWrapper<Role>().in(Role::getRoleCode, reviewerRoles).eq(Role::getTenantId, tenantId));
        // get users
        Map<Integer, User> userMap = new HashMap<>();
        for (Role role : roles) {
            List<User> userList = roleService.getUserListByRoleId(role.getId());
            for (User user : userList) {
                if (SystemConfiguration.getInstances().enforceCrossView() && user.getId().equals(StpUtil.getLoginIdAsInt())) {
                    continue;
                }
                userMap.put(user.getId(), user);
            }
        }
        return new ArrayList<>(userMap.values());
    }

    public void handleApproveEvent(ApprovalEvent event, ApprovalDTO approvalDTO) {
        Approval approval = baseMapper.selectById(approvalDTO.getId());
        // permission check
        if (!checkApprovalPermission(approval, event)) {
            throw new BusException("No operation permission!");
        }
        // only one approval in process check
        if (event.equals(ApprovalEvent.SUBMIT) && alreadyHaveOneInProcess(approval.getTaskId())) {
            throw new BusException("Already have a approval in process");
        }
        // status machine execute
        if (!validPreStatusMap.get(event).contains(ApprovalStatus.fromValue(approval.getStatus()))) {
            throw new BusException("Not a valid operation!");
        }
        approval.setStatus(operationResultMap.get(event).getValue());
        // handle other information
        updateApprovalFromDTOByEvent(approval, approvalDTO, event);
        baseMapper.updateById(approval);
    }

    /**
     * check approval operation permission
     *
     * @param approval approval
     * @param event    operation event
     * @return true if has permission
     */
    private boolean checkApprovalPermission(Approval approval, ApprovalEvent event) {
        // permission check
        switch (event) {
            case SUBMIT:
                return taskService.checkTaskOperatePermission(approval.getTaskId());
            case WITHDRAW:
            case CANCEL:
                return StpUtil.getLoginIdAsInt() == approval.getSubmitter();
            case APPROVE:
            case REJECT:
                List<Role> roleList = userService.getCurrentRole();
                Set<String> reviewerRoles = SystemConfiguration.getInstances().getReviewerRoles();
                int currentUserId = StpUtil.getLoginIdAsInt();
                for (Role role : roleList) {
                    if (reviewerRoles.contains(role.getRoleName())) {
                        return (currentUserId == approval.getReviewer()) && (!SystemConfiguration.getInstances().enforceCrossView() || currentUserId != approval.getSubmitter());
                    }
                }
            default:
                throw new BusException("No approval permission");
        }
    }

    /**
     * check if already have one approval in process
     *
     * @param taskId task id
     * @return true if an approval is in process
     */
    private boolean alreadyHaveOneInProcess(Integer taskId) {
        for (Approval approval : baseMapper.getApprovalByTaskId(taskId)) {
            if (ApprovalStatus.isInProcess(ApprovalStatus.valueOf(approval.getStatus()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * init a simple status machine to control approval status
     */
    @PostConstruct
    private void initSimpleStatusMachine() {
        // valid pre status
        validPreStatusMap = new HashMap<>();
        validPreStatusMap.put(ApprovalEvent.SUBMIT, new HashSet<>(Collections.singletonList(ApprovalStatus.CREATED)));
        validPreStatusMap.put(ApprovalEvent.CANCEL, new HashSet<>(Collections.singletonList(ApprovalStatus.CREATED)));
        validPreStatusMap.put(ApprovalEvent.WITHDRAW, new HashSet<>(Collections.singletonList(ApprovalStatus.SUBMITTED)));
        validPreStatusMap.put(ApprovalEvent.APPROVE, new HashSet<>(Collections.singletonList(ApprovalStatus.SUBMITTED)));
        validPreStatusMap.put(ApprovalEvent.REJECT, new HashSet<>(Collections.singletonList(ApprovalStatus.SUBMITTED)));
        //operation result
        operationResultMap = new HashMap<>();
        operationResultMap.put(ApprovalEvent.SUBMIT, ApprovalStatus.SUBMITTED);
        operationResultMap.put(ApprovalEvent.CANCEL, ApprovalStatus.CANCELED);
        operationResultMap.put(ApprovalEvent.WITHDRAW, ApprovalStatus.CREATED);
        operationResultMap.put(ApprovalEvent.APPROVE, ApprovalStatus.APPROVED);
        operationResultMap.put(ApprovalEvent.REJECT, ApprovalStatus.REJECTED);
    }

    /**
     * update necessary info of approval from DTO
     *
     * @param target target approval
     * @param source source DTO
     * @param event  approval event, different event need different info
     */
    private void updateApprovalFromDTOByEvent(Approval target, ApprovalDTO source, ApprovalEvent event) {
        switch (event) {
            case SUBMIT:
                target.setReviewer(source.getReviewer());
                target.setSubmitterComment(source.getComment());
                break;
            case APPROVE:
            case REJECT:
                target.setReviewerComment(source.getComment());
                break;
        }
    }
}
