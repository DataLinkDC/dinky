package com.dlink.service.impl;

import com.dlink.assertion.Assert;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.mapper.TaskMapper;
import com.dlink.model.Cluster;
import com.dlink.model.Statement;
import com.dlink.model.Task;
import com.dlink.service.ClusterService;
import com.dlink.service.StatementService;
import com.dlink.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public JobResult submitByTaskId(Integer id) {
        Task task = this.getById(id);
        Assert.check(task);
        Statement statement = statementService.getById(id);
        Assert.check(statement);
        JobConfig config = task.buildSubmitConfig();
        config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(),task.getClusterId()));
        JobManager jobManager = JobManager.build(config);
        return jobManager.executeSql(statement.getStatement());
    }

    @Override
    public Task getTaskInfoById(Integer id) {
        Task task = this.getById(id);
        if (task != null) {
            Statement statement = statementService.getById(id);
            if(task.getClusterId()!=null) {
                Cluster cluster = clusterService.getById(task.getClusterId());
                if(cluster!=null){
                    task.setClusterName(cluster.getAlias());
                }
            }
            if (statement != null) {
                task.setStatement(statement.getStatement());
            }
        }
        return task;
    }

    @Override
    public boolean saveOrUpdateTask(Task task) {
        if (task.getId() != null) {
            if (StringUtils.isEmpty(task.getAlias())) {
                task.setAlias(task.getName());
            }
            this.updateById(task);
            if (task.getStatement() != null) {
                Statement statement = new Statement();
                statement.setId(task.getId());
                statement.setStatement(task.getStatement());
                statementService.updateById(statement);
            }
        } else {
            if (task.getCheckPoint() == null) {
                task.setCheckPoint(0);
            }
            if (task.getParallelism() == null) {
                task.setParallelism(1);
            }
            if (task.getClusterId() == null) {
                task.setClusterId(0);
            }
            if (StringUtils.isEmpty(task.getAlias())) {
                task.setAlias(task.getName());
            }
            this.save(task);

            Statement statement = new Statement();
            statement.setId(task.getId());
            if (task.getStatement() == null) {
                task.setStatement("");
            }
            statement.setStatement(task.getStatement());
            statementService.insert(statement);
        }
        return true;
    }

}
