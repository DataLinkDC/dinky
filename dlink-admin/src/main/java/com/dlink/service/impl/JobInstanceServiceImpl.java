package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.db.util.ProTableUtil;
import com.dlink.explainer.lineage.LineageBuilder;
import com.dlink.explainer.lineage.LineageResult;
import com.dlink.job.FlinkJobTaskPool;
import com.dlink.mapper.JobInstanceMapper;
import com.dlink.model.*;
import com.dlink.service.*;
import com.dlink.utils.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * JobInstanceServiceImpl
 *
 * @author wenmo
 * @since 2022/2/2 13:52
 */
@Service
public class JobInstanceServiceImpl extends SuperServiceImpl<JobInstanceMapper, JobInstance> implements JobInstanceService {

    @Autowired
    private HistoryService historyService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ClusterConfigurationService clusterConfigurationService;
    @Autowired
    private JobHistoryService jobHistoryService;

    @Override
    public JobInstanceStatus getStatusCount(boolean isHistory) {
        List<JobInstanceCount> jobInstanceCounts = null;
        if (isHistory) {
            jobInstanceCounts = baseMapper.countHistoryStatus();
        } else {
            jobInstanceCounts = baseMapper.countStatus();
        }
        JobInstanceStatus jobInstanceStatus = new JobInstanceStatus();
        Integer total = 0;
        for (JobInstanceCount item : jobInstanceCounts) {
            Integer counts = Asserts.isNull(item.getCounts()) ? 0 : item.getCounts();
            total += counts;
            switch (JobStatus.get(item.getStatus())) {
                case INITIALIZING:
                    jobInstanceStatus.setInitializing(counts);
                    break;
                case RUNNING:
                    jobInstanceStatus.setRunning(counts);
                    break;
                case FINISHED:
                    jobInstanceStatus.setFinished(counts);
                    break;
                case FAILED:
                    jobInstanceStatus.setFailed(counts);
                    break;
                case CANCELED:
                    jobInstanceStatus.setCanceled(counts);
                    break;
                case RESTARTING:
                    jobInstanceStatus.setRestarting(counts);
                    break;
                case CREATED:
                    jobInstanceStatus.setCreated(counts);
                    break;
                case FAILING:
                    jobInstanceStatus.setFailed(counts);
                    break;
                case CANCELLING:
                    jobInstanceStatus.setCancelling(counts);
                    break;
                case SUSPENDED:
                    jobInstanceStatus.setSuspended(counts);
                    break;
                case RECONCILING:
                    jobInstanceStatus.setReconciling(counts);
                    break;
                case UNKNOWN:
                    jobInstanceStatus.setUnknown(counts);
            }
        }
        jobInstanceStatus.setAll(total);
        return jobInstanceStatus;
    }

    @Override
    public List<JobInstance> listJobInstanceActive() {
        return baseMapper.listJobInstanceActive();
    }

    @Override
    public JobInfoDetail getJobInfoDetail(Integer id) {
        return getJobInfoDetailInfo(getById(id));
    }

    @Override
    public JobInfoDetail getJobInfoDetailInfo(JobInstance jobInstance) {
        Asserts.checkNull(jobInstance, "该任务实例不存在");
        JobInfoDetail jobInfoDetail;
        FlinkJobTaskPool pool = FlinkJobTaskPool.getInstance();
        String key = jobInstance.getId().toString();
        if (pool.exist(key)) {
            jobInfoDetail = pool.get(key);
        } else {
            jobInfoDetail = new JobInfoDetail(jobInstance.getId());
            jobInfoDetail.setInstance(jobInstance);
            jobInfoDetail.setCluster(clusterService.getById(jobInstance.getClusterId()));
            jobInfoDetail.setJobHistory(jobHistoryService.getJobHistory(jobInstance.getId()));
            History history = historyService.getById(jobInstance.getHistoryId());
            history.setConfig(JSONUtil.parseObject(history.getConfigJson()));
            jobInfoDetail.setHistory(history);
            if (Asserts.isNotNull(history) && Asserts.isNotNull(history.getClusterConfigurationId())) {
                jobInfoDetail.setClusterConfiguration(clusterConfigurationService.getClusterConfigById(history.getClusterConfigurationId()));
            }
            JobManagerConfiguration jobManagerConfiguration = new JobManagerConfiguration();
            Set<TaskManagerConfiguration> taskManagerConfigurationList = new HashSet<>();
            if (Asserts.isNotNullString(history.getJobManagerAddress())) { // 如果有jobManager地址，则使用该地址
                FlinkAPI flinkAPI = FlinkAPI.build(history.getJobManagerAddress());

                // 获取jobManager的配置信息 开始
                buildJobManagerConfiguration(jobManagerConfiguration, flinkAPI);
                // 获取jobManager的配置信息 结束

                // 获取taskManager的配置信息 开始
                JsonNode taskManagerContainers = flinkAPI.getTaskManagers(); //获取taskManager列表
                buildTaskManagerConfiguration(taskManagerConfigurationList, flinkAPI, taskManagerContainers);
                // 获取taskManager的配置信息 结束

            }
            jobInfoDetail.setJobManagerConfiguration(jobManagerConfiguration);
            jobInfoDetail.setTaskManagerConfiguration(taskManagerConfigurationList);

        }
        return jobInfoDetail;
    }

