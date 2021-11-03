package com.dlink.gateway.yarn;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.AbstractGateway;
import com.dlink.gateway.ConfigPara;
import com.dlink.gateway.GatewayConfig;
import com.dlink.gateway.result.GatewayResult;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnLogConfigUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.Collections;
import java.util.List;

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
        configuration = GlobalConfiguration.loadConfiguration(config.getFlinkConfigPath());
        addConfigParas(config.getConfigParas());
        configuration.set(DeploymentOptions.TARGET, getType().getLongValue());
        if(Asserts.isNotNullString(config.getSavePoint())) {
            configuration.setString(SavepointConfigOptions.SAVEPOINT_PATH, config.getSavePoint());
        }
        configuration.set(YarnConfigOptions.PROVIDED_LIB_DIRS, Collections.singletonList(config.getFlinkLibs()));
        configuration.set(YarnConfigOptions.APPLICATION_NAME, config.getJobName());
        YarnLogConfigUtil.setLogConfigFileInConfig(configuration, config.getFlinkConfigPath());
    }

    private void initYarnClient(){
        yarnConfiguration = new YarnConfiguration();
        yarnConfiguration.addResource( new Path( config.getYarnConfigPath() ) );
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

    public GatewayResult savepoint(){

    }
}
