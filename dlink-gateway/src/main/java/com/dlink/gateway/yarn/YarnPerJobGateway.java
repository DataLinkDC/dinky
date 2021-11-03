package com.dlink.gateway.yarn;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.GatewayConfig;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.YarnResult;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;

/**
 * YarnApplicationGateway
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public class YarnPerJobGateway extends YarnGateway {

    public YarnPerJobGateway(GatewayConfig config) {
        super(config);
    }

    public YarnPerJobGateway() {
    }

    @Override
    public GatewayType getType() {
        return GatewayType.YARN_PER_JOB;
    }

    @Override
    public GatewayResult submitJobGraph(JobGraph jobGraph) {
        if(Asserts.isNull(yarnClient)){
            init();
        }
        YarnResult result = YarnResult.build(getType());
        ClusterSpecification clusterSpecification = new ClusterSpecification.ClusterSpecificationBuilder().createClusterSpecification();
        YarnClusterDescriptor yarnClusterDescriptor = new YarnClusterDescriptor(
                configuration, yarnConfiguration, yarnClient, YarnClientYarnClusterInformationRetriever.create(yarnClient), true);
        try {
            ClusterClientProvider<ApplicationId> clusterClientProvider = yarnClusterDescriptor.deployJobCluster(clusterSpecification,jobGraph,false);
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
        throw new GatewayException("Couldn't deploy Yarn Per-Job Cluster with User Application Jar.");
    }
}