    /**
     * @Author: zhumingye
     * @date: 2022/6/27
     * @Description: buildTaskManagerConfiguration
     * @Params: [taskManagerConfigurationList, flinkAPI, taskManagerContainers]
     * @return void
     */
    private void buildTaskManagerConfiguration(Set<TaskManagerConfiguration> taskManagerConfigurationList, FlinkAPI flinkAPI, JsonNode taskManagerContainers) {

        if (Asserts.isNotNull(taskManagerContainers)) {
            JsonNode taskmanagers = taskManagerContainers.get("taskmanagers");
            for (JsonNode taskManagers : taskmanagers) {
                TaskManagerConfiguration taskManagerConfiguration = new TaskManagerConfiguration();

                /**
                 * 解析 taskManager 的配置信息
                 */
                String containerId = taskManagers.get("id").asText();// 获取container id
                String containerPath =  taskManagers.get("path").asText(); // 获取container path
                Integer dataPort = taskManagers.get("dataPort").asInt(); // 获取container dataPort
                Integer jmxPort =taskManagers.get("jmxPort").asInt(); // 获取container jmxPort
                Long timeSinceLastHeartbeat =taskManagers.get("timeSinceLastHeartbeat").asLong(); // 获取container timeSinceLastHeartbeat
                Integer slotsNumber =taskManagers.get("slotsNumber").asInt(); // 获取container slotsNumber
                Integer freeSlots = taskManagers.get("freeSlots").asInt(); // 获取container freeSlots
                String totalResource =  JSONUtil.toJsonString(taskManagers.get("totalResource")); // 获取container totalResource
                String freeResource =  JSONUtil.toJsonString(taskManagers.get("freeResource") ); // 获取container freeResource
                String hardware = JSONUtil.toJsonString(taskManagers.get("hardware") ); // 获取container hardware
                String memoryConfiguration = JSONUtil.toJsonString(taskManagers.get("memoryConfiguration") ); // 获取container memoryConfiguration
                Asserts.checkNull(containerId, "获取不到 containerId , containerId不能为空");
                JsonNode taskManagerMetrics = flinkAPI.getTaskManagerMetrics(containerId);//获取taskManager metrics
                String taskManagerLog = flinkAPI.getTaskManagerLog(containerId);//获取taskManager日志
                String taskManagerThreadDumps = JSONUtil.toJsonString(flinkAPI.getTaskManagerThreadDump(containerId).get("threadInfos"));//获取taskManager线程dumps
                String taskManagerStdOut = flinkAPI.getTaskManagerStdOut(containerId);//获取taskManager标准输出日志

                Map<String, String> taskManagerMetricsMap = new HashMap<String, String>(); //获取taskManager metrics
                List<LinkedHashMap> taskManagerMetricsItemsList = JSONUtil.toList(JSONUtil.toJsonString(taskManagerMetrics), LinkedHashMap.class);
                taskManagerMetricsItemsList.forEach(mapItems -> {
                    String configKey = (String) mapItems.get("id");
                    String configValue = (String) mapItems.get("value");
                    if (Asserts.isNotNullString(configKey) && Asserts.isNotNullString(configValue)) {
                        taskManagerMetricsMap.put(configKey, configValue);
                    }
                });

                /**
                 * TaskManagerConfiguration 赋值
                 */
                taskManagerConfiguration.setContainerId(containerId);
                taskManagerConfiguration.setContainerPath(containerPath);
                taskManagerConfiguration.setDataPort(dataPort);
                taskManagerConfiguration.setJmxPort(jmxPort);
                taskManagerConfiguration.setTimeSinceLastHeartbeat(timeSinceLastHeartbeat);
                taskManagerConfiguration.setSlotsNumber(slotsNumber);
                taskManagerConfiguration.setFreeSlots(freeSlots);
                taskManagerConfiguration.setTotalResource(totalResource);
                taskManagerConfiguration.setFreeResource(freeResource);
                taskManagerConfiguration.setHardware(hardware);
                taskManagerConfiguration.setMemoryConfiguration(memoryConfiguration);

                /**
                 * TaskContainerConfigInfo 赋值
                 */
                TaskContainerConfigInfo taskContainerConfigInfo = new TaskContainerConfigInfo();
                taskContainerConfigInfo.setMetrics(taskManagerMetricsMap);
                taskContainerConfigInfo.setTaskManagerLog(taskManagerLog);
                taskContainerConfigInfo.setTaskManagerThreadDump(taskManagerThreadDumps);
                taskContainerConfigInfo.setTaskManagerStdout(taskManagerStdOut);


                taskManagerConfiguration.setTaskContainerConfigInfo(taskContainerConfigInfo);

                // 将taskManagerConfiguration添加到set集合中
                taskManagerConfigurationList.add(taskManagerConfiguration);
            }
        }
    }

