package com.dlink.service.impl;

import com.dlink.assertion.Assert;
import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.gateway.GatewayType;
import com.dlink.job.JobConfig;
import com.dlink.job.JobManager;
import com.dlink.job.JobResult;
import com.dlink.mapper.TaskMapper;
import com.dlink.model.*;
import com.dlink.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

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
    @Autowired
    private ClusterConfigurationService clusterConfigurationService;
    @Autowired
    private SavepointsService savepointsService;
    @Autowired
    private JarService jarService;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    private String buildParas(Integer id) {
        return "--id " + id + " --driver " + driver + " --url " + url + " --username " + username + " --password " + password;
    }

    @Override
    public JobResult submitByTaskId(Integer id) {
        Task task = this.getById(id);
        Assert.check(task);
        boolean isJarTask = isJarTask(task);
        Statement statement = null;
        if(!isJarTask){
            statement = statementService.getById(id);
            Assert.check(statement);
        }
        JobConfig config = task.buildSubmitConfig();
        if (!JobManager.useGateway(config.getType())) {
            config.setAddress(clusterService.buildEnvironmentAddress(config.isUseRemote(), task.getClusterId()));
        } else {
            Map<String, Object> gatewayConfig = clusterConfigurationService.getGatewayConfig(task.getClusterConfigurationId());
            if (GatewayType.YARN_APPLICATION.equalsValue(config.getType())) {
                if(!isJarTask) {
                    SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
                    gatewayConfig.put("userJarPath", systemConfiguration.getSqlSubmitJarPath());
                    gatewayConfig.put("userJarParas", systemConfiguration.getSqlSubmitJarParas() + buildParas(config.getTaskId()));
                    gatewayConfig.put("userJarMainAppClass", systemConfiguration.getSqlSubmitJarMainAppClass());
                }else{
                    Jar jar = jarService.getById(task.getJarId());
                    Assert.check(jar);
                    gatewayConfig.put("userJarPath", jar.getPath());
                    gatewayConfig.put("userJarParas", jar.getParas());
                    gatewayConfig.put("userJarMainAppClass", jar.getMainClass());
                }
            }
            config.buildGatewayConfig(gatewayConfig);
        }
        switch (config.getSavePointStrategy()) {
            case LATEST:
                Savepoints latestSavepoints = savepointsService.getLatestSavepointByTaskId(id);
                if (Asserts.isNotNull(latestSavepoints)) {
                    config.setSavePointPath(latestSavepoints.getPath());
                }
                break;
            case EARLIEST:
                Savepoints earliestSavepoints = savepointsService.getEarliestSavepointByTaskId(id);
                if (Asserts.isNotNull(earliestSavepoints)) {
                    config.setSavePointPath(earliestSavepoints.getPath());
                }
                break;
            case CUSTOM:
                break;
            default:
                config.setSavePointPath(null);
        }
        JobManager jobManager = JobManager.build(config);
        if(!isJarTask) {
            return jobManager.executeSql(statement.getStatement());
        }else{
            return jobManager.executeJar();
        }
    }

    private boolean isJarTask(Task task){
        return GatewayType.YARN_APPLICATION.equalsValue(task.getType())&&Asserts.isNotNull(task.getJarId());
    }

    @Override
    public Task getTaskInfoById(Integer id) {
        Task task = this.getById(id);
        if (task != null) {
            Statement statement = statementService.getById(id);
            if (task.getClusterId() != null) {
                Cluster cluster = clusterService.getById(task.getClusterId());
                if (cluster != null) {
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
