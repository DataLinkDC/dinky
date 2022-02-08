package com.dlink.gateway.kubernetes;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.AppConfig;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.KubernetesResult;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.runtime.jobgraph.JobGraph;

import java.util.Collections;

/**
 * KubernetesApplicationGateway
 *
 * @author wenmo
 * @since 2021/12/26 14:59
 */
public class KubernetesApplicationGateway extends KubernetesGateway {
    @Override
    public GatewayType getType() {
        return GatewayType.KUBERNETES_APPLICATION;
    }

    @Override
    public GatewayResult submitJobGraph(JobGraph jobGraph) {
        throw new GatewayException("Couldn't deploy Kubernetes Application Cluster with job graph.");
    }

    @Override
    public GatewayResult submitJar() {
        if(Asserts.isNull(client)){
            init();
        }
        KubernetesResult result = KubernetesResult.build(getType());
        AppConfig appConfig = config.getAppConfig();
        configuration.set(PipelineOptions.JARS, Collections.singletonList(appConfig.getUserJarPath()));
        ClusterSpecification clusterSpecification = new ClusterSpecification.ClusterSpecificationBuilder().createClusterSpecification();
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(appConfig.getUserJarParas(), appConfig.getUserJarMainAppClass());
        KubernetesClusterDescriptor kubernetesClusterDescriptor = new KubernetesClusterDescriptor(configuration, client);
        try{
            ClusterClientProvider<String> clusterClientProvider = kubernetesClusterDescriptor.deployApplicationCluster(clusterSpecification, applicationConfiguration);
            ClusterClient<String> clusterClient = clusterClientProvider.getClusterClient();
            String clusterId = clusterClient.getClusterId();
            result.setClusterId(clusterId);
            result.setWebURL(clusterClient.getWebInterfaceURL());
            result.success();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("任务提交时发生异常",e);
            result.fail(e.getMessage());
        }finally {
            kubernetesClusterDescriptor.close();
        }
        return result;
    }
}