    /**
     * @Author: zhumingye
     * @date: 2022/6/27
     * @Description: buildJobManagerConfiguration
     * @Params: [jobManagerConfiguration, flinkAPI]
     * @return void
     */
    private void buildJobManagerConfiguration(JobManagerConfiguration jobManagerConfiguration, FlinkAPI flinkAPI) {

        Map<String, String> jobManagerMetricsMap = new HashMap<String, String>(); //获取jobManager metrics
        List<LinkedHashMap> jobManagerMetricsItemsList = JSONUtil.toList(JSONUtil.toJsonString(flinkAPI.getJobManagerMetrics()), LinkedHashMap.class);
        jobManagerMetricsItemsList.forEach(mapItems -> {
            String configKey = (String) mapItems.get("id");
            String configValue = (String) mapItems.get("value");
            if (Asserts.isNotNullString(configKey) && Asserts.isNotNullString(configValue)) {
                jobManagerMetricsMap.put(configKey, configValue);
            }
        });
        Map<String, String> jobManagerConfigMap = new HashMap<String, String>();//获取jobManager配置信息
        List<LinkedHashMap> jobManagerConfigMapItemsList = JSONUtil.toList(JSONUtil.toJsonString(flinkAPI.getJobManagerConfig()), LinkedHashMap.class);
        jobManagerConfigMapItemsList.forEach(mapItems -> {
            String configKey = (String) mapItems.get("key");
            String configValue = (String) mapItems.get("value");
            if (Asserts.isNotNullString(configKey) && Asserts.isNotNullString(configValue)) {
                jobManagerConfigMap.put(configKey, configValue);
            }
        });
        String jobMangerLog = flinkAPI.getJobManagerLog(); //获取jobManager日志
        String jobManagerStdOut = flinkAPI.getJobManagerStdOut(); //获取jobManager标准输出日志

        jobManagerConfiguration.setMetrics(jobManagerMetricsMap);
        jobManagerConfiguration.setJobManagerConfig(jobManagerConfigMap);
        jobManagerConfiguration.setJobManagerLog(jobMangerLog);
        jobManagerConfiguration.setJobManagerStdout(jobManagerStdOut);
    }



    @Override
    public LineageResult getLineage(Integer id) {
        History history = getJobInfoDetail(id).getHistory();
        return LineageBuilder.getLineage(history.getStatement(), history.getConfig().get("useStatementSet").asBoolean());
    }

    @Override
    public JobInstance getJobInstanceByTaskId(Integer id) {
        return baseMapper.getJobInstanceByTaskId(id);
    }

    @Override
    public ProTableResult<JobInstance> listJobInstances(JsonNode para) {
        Integer current = para.has("current") ? para.get("current").asInt() : 1;
        Integer pageSize = para.has("pageSize") ? para.get("pageSize").asInt() : 10;
        QueryWrapper<JobInstance> queryWrapper = new QueryWrapper<>();
        ProTableUtil.autoQueryDefalut(para, queryWrapper);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(para, Map.class);
        Page<JobInstance> page = new Page<>(current, pageSize);
        List<JobInstance> list = baseMapper.selectForProTable(page, queryWrapper, param);
        FlinkJobTaskPool pool = FlinkJobTaskPool.getInstance();
        for (int i = 0; i < list.size(); i++) {
            if (pool.exist(list.get(i).getId().toString())) {
                list.get(i).setStatus(pool.get(list.get(i).getId().toString()).getInstance().getStatus());
                list.get(i).setUpdateTime(pool.get(list.get(i).getId().toString()).getInstance().getUpdateTime());
                list.get(i).setFinishTime(pool.get(list.get(i).getId().toString()).getInstance().getFinishTime());
                list.get(i).setError(pool.get(list.get(i).getId().toString()).getInstance().getError());
                list.get(i).setDuration(pool.get(list.get(i).getId().toString()).getInstance().getDuration());
            }
        }
        return ProTableResult.<JobInstance>builder().success(true).data(list).total(page.getTotal()).current(current).pageSize(pageSize).build();
    }

}
