package com.dlink.gateway.yarn;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.AbstractGateway;
import com.dlink.gateway.config.ActionType;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.gateway.result.TestResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.SecurityOptions;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.security.SecurityConfiguration;
import org.apache.flink.runtime.security.SecurityUtils;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnLogConfigUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.net.URI;
import java.util.*;
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
        if(Asserts.isNotNull(config.getFlinkConfig().getConfiguration())) {
            addConfigParas(config.getFlinkConfig().getConfiguration());
        }
        configuration.set(DeploymentOptions.TARGET, getType().getLongValue());
        if(Asserts.isNotNullString(config.getFlinkConfig().getSavePoint())) {
            configuration.setString(SavepointConfigOptions.SAVEPOINT_PATH, config.getFlinkConfig().getSavePoint());
        }
        configuration.set(YarnConfigOptions.PROVIDED_LIB_DIRS, Collections.singletonList(config.getClusterConfig().getFlinkLibPath()));
        if(Asserts.isNotNullString(config.getFlinkConfig().getJobName())) {
            configuration.set(YarnConfigOptions.APPLICATION_NAME, config.getFlinkConfig().getJobName());
        }

        if(configuration.containsKey(SecurityOptions.KERBEROS_LOGIN_KEYTAB.key())) {
            try {
                SecurityUtils.install(new SecurityConfiguration(configuration));
                UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
                logger.info("安全认证结束，用户和认证方式:" + currentUser.toString());
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }

        YarnLogConfigUtil.setLogConfigFileInConfig(configuration, config.getClusterConfig().getFlinkConfigPath());
    }

    private void initYarnClient(){
        yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.addResource( new Path( URI.create(config.getClusterConfig().getYarnConfigPath()+"/yarn-site.xml") ) );
        yarnConfiguration.addResource( new Path( URI.create(config.getClusterConfig().getYarnConfigPath()+"/core-site.xml") ) );
        yarnConfiguration.addResource( new Path( URI.create(config.getClusterConfig().getYarnConfigPath()+"/hdfs-site.xml") ) );
        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
    }

    private void addConfigParas(Map<String, String> configMap){
        if(Asserts.isNotNull(configMap)) {
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                this.configuration.setString(entry.getKey(), entry.getValue());
            }
        }
    }

    public SavePointResult savepointCluster(){
        return savepointCluster(null);
    }

    public SavePointResult savepointCluster(String savePoint){
        if(Asserts.isNull(yarnClient)){
            init();
        }
        /*if(Asserts.isNotNullString(config.getClusterConfig().getYarnConfigPath())) {
            configuration = GlobalConfiguration.loadConfiguration(config.getClusterConfig().getYarnConfigPath());
        }else {
            configuration = new Configuration();
        }*/
        SavePointResult result = SavePointResult.build(getType());
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();
        configuration.set(YarnConfigOptions.APPLICATION_ID, config.getClusterConfig().getAppId());
        ApplicationId applicationId = clusterClientFactory.getClusterId(configuration);
        if (applicationId == null){
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }
        /*YarnClusterDescriptor clusterDescriptor = clusterClientFactory
                .createClusterDescriptor(
                        configuration);*/
        YarnClusterDescriptor clusterDescriptor = new YarnClusterDescriptor(
                configuration, yarnConfiguration, yarnClient, YarnClientYarnClusterInformationRetriever.create(yarnClient), true);
        try(ClusterClient<ApplicationId> clusterClient = clusterDescriptor.retrieve(
                applicationId).getClusterClient()){
            List<JobInfo> jobInfos = new ArrayList<>();
            CompletableFuture<Collection<JobStatusMessage>> listJobsFuture = clusterClient.listJobs();
            for( JobStatusMessage jobStatusMessage: listJobsFuture.get()){
                JobInfo jobInfo = new JobInfo(jobStatusMessage.getJobId().toHexString());
                jobInfo.setStatus(JobInfo.JobStatus.RUN);
                jobInfos.add(jobInfo);
            }
            runSavePointJob(jobInfos,clusterClient,savePoint);
            result.setJobInfos(jobInfos);
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result.fail(e.getMessage());
        }
        return result;
    }

    public SavePointResult savepointJob(){
        return savepointJob(null);
    }

    public SavePointResult savepointJob(String savePoint){
        if(Asserts.isNull(yarnClient)){
            init();
        }
        if(Asserts.isNull(config.getFlinkConfig().getJobId())){
            throw new GatewayException(
                    "No job id was specified. Please specify a job to which you would like to savepont.");
        }
        /*if(Asserts.isNotNullString(config.getClusterConfig().getYarnConfigPath())) {
            configuration = GlobalConfiguration.loadConfiguration(config.getClusterConfig().getYarnConfigPath());
        }else {
            configuration = new Configuration();
        }*/
        SavePointResult result = SavePointResult.build(getType());
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();
        configuration.set(YarnConfigOptions.APPLICATION_ID, config.getClusterConfig().getAppId());
        ApplicationId applicationId = clusterClientFactory.getClusterId(configuration);
        if (Asserts.isNull(applicationId)){
            throw new GatewayException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }
        /*YarnClusterDescriptor clusterDescriptor = clusterClientFactory
                .createClusterDescriptor(
                        configuration);*/
        YarnClusterDescriptor clusterDescriptor = new YarnClusterDescriptor(
                configuration, yarnConfiguration, yarnClient, YarnClientYarnClusterInformationRetriever.create(yarnClient), true);
        try(ClusterClient<ApplicationId> clusterClient = clusterDescriptor.retrieve(
                applicationId).getClusterClient()){
            List<JobInfo> jobInfos = new ArrayList<>();
            jobInfos.add(new JobInfo(config.getFlinkConfig().getJobId(),JobInfo.JobStatus.FAIL));
            runSavePointJob(jobInfos,clusterClient,savePoint);
            result.setJobInfos(jobInfos);
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result.fail(e.getMessage());
        }
        return result;
    }

    private void runSavePointJob(List<JobInfo> jobInfos,ClusterClient<ApplicationId> clusterClient,String savePoint) throws Exception{
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

    public TestResult test(){
        try {
            initConfig();
        }catch (Exception e){
            logger.error("测试 Flink 配置失败："+e.getMessage());
            return TestResult.fail("测试 Flink 配置失败："+e.getMessage());
        }
        try {
            initYarnClient();
            if(yarnClient.isInState(Service.STATE.STARTED)){
                logger.info("配置连接测试成功");
                return TestResult.success();
            }else{
                logger.error("该配置无对应 Yarn 集群存在");
                return TestResult.fail("该配置无对应 Yarn 集群存在");
            }
        }catch (Exception e){
            logger.error("测试 Yarn 配置失败："+e.getMessage());
            return TestResult.fail("测试 Yarn 配置失败："+e.getMessage());
        }
    }
}
