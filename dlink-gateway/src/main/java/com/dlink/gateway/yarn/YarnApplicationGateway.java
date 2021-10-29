package com.dlink.gateway.yarn;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.GatewayConfig;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.YarnResult;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.entrypoint.YarnApplicationClusterEntryPoint;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.util.Collections;

/**
 * YarnApplicationGateway
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public class YarnApplicationGateway extends YarnGateway {

    public YarnApplicationGateway(GatewayConfig config) {
        super(config);
    }

    public YarnApplicationGateway() {
    }

    @Override
    public GatewayType getType() {
        return GatewayType.YARN_APPLICATION;
    }

    @Override
    public void init() {
        configuration = GlobalConfiguration.loadConfiguration(config.getConfigDir());
        configuration.set(DeploymentOptions.TARGET, getType().getLongValue());
        if(Asserts.isNotNullString(config.getSavePoint())) {
            configuration.setString(SavepointConfigOptions.SAVEPOINT_PATH, config.getSavePoint());
        }
        clientServiceLoader = new DefaultClusterClientServiceLoader();
    }

    @Override
    public GatewayResult submitJobGraph(JobGraph jobGraph) {
        init();
        YarnResult result = YarnResult.build(getType());
        final ClusterClientFactory clientFactory = clientServiceLoader.getClusterClientFactory(configuration);
        try (final YarnClusterDescriptor clusterDescriptor =
                     (YarnClusterDescriptor) clientFactory.createClusterDescriptor(configuration)) {
            final ClusterSpecification clusterSpecification =
                    clientFactory.getClusterSpecification(configuration);
            ClusterClientProvider<ApplicationId> clusterClientProvider = clusterDescriptor.deployInternal(
                    clusterSpecification,
                    config.getJobName(),
                    YarnApplicationClusterEntryPoint.class.getName(),
                    jobGraph,
                    false);
            ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
            ApplicationId applicationId = clusterClient.getClusterId();
            result.setAppId(applicationId.toString());
            result.setWebURL(clusterClient.getWebInterfaceURL());
            result.success();
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result.fail(e.getMessage());
        }
        return result;
    }

    @Override
    public GatewayResult submitJar() {
        init();
        YarnResult result = YarnResult.build(getType());
        logger.warn(config.toString());
        configuration.set(PipelineOptions.JARS, Collections.singletonList(config.getUserJarPath()));
        configuration.set(YarnConfigOptions.APPLICATION_NAME, config.getJobName());
        ApplicationConfiguration appConfig = new ApplicationConfiguration(config.getUserJarParas(), config.getUserJarMainAppClass());
        final ClusterClientFactory clientFactory = clientServiceLoader.getClusterClientFactory(configuration);
        try (final YarnClusterDescriptor clusterDescriptor =
                     (YarnClusterDescriptor) clientFactory.createClusterDescriptor(configuration)) {
            final ClusterSpecification clusterSpecification =
                    clientFactory.getClusterSpecification(configuration);
            ClusterClientProvider<ApplicationId> clusterClientProvider = clusterDescriptor.deployApplicationCluster(
                    clusterSpecification,
                    appConfig);
            ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
            ApplicationId applicationId = clusterClient.getClusterId();
            result.setAppId(applicationId.toString());
            result.setWebURL(clusterClient.getWebInterfaceURL());
            result.success();
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result.fail(e.getMessage());
        }
        return result;
    }
}
