package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.JobHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * JobHistoryMapper
 *
 * @author wenmo
 * @since 2022/3/2 19:50
 **/
@Mapper
public interface JobHistoryMapper extends SuperMapper<JobHistory> {

    int insert(JobHistory jobHistory);

}
