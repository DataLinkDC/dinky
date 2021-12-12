package com.dlink.job;

import com.dlink.executor.ExecutorSetting;
import com.dlink.gateway.config.AppConfig;
import com.dlink.gateway.config.ClusterConfig;
import com.dlink.gateway.config.FlinkConfig;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.config.SavePointStrategy;
import com.dlink.session.SessionConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * JobConfig
 *
 * @author wenmo
 * @since 2021/6/27 18:45
 */
@Getter
@Setter
public class JobConfig {

    // flink run mode
    private String type;
    private boolean useResult;
    private boolean useSession;
    private String session;
    private boolean useRemote;
    private Integer clusterId;
    private Integer clusterConfigurationId;
    private Integer jarId;
    private String address;
    private Integer taskId;
    private String jobName;
    private boolean useSqlFragment;
    private boolean useStatementSet;
    private Integer maxRowNum;
    private Integer checkpoint;
    private Integer parallelism;
    private SavePointStrategy savePointStrategy;
    private String savePointPath;
    private GatewayConfig gatewayConfig;
    private boolean useRestAPI;

    private Map<String,String> config;

    public JobConfig() {
    }

    public JobConfig(String type, boolean useSession, boolean useRemote, boolean useSqlFragment, boolean useStatementSet, Integer parallelism, Map<String, String> config) {
        this.type = type;
        this.useSession = useSession;
        this.useRemote = useRemote;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.parallelism = parallelism;
        this.config = config;
    }

    public JobConfig(String type, boolean useResult, boolean useSession, String session, boolean useRemote, Integer clusterId,
                     Integer clusterConfigurationId, Integer jarId, Integer taskId, String jobName, boolean useSqlFragment,
                     boolean useStatementSet, Integer maxRowNum, Integer checkpoint, Integer parallelism,
                     Integer savePointStrategyValue, String savePointPath, Map<String,String> config) {
        this.type = type;
        this.useResult = useResult;
        this.useSession = useSession;
        this.session = session;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
        this.clusterConfigurationId = clusterConfigurationId;
        this.jarId = jarId;
        this.taskId = taskId;
        this.jobName = jobName;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.maxRowNum = maxRowNum;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.savePointStrategy = SavePointStrategy.get(savePointStrategyValue);
        this.savePointPath = savePointPath;
        this.config = config;
    }

    public JobConfig(String type, boolean useResult, boolean useSession, String session, boolean useRemote, String address,
                     String jobName, boolean useSqlFragment,
                     boolean useStatementSet, Integer maxRowNum, Integer checkpoint, Integer parallelism,
                     Integer savePointStrategyValue, String savePointPath, Map<String,String> config, GatewayConfig gatewayConfig) {
        this.type = type;
        this.useResult = useResult;
        this.useSession = useSession;
        this.session = session;
        this.useRemote = useRemote;
        this.address = address;
        this.jobName = jobName;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.maxRowNum = maxRowNum;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.savePointStrategy = SavePointStrategy.get(savePointStrategyValue);
        this.savePointPath = savePointPath;
        this.config = config;
        this.gatewayConfig = gatewayConfig;
    }

    public JobConfig(String type,boolean useResult, boolean useSession, String session, boolean useRemote, Integer clusterId) {
        this.type = type;
        this.useResult = useResult;
        this.useSession = useSession;
        this.session = session;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
    }

    public JobConfig(String type,boolean useResult, boolean useSession, boolean useRemote, Integer clusterId,
                     Integer clusterConfigurationId, Integer jarId, Integer taskId, String jobName, boolean useSqlFragment,
                     boolean useStatementSet,Integer checkpoint, Integer parallelism, Integer savePointStrategyValue,
                     String savePointPath,Map<String,String> config) {
        this.type = type;
        this.useResult = useResult;
        this.useSession = useSession;
        this.useRemote = useRemote;
        this.clusterId = clusterId;
        this.clusterConfigurationId = clusterConfigurationId;
        this.jarId = jarId;
        this.taskId = taskId;
        this.jobName = jobName;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.savePointStrategy = SavePointStrategy.get(savePointStrategyValue);
        this.savePointPath = savePointPath;
        this.config = config;
    }

    public ExecutorSetting getExecutorSetting(){
        return new ExecutorSetting(checkpoint,parallelism,useSqlFragment,useStatementSet,savePointPath,jobName,config);
    }

    public void setSessionConfig(SessionConfig sessionConfig){
        if(sessionConfig!=null) {
            address = sessionConfig.getAddress();
            clusterId = sessionConfig.getClusterId();
            useRemote = sessionConfig.isUseRemote();
        }
    }

    public void buildGatewayConfig(Map<String,Object> config){
        gatewayConfig = new GatewayConfig();
        gatewayConfig.setClusterConfig(ClusterConfig.build(config.get("flinkConfigPath").toString(),
                config.get("flinkLibPath").toString(),
                config.get("hadoopConfigPath").toString()));
        AppConfig appConfig = new AppConfig();
        if(config.containsKey("userJarPath")){
            appConfig.setUserJarPath(config.get("userJarPath").toString());
            if(config.containsKey("userJarMainAppClass")){
                appConfig.setUserJarMainAppClass(config.get("userJarMainAppClass").toString());
            }
            if(config.containsKey("userJarParas")){
                appConfig.setUserJarParas(config.get("userJarParas").toString().split(" "));
            }
            gatewayConfig.setAppConfig(appConfig);
        }
        if(config.containsKey("flinkConfig")){
            gatewayConfig.setFlinkConfig(FlinkConfig.build((Map<String, String>)config.get("flinkConfig")));
        }
    }
}
