package org.dinky.mapper;

import org.dinky.data.model.Approval;
import org.dinky.mybatis.mapper.SuperMapper;

import java.util.List;

public interface ApprovalMapper extends SuperMapper<Approval> {
    List<Approval> getApprovalByTaskId(Integer taskId);
}
