package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.JobInstanceMapper;
import com.dlink.model.JobInstance;
import com.dlink.service.JobInstanceService;
import org.springframework.stereotype.Service;

/**
 * JobInstanceServiceImpl
 *
 * @author wenmo
 * @since 2022/2/2 13:52
 */
@Service
public class JobInstanceServiceImpl extends SuperServiceImpl<JobInstanceMapper, JobInstance> implements JobInstanceService {
}
