/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.gateway.kubernetes;

import org.dinky.assertion.Asserts;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.gateway.config.AppConfig;
import org.dinky.gateway.exception.GatewayException;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.KubernetesResult;

import org.apache.flink.api.common.JobStatus;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.utils.Constants;
import org.apache.flink.runtime.client.JobStatusMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import cn.hutool.core.text.StrFormatter;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;

/**
 * KubernetesApplicationGateway
 */
public class KubernetesApplicationGateway extends KubernetesGateway {

    /**
     * @return The type of the Kubernetes gateway, which is GatewayType.KUBERNETES_APPLICATION.
     */
    @Override
    public GatewayType getType() {
        return GatewayType.KUBERNETES_APPLICATION;
    }

    /**
     * Submits a jar file to the Kubernetes gateway.
     *
     * @throws RuntimeException if an error occurs during submission.
     */
    @Override
    public GatewayResult submitJar(FlinkUdfPathContextHolder udfPathContextHolder) {
        try {
            logger.info("Start submit k8s application.");
            ClusterClientProvider<String> clusterClient = deployApplication();
            Deployment deployment = kubernetesClient
                    .apps()
                    .deployments()
                    .inNamespace(configuration.getString(KubernetesConfigOptions.NAMESPACE))
                    .withName(configuration.getString(KubernetesConfigOptions.CLUSTER_ID))
                    .get();
            KubernetesResult kubernetesResult = waitForJmAndJobStart(deployment, clusterClient);
            kubernetesResult.success();
            return kubernetesResult;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            close();
        }
    }

    /**
     * Checks the status of a Pod in Kubernetes.
     *
     * @param pod The Pod to check the status of.
     * @return True if the Pod is ready, false otherwise.
     * @throws GatewayException if the Pod has restarted or terminated.
     */
    public boolean checkPodStatus(Pod pod) {
        // Get the Flink container status.
        Optional<ContainerStatus> flinContainer = pod.getStatus().getContainerStatuses().stream()
                .filter(s -> s.getName().equals(Constants.MAIN_CONTAINER_NAME))
                .findFirst();
        ContainerStatus containerStatus =
                flinContainer.orElseThrow(() -> new GatewayException("Deploy k8s failed, can't find flink container"));

        Yaml yaml = new Yaml(new LogRepresenter());
        String logStr = StrFormatter.format(
                "Got Flink Container State:\nPod: {},\tReady: {},\trestartCount: {},\timage: {}\n"
                        + "------CurrentState------\n{}\n------LastState------\n{}",
                pod.getMetadata().getName(),
                containerStatus.getReady(),
                containerStatus.getRestartCount(),
                containerStatus.getImage(),
                yaml.dumpAsMap(containerStatus.getState()),
                yaml.dumpAsMap(containerStatus.getLastState()));
        logger.info(logStr);

        if (containerStatus.getRestartCount() > 0 || containerStatus.getState().getTerminated() != null) {
            throw new GatewayException("Deploy k8s failed, pod have restart or terminated");
        }
        return containerStatus.getReady();
    }

    /**
     * Deploys an application to Kubernetes.
     *
     * @return A ClusterClientProvider<String> object for accessing the Kubernetes cluster.
     * @throws ClusterDeploymentException if deployment to Kubernetes fails.
     */
    public ClusterClientProvider<String> deployApplication() throws ClusterDeploymentException {
        if (Asserts.isNull(client)) {
            init();
        }
        // Build the commit information
        AppConfig appConfig = config.getAppConfig();
        String[] userJarParas =
                Asserts.isNotNull(appConfig.getUserJarParas()) ? appConfig.getUserJarParas() : new String[0];
        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder =
                createClusterSpecificationBuilder();
        // Deploy to k8s
        ApplicationConfiguration applicationConfiguration =
                new ApplicationConfiguration(userJarParas, appConfig.getUserJarMainAppClass());
        KubernetesClusterDescriptor kubernetesClusterDescriptor =
                new KubernetesClusterDescriptor(configuration, client);
        return kubernetesClusterDescriptor.deployApplicationCluster(
                clusterSpecificationBuilder.createClusterSpecification(), applicationConfiguration);
    }

    /**
     * Waits for the JobManager and the Job to start in Kubernetes.
     *
     * @param deployment    The deployment in Kubernetes.
     * @param clusterClient The ClusterClientProvider<String> object for accessing the Kubernetes cluster.
     * @return A KubernetesResult object containing the Kubernetes gateway's Web URL, the Job ID, and the cluster ID.
     * @throws InterruptedException if waiting is interrupted.
     */
    public KubernetesResult waitForJmAndJobStart(Deployment deployment, ClusterClientProvider<String> clusterClient)
            throws InterruptedException {
        KubernetesResult result = KubernetesResult.build(getType());
        long waitSends = SystemConfiguration.getInstances().getJobIdWait() * 1000L;
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < waitSends) {
            List<Pod> pods = kubernetesClient
                    .pods()
                    .inNamespace(deployment.getMetadata().getNamespace())
                    .withLabelSelector(deployment.getSpec().getSelector())
                    .list()
                    .getItems();
            for (Pod pod : pods) {
                if (!checkPodStatus(pod)) {
                    logger.info("Kubernetes Pod have not ready, reTry at 5 sec later");
                    continue;
                }
                try (ClusterClient<String> client = clusterClient.getClusterClient()) {
                    logger.info("Start get job list ....");
                    Collection<JobStatusMessage> jobList = client.listJobs().get(15, TimeUnit.SECONDS);
                    logger.info("Get K8S Job list: {}", jobList);
                    if (jobList.isEmpty()) {
                        logger.error("Get job is empty, will be reconnect later....");
                        continue;
                    }
                    JobStatusMessage job = jobList.stream().findFirst().get();
                    JobStatus jobStatus = client.getJobStatus(job.getJobId()).get();
                    // To create a cluster ID, you need to combine the cluster ID with the jobID to ensure uniqueness
                    String cid = configuration.getString(KubernetesConfigOptions.CLUSTER_ID)
                            + job.getJobId().toHexString();
                    logger.info("Success get job status:{}", jobStatus);
                    return result.setWebURL(client.getWebInterfaceURL())
                            .setJids(Collections.singletonList(job.getJobId().toHexString()))
                            .setId(cid);
                } catch (GatewayException e) {
                    throw e;
                } catch (Exception ex) {
                    logger.error("Get job status failed,{}", ex.getMessage());
                }
            }
            Thread.sleep(5000);
        }
        throw new GatewayException(
                "The number of retries exceeds the limit, check the K8S cluster for more information");
    }

    /**
     * Represents Java Bean properties for YAML serialization with LogRepresenter.
     * If the property value is null, it is ignored.
     */
    static class LogRepresenter extends Representer {
        /**
         * Represents the Java Bean property, ignoring null and empty values.
         *
         * @param javaBean      The Java Bean object.
         * @param property      The property to represent.
         * @param propertyValue The value of the property.
         * @param tag           The custom tag.
         * @return The represented node tuple, or null if the property value is null.
         */
        @Override
        protected NodeTuple representJavaBeanProperty(
                Object javaBean, Property property, Object propertyValue, Tag tag) {
            if (propertyValue == null) {
                return null;
            }
            return super.representJavaBeanProperty(javaBean, property, propertyValue, tag);
        }
    }
}
