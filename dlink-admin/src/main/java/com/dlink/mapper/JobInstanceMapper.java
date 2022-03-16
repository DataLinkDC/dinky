package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.JobInstance;
import com.dlink.model.JobInstanceCount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * JobInstanceMapper
 *
 * @author wenmo
 * @since 2022/2/2 13:02
 */
@Mapper
public interface JobInstanceMapper extends SuperMapper<JobInstance> {

    List<JobInstanceCount> countStatus();

    List<JobInstanceCount> countHistoryStatus();

    List<JobInstance> listJobInstanceActive();
}
