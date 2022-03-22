package com.dlink.gateway.kubernetes;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.AppConfig;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.KubernetesResult;
import com.dlink.utils.LogUtil;

import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.jobgraph.JobGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        if (Asserts.isNull(client)) {
            init();
        }
        KubernetesResult result = KubernetesResult.build(getType());
        AppConfig appConfig = config.getAppConfig();
        configuration.set(PipelineOptions.JARS, Collections.singletonList(appConfig.getUserJarPath()));
        ClusterSpecification clusterSpecification = new ClusterSpecification.ClusterSpecificationBuilder().createClusterSpecification();
        String[] userJarParas = appConfig.getUserJarParas();
        if (Asserts.isNull(userJarParas)) {
            userJarParas = new String[0];
        }
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(userJarParas, appConfig.getUserJarMainAppClass());
        KubernetesClusterDescriptor kubernetesClusterDescriptor = new KubernetesClusterDescriptor(configuration, client);
        try {
            ClusterClientProvider<String> clusterClientProvider = kubernetesClusterDescriptor.deployApplicationCluster(clusterSpecification, applicationConfiguration);
            ClusterClient<String> clusterClient = clusterClientProvider.getClusterClient();
            Collection<JobStatusMessage> jobStatusMessages = clusterClient.listJobs().get();
            int counts = 10;
            while (jobStatusMessages.size() == 0 && counts > 0) {
                Thread.sleep(1000);
                counts--;
                jobStatusMessages = clusterClient.listJobs().get();
                if (jobStatusMessages.size() > 0) {
                    break;
                }
            }
            if (jobStatusMessages.size() > 0) {
                List<String> jids = new ArrayList<>();
                for (JobStatusMessage jobStatusMessage : jobStatusMessages) {
                    jids.add(jobStatusMessage.getJobId().toHexString());
                }
                result.setJids(jids);
            }
            String clusterId = clusterClient.getClusterId();
            result.setClusterId(clusterId);
            result.setWebURL(clusterClient.getWebInterfaceURL());
            result.success();
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        } finally {
            kubernetesClusterDescriptor.close();
        }
        return result;
    }
}
