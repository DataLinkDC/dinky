package com.dlink.service.impl;

import com.dlink.assertion.Assert;
import com.dlink.cluster.FlinkCluster;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.job.JobManager;
import com.dlink.model.Cluster;
import com.dlink.result.RunResult;
import com.dlink.service.ClusterService;
import com.dlink.service.StudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * StudioServiceImpl
 *
 * @author wenmo
 * @since 2021/5/30 11:08
 */
@Service
public class StudioServiceImpl implements StudioService {

    @Autowired
    private ClusterService clusterService;

    @Override
    public RunResult executeSql(StudioExecuteDTO studioExecuteDTO) {
        Cluster cluster = clusterService.getById(studioExecuteDTO.getClusterId());
        Assert.check(cluster);
        String host = FlinkCluster.testFlinkJobManagerIP(cluster.getHosts(), cluster.getJobManagerHost());
        Assert.checkHost(host);
        JobManager jobManager = new JobManager(host);
        return jobManager.execute(studioExecuteDTO.getStatement(), new ExecutorSetting(Executor.REMOTE));
    }
}
