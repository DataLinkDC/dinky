package com.dlink.gateway.yarn;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.AbstractGateway;
import com.dlink.gateway.ConfigPara;
import com.dlink.gateway.config.FlinkConfig;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.config.ActionType;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.gateway.result.YarnResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnLogConfigUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * YarnSubmiter
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public abstract class YarnGateway extends AbstractGateway {

    protected YarnConfiguration yarnConfiguration;
    protected YarnClient yarnClient;

    public YarnGateway() {
    }

    public YarnGateway(GatewayConfig config) {
        super(config);
    }

    public void init(){
        initConfig();
        initYarnClient();
    }

    private void initConfig(){
        configuration = GlobalConfiguration.loadConfiguration(config.getClusterConfig().getFlinkConfigPath());
        addConfigParas(config.getFlinkConfig().getConfigParas());
        configuration.set(DeploymentOptions.TARGET, getType().getLongValue());
        if(Asserts.isNotNullString(config.getFlinkConfig().getSavePoint())) {
            configuration.setString(SavepointConfigOptions.SAVEPOINT_PATH, config.getFlinkConfig().getSavePoint());
        }
        configuration.set(YarnConfigOptions.PROVIDED_LIB_DIRS, Collections.singletonList(config.getClusterConfig().getFlinkLibs()));
        configuration.set(YarnConfigOptions.APPLICATION_NAME, config.getFlinkConfig().getJobName());
        YarnLogConfigUtil.setLogConfigFileInConfig(configuration, config.getClusterConfig().getFlinkConfigPath());
    }

    private void initYarnClient(){
        yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.addResource( new Path( config.getClusterConfig().getYarnConfigPath() ) );
        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
    }

    private void addConfigParas(List<ConfigPara> configParas){
        if(Asserts.isNotNull(configParas)) {
            for (ConfigPara configPara : configParas) {
                configuration.setString(configPara.getKey(), configPara.getValue());
            }
        }
    }

    public GatewayResult savepointCluster(){
        if(Asserts.isNull(yarnClient)){
            init();
        }
        SavePointResult result = SavePointResult.build(getType());
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();
        ApplicationId applicationId = clusterClientFactory.getClusterId(configuration);
        if (applicationId == null){
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }
        YarnClusterDescriptor clusterDescriptor = clusterClientFactory
                .createClusterDescriptor(
                        configuration);

        try(ClusterClient<ApplicationId> clusterClient = clusterDescriptor.retrieve(
                applicationId).getClusterClient()){
            List<JobInfo> jobInfos = new ArrayList<>();
            CompletableFuture<Collection<JobStatusMessage>> listJobsFuture = clusterClient.listJobs();
            for( JobStatusMessage jobStatusMessage: listJobsFuture.get()){
                JobInfo jobInfo = new JobInfo(jobStatusMessage.getJobId().toHexString());
                jobInfo.setStatus(JobInfo.JobStatus.RUN);
                jobInfos.add(jobInfo);
            }
            runSavePointJob(jobInfos,clusterClient);
            result.setJobInfos(jobInfos);
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result.fail(e.getMessage());
        }
        return result;
    }

    public GatewayResult savepointJob(){
        if(Asserts.isNull(yarnClient)){
            init();
        }
        if(Asserts.isNull(config.getFlinkConfig().getJobId())){
            throw new GatewayException(
                    "No job id was specified. Please specify a job to which you would like to savepont.");
        }
        SavePointResult result = SavePointResult.build(getType());
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();
        ApplicationId applicationId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(applicationId)){
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }
        YarnClusterDescriptor clusterDescriptor = clusterClientFactory
                .createClusterDescriptor(
                        configuration);
        try(ClusterClient<ApplicationId> clusterClient = clusterDescriptor.retrieve(
                applicationId).getClusterClient()){
            List<JobInfo> jobInfos = new ArrayList<>();
            jobInfos.add(new JobInfo(config.getFlinkConfig().getJobId(),JobInfo.JobStatus.FAIL));
            runSavePointJob(jobInfos,clusterClient);
            result.setJobInfos(jobInfos);
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result.fail(e.getMessage());
        }
        return result;
    }

    private void runSavePointJob(List<JobInfo> jobInfos,ClusterClient<ApplicationId> clusterClient) throws Exception{
        String savePoint = FlinkConfig.DEFAULT_SAVEPOINT_PREFIX;
        if(Asserts.isNotNull(config.getTaskId())){
            savePoint = savePoint + config.getTaskId();
        }
        if(Asserts.isNotNullString(config.getFlinkConfig().getSavePoint())){
            savePoint = config.getFlinkConfig().getSavePoint();
        }
        for( JobInfo jobInfo: jobInfos){
            if(ActionType.CANCEL== config.getFlinkConfig().getAction()){
                clusterClient.cancel(JobID.fromHexString(jobInfo.getJobId()));
                jobInfo.setStatus(JobInfo.JobStatus.CANCEL);
                continue;
            }
            switch (config.getFlinkConfig().getSavePointType()){
                case TRIGGER:
                    CompletableFuture<String> triggerFuture = clusterClient.triggerSavepoint(JobID.fromHexString(jobInfo.getJobId()), savePoint);
                    jobInfo.setSavePoint(triggerFuture.get());
                    break;
                case DISPOSE:
                    clusterClient.disposeSavepoint(savePoint);
                    break;
                case STOP:
                    CompletableFuture<String> stopFuture = clusterClient.stopWithSavepoint(JobID.fromHexString(jobInfo.getJobId()), true, savePoint);
                    jobInfo.setStatus(JobInfo.JobStatus.STOP);
                    jobInfo.setSavePoint(stopFuture.get());
                    break;
                case CANCEL:
                    CompletableFuture<String> cancelFuture = clusterClient.cancelWithSavepoint(JobID.fromHexString(jobInfo.getJobId()), savePoint);
                    jobInfo.setStatus(JobInfo.JobStatus.CANCEL);
                    jobInfo.setSavePoint(cancelFuture.get());
                    break;
                default:
            }
        }
    }
}
