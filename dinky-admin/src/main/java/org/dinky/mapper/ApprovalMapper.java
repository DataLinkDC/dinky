package org.dinky.mapper;

import org.dinky.data.model.Approval;
import org.dinky.mybatis.mapper.SuperMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ApprovalMapper extends SuperMapper<Approval> {
    List<Approval> getApprovalByTaskId(Integer taskId);
}
