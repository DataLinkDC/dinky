package com.dlink.service.impl;

import com.dlink.dto.APIExecuteSqlDTO;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.result.APIJobResult;
import com.dlink.service.APIService;
import com.dlink.utils.RunTimeUtil;
import org.springframework.stereotype.Service;

/**
 * APIServiceImpl
 *
 * @author wenmo
 * @since 2021/12/11 21:46
 */
@Service
public class APIServiceImpl implements APIService {

    @Override
    public APIJobResult executeSql(APIExecuteSqlDTO apiExecuteSqlDTO) {
        JobConfig config = apiExecuteSqlDTO.getJobConfig();
        JobManager jobManager = JobManager.build(config);
        JobResult jobResult = jobManager.executeSql(apiExecuteSqlDTO.getStatement());
        APIJobResult apiJobResult = APIJobResult.build(jobResult);
        RunTimeUtil.recovery(jobManager);
        return apiJobResult;
    }
}
