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

import static org.dinky.gateway.kubernetes.utils.DinkyKubernetsConstants.DINKY_K8S_INGRESS_DOMAIN_KEY;
import static org.dinky.gateway.kubernetes.utils.DinkyKubernetsConstants.DINKY_K8S_INGRESS_ENABLED_KEY;

import org.dinky.assertion.Asserts;
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.enums.GatewayType;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.executor.ClusterDescriptorAdapterImpl;
import org.dinky.gateway.config.AppConfig;
import org.dinky.gateway.exception.GatewayException;
import org.dinky.gateway.kubernetes.ingress.DinkyKubernetesIngress;
import org.dinky.gateway.kubernetes.utils.IgnoreNullRepresenter;
import org.dinky.gateway.kubernetes.utils.K8sClientHelper;
import org.dinky.gateway.model.ingress.JobDetails;
import org.dinky.gateway.model.ingress.JobOverviewInfo;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.KubernetesResult;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.kubeclient.FlinkKubeClient;
import org.apache.flink.kubernetes.utils.Constants;
import org.apache.flink.runtime.client.JobStatusMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.Yaml;

import com.alibaba.fastjson2.JSONObject;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;

/**
 * KubernetesApplicationGateway
 */
@Slf4j
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
        init();
        try (KubernetesClient kubernetesClient = getK8sClientHelper().getKubernetesClient()) {
            logger.info("Start submit k8s application.");

            ClusterClientProvider<String> clusterClient =
                    deployApplication(getK8sClientHelper().getClient());

            Deployment deployment = getK8sClientHelper().createDinkyResource();

            KubernetesResult kubernetesResult;

            String ingressDomain = checkUseIngress();
            // if ingress is enabled and ingress domain is not empty, create an ingress service
            if (StringUtils.isNotEmpty(ingressDomain)) {
                K8sClientHelper k8sClientHelper = getK8sClientHelper();
                long ingressStart = SystemClock.now();
                DinkyKubernetesIngress ingress = new DinkyKubernetesIngress(k8sClientHelper);
                ingress.configureIngress(
                        k8sClientHelper.getConfiguration().getString(KubernetesConfigOptions.CLUSTER_ID),
                        ingressDomain,
                        k8sClientHelper.getConfiguration().getString(KubernetesConfigOptions.NAMESPACE));
                log.info("Create dinky ingress service success, cost time:{} ms", SystemClock.now() - ingressStart);
                kubernetesResult = waitForJmAndJobStartByIngress(kubernetesClient, deployment, clusterClient);
            } else {
                kubernetesResult = waitForJmAndJobStart(kubernetesClient, deployment, clusterClient);
            }
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
        if (!flinContainer.isPresent()) {
            return false;
        }
        ContainerStatus containerStatus = flinContainer.get();
        Yaml yaml = new Yaml(new IgnoreNullRepresenter());
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
    public ClusterClientProvider<String> deployApplication(FlinkKubeClient client) throws ClusterDeploymentException {
        // Build the commit information
        AppConfig appConfig = config.getAppConfig();
        String[] userJarParas =
                Asserts.isNotNull(appConfig.getUserJarParas()) ? appConfig.getUserJarParas() : new String[0];
        ClusterSpecification.ClusterSpecificationBuilder clusterSpecificationBuilder =
                createClusterSpecificationBuilder();
        // Deploy to k8s
        ApplicationConfiguration applicationConfiguration =
                new ApplicationConfiguration(userJarParas, appConfig.getUserJarMainAppClass());
        ClusterDescriptorAdapterImpl clusterDescriptorAdapter = new ClusterDescriptorAdapterImpl();
        KubernetesClusterDescriptor kubernetesClusterDescriptor =
                clusterDescriptorAdapter.createKubernetesClusterDescriptor(configuration, client);
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
    public KubernetesResult waitForJmAndJobStart(
            KubernetesClient kubernetesClient, Deployment deployment, ClusterClientProvider<String> clusterClient)
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
                    if (jobList == null) {
                        logger.error("Get job list is failed, Please check your Network !!");
                        continue;
                    } else {
                        logger.info("Get K8S Job list: {}", jobList);
                    }
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
     * Waits for the JobManager and the Job to start in Kubernetes by ingress.
     *
     * @param deployment    The deployment in Kubernetes.
     * @param clusterClient The ClusterClientProvider<String> object for accessing the Kubernetes cluster.
     * @return A KubernetesResult object containing the Kubernetes gateway's Web URL, the Job ID, and the cluster ID.
     * @throws InterruptedException if waiting is interrupted.
     */
    public KubernetesResult waitForJmAndJobStartByIngress(
            KubernetesClient kubernetesClient, Deployment deployment, ClusterClientProvider<String> clusterClient)
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
                try {
                    logger.info("Start get job list ....");
                    JobDetails jobDetails = fetchApplicationJob(kubernetesClient, deployment);
                    if (Objects.isNull(jobDetails) || CollectionUtils.isEmpty(jobDetails.getJobs())) {
                        logger.error("Get job is empty, will be reconnect alter 5 sec later....");
                        Thread.sleep(5000);
                        continue;
                    }
                    JobOverviewInfo jobOverviewInfo =
                            jobDetails.getJobs().stream().findFirst().get();
                    // To create a cluster ID, you need to combine the cluster ID with the jobID to ensure uniqueness
                    String cid = configuration.getString(KubernetesConfigOptions.CLUSTER_ID) + jobOverviewInfo.getJid();
                    logger.info("Success get job status: {}", jobOverviewInfo.getState());

                    return result.setJids(Collections.singletonList(jobOverviewInfo.getJid()))
                            .setWebURL(jobDetails.getWebUrl())
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

    private JobDetails fetchApplicationJob(KubernetesClient kubernetesClient, Deployment deployment) {
        // 判断是不是存在ingress, 如果存在ingress的话返回ingress地址
        Ingress ingress = kubernetesClient
                .network()
                .v1()
                .ingresses()
                .inNamespace(deployment.getMetadata().getNamespace())
                .withName(deployment.getMetadata().getName())
                .get();
        String ingressUrl = getIngressUrl(
                ingress,
                deployment.getMetadata().getNamespace(),
                deployment.getMetadata().getName());
        logger.info("Get dinky ingress url:{}", ingressUrl);
        return invokeJobsOverviewApi(ingressUrl);
    }

    private JobDetails invokeJobsOverviewApi(String restUrl) {
        try {
            String body;
            try (HttpResponse execute = HttpUtil.createGet(restUrl + "/jobs/overview")
                    .timeout(10000)
                    .execute()) {
                // 判断状态码，如果是504的话可能是因为task manage节点还未启动
                if (Objects.equals(execute.getStatus(), HttpStatus.HTTP_GATEWAY_TIMEOUT)) {
                    return null;
                }
                body = execute.body();
            }
            if (StringUtils.isNotEmpty(body)) {
                JobDetails jobDetails = JSONObject.parseObject(body, JobDetails.class);
                jobDetails.setWebUrl(restUrl);
                return jobDetails;
            }
        } catch (Exception e) {
            logger.warn("Get job overview warning, task manage is enabled and can be ignored");
        }
        return null;
    }

    private String getIngressUrl(Ingress ingress, String namespace, String clusterId) {
        if (Objects.nonNull(ingress)
                && Objects.nonNull(ingress.getSpec())
                && Objects.nonNull(ingress.getSpec().getRules())
                && !ingress.getSpec().getRules().isEmpty()) {
            String host = ingress.getSpec().getRules().get(0).getHost();
            return StrFormatter.format("http://{}/{}/{}", host, namespace, clusterId);
        }
        throw new GatewayException(
                StrFormatter.format("Dinky clusterId {} ingress not found in namespace {}", clusterId, namespace));
    }

    /**
     * Determine whether to use the ingress agent service
     * @return ingress domain
     */
    private String checkUseIngress() {
        Map<String, String> ingressConfig = k8sConfig.getIngressConfig();
        if (MapUtils.isNotEmpty(ingressConfig)) {
            boolean ingressEnable =
                    Boolean.parseBoolean(ingressConfig.getOrDefault(DINKY_K8S_INGRESS_ENABLED_KEY, "false"));
            String ingressDomain = ingressConfig.getOrDefault(DINKY_K8S_INGRESS_DOMAIN_KEY, StringUtils.EMPTY);
            if (ingressEnable && StringUtils.isNotEmpty(ingressDomain)) {
                return ingressDomain;
            }
        }
        return StringUtils.EMPTY;
    }
}
