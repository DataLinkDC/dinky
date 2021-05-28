package com.dlink.service.impl;

import com.dlink.cluster.FlinkCluster;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.exception.BusException;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.job.JobManager;
import com.dlink.mapper.TaskMapper;
import com.dlink.model.Cluster;
import com.dlink.model.Statement;
import com.dlink.model.Task;
import com.dlink.result.SubmitResult;
import com.dlink.service.ClusterService;
import com.dlink.service.StatementService;
import com.dlink.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务 服务实现类
 *
 * @author wenmo
 * @since 2021-05-24
 */
@Service
public class TaskServiceImpl extends SuperServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private StatementService statementService;
    @Autowired
    private ClusterService clusterService;

    @Override
    public SubmitResult submitByTaskId(Integer id) {
        Task task = this.getById(id);
        if (task == null) {
            throw new BusException("作业不存在");
        }
        Cluster cluster = clusterService.getById(task.getClusterId());
        if (cluster == null) {
            throw new BusException("Flink集群不存在");
        }
        Statement statement = statementService.getById(id);
        if (statement == null) {
            throw new BusException("FlinkSql语句不存在");
        }
        String host = FlinkCluster.testFlinkJobManagerIP(cluster.getHosts(), cluster.getJobManagerHost());
        if (host == null || "".equals(host)) {
            throw new BusException("集群地址暂不可用");
        }
        JobManager jobManager = new JobManager(host);
        return jobManager.submit(statement.getStatement(), task.getRemoteExecutorSetting());
    }

}
