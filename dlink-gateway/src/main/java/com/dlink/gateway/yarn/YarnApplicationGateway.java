package com.dlink.gateway.yarn;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.AppConfig;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.YarnResult;
import com.dlink.utils.LogUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public GatewayResult submitJobGraph(JobGraph jobGraph) {
        throw new GatewayException("Couldn't deploy Yarn Application Cluster with job graph.");
    }

    @Override
    public GatewayResult submitJar() {
        if (Asserts.isNull(yarnClient)) {
            init();
        }
        YarnResult result = YarnResult.build(getType());
        AppConfig appConfig = config.getAppConfig();
        configuration.set(PipelineOptions.JARS, Collections.singletonList(appConfig.getUserJarPath()));
        ClusterSpecification clusterSpecification = new ClusterSpecification.ClusterSpecificationBuilder().createClusterSpecification();
        String[] userJarParas = appConfig.getUserJarParas();
        if (Asserts.isNull(userJarParas)) {
            userJarParas = new String[0];
        }
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(userJarParas, appConfig.getUserJarMainAppClass());
        YarnClusterDescriptor yarnClusterDescriptor = new YarnClusterDescriptor(
            configuration, yarnConfiguration, yarnClient, YarnClientYarnClusterInformationRetriever.create(yarnClient), true);
        try {
            ClusterClientProvider<ApplicationId> clusterClientProvider = yarnClusterDescriptor.deployApplicationCluster(
                clusterSpecification,
                applicationConfiguration);
            ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
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
                logger.info("JIDS =" + StringUtils.join(jids, ","));
            }
            ApplicationId applicationId = clusterClient.getClusterId();
            result.setAppId(applicationId.toString());
            result.setWebURL(clusterClient.getWebInterfaceURL());
            result.success();
        } catch (Exception e) {
            result.fail(LogUtil.getError(e));
        } finally {
            yarnClusterDescriptor.close();
        }
        return result;
    }
}
