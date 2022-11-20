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

package com.dlink.gateway.kubernets.operator;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.AbstractGateway;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.KubernetesResult;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.gateway.result.TestResult;

import org.apache.flink.kubernetes.operator.crd.FlinkDeployment;
import org.apache.flink.kubernetes.operator.crd.spec.FlinkDeploymentSpec;
import org.apache.flink.kubernetes.operator.crd.spec.FlinkVersion;
import org.apache.flink.kubernetes.operator.crd.spec.JobManagerSpec;
import org.apache.flink.kubernetes.operator.crd.spec.JobSpec;
import org.apache.flink.kubernetes.operator.crd.spec.TaskManagerSpec;
import org.apache.flink.kubernetes.operator.crd.spec.UpgradeMode;
import org.apache.flink.runtime.jobgraph.JobGraph;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ListOptions;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;

public class KubetnetsApplicationOperatorGateway extends AbstractGateway {

    private Map<String, String> submitConfiguration;
    private FlinkDeployment flinkDeployment = new FlinkDeployment();
    private FlinkDeploymentSpec flinkDeploymentSpec = new FlinkDeploymentSpec();

    private KubernetesClient kubernetesClient;

    private static final Logger logger = LoggerFactory.getLogger(KubetnetsApplicationOperatorGateway.class);

    @Override
    protected void init() {
        submitConfiguration = config.getClusterConfig().getClusterConfig();
        kubernetesClient = new DefaultKubernetesClient();
        initBase();
        initMetadata();
        initSpec();
        initResource(kubernetesClient);
        initJob();
    }

    @Override
    public GatewayType getType() {
        return GatewayType.KUBERNETES_APPLICATION_OPERATOR;
    }

    @Override
    public GatewayResult submitJobGraph(JobGraph jobGraph) {
        return null;
    }

    @Override
    public GatewayResult submitJar() {
        logger.info("start submit flink jar use {}", getType());

        KubernetesResult result = KubernetesResult.build(getType());
        try {
            init();
            kubernetesClient.resource(flinkDeployment).delete();

            kubernetesClient.resource(flinkDeployment)
                    .waitUntilCondition(flinkDeployment -> flinkDeployment == null ? true : false, 1, TimeUnit.MINUTES);

            kubernetesClient.resource(flinkDeployment).createOrReplace();

            kubernetesClient.resource(flinkDeployment).waitUntilCondition(new Predicate<FlinkDeployment>() {

                @Override
                public boolean test(FlinkDeployment flinkDeployment) {
                    String status = flinkDeployment.getStatus().getJobManagerDeploymentStatus().toString();
                    logger.info("deploy kubernetes , status is : {}", status);
                    String error = flinkDeployment.getStatus().getError();
                    if (Asserts.isNotNullString(error)) {
                        logger.info("deploy kubernetes error :{}", error);
                        return true;
                    }
                    if (status.equals("READY")) {
                        logger.info("deploy kubernetes success ");
                        String jobId = flinkDeployment.getStatus().getJobStatus().getJobId();
                        result.setJids(Collections.singletonList(jobId));
                        return true;
                    }
                    return false;
                }
            }, 1, TimeUnit.MINUTES);

            // get jobmanager addr by service
            ListOptions options = new ListOptions();
            String serviceName = config.getFlinkConfig().getJobName() + "-rest";
            options.setFieldSelector("metadata.name=" + serviceName);
            ServiceList list = kubernetesClient.services().list(options);
            String ipPort = "";
            for (Service item : list.getItems()) {
                ipPort += item.getSpec().getClusterIP();
                for (ServicePort servicePort : item.getSpec().getPorts()) {
                    if (servicePort.getName().equals("rest")) {
                        ipPort += ":" + servicePort.getPort();
                    }
                }
            }

            result.setWebURL("http://" + ipPort);
            result.setClusterId(result.getJids().get(0) + System.currentTimeMillis());
            result.success();
        } catch (KubernetesClientException e) {
            // some error while connecting to kube cluster
            result.fail(e.getMessage());
            e.printStackTrace();
        }
        logger.info("submit {} job finish, web url is {}, jobid is {}", getType(), result.getWebURL(),
                result.getJids());
        return result;
    }

    @Override
    public SavePointResult savepointCluster() {
        return null;
    }

    @Override
    public SavePointResult savepointCluster(String savePoint) {
        return null;
    }

    @Override
    public SavePointResult savepointJob() {
        return null;
    }

    @Override
    public SavePointResult savepointJob(String savePoint) {
        return null;
    }

    @Override
    public TestResult test() {
        return TestResult.success();
    }

    public boolean handleJobDone() {
        submitConfiguration = config.getClusterConfig().getClusterConfig();
        kubernetesClient = new DefaultKubernetesClient();
        String jobName = config.getFlinkConfig().getJobName();
        String status = submitConfiguration.getOrDefault("state", "");
        logger.info(
                "start handle job {} done on application mode, job status is {}, the cluster will be delete later..",
                jobName, status);
        return deleteCluster();
    }

    public boolean deleteCluster() {
        initMetadata();
        return kubernetesClient.resource(flinkDeployment).delete();
    }

    private void initJob() {

        String jarMainClass = config.getAppConfig().getUserJarMainAppClass();
        String userJarPath = config.getAppConfig().getUserJarPath();
        String[] userJarParas = config.getAppConfig().getUserJarParas();
        Integer parallelism = config.getAppConfig().getParallelism();

        logger.info("\nThe app config is : \njarMainClass:{}\n userJarPath:{}\n userJarParas:{}\n parallelism:{}",
                jarMainClass, userJarPath, userJarParas, parallelism);

        if (Asserts.isNullString(jarMainClass) || Asserts.isNullString(userJarPath)) {
            throw new IllegalArgumentException("jarMainClass or userJarPath must be config!!!");
        }

        JobSpec.JobSpecBuilder jobSpecBuilder = JobSpec.builder().entryClass(jarMainClass)
                .jarURI(userJarPath)
                .args(userJarParas)
                .parallelism(parallelism);

        if (Asserts.isNotNull(config.getFlinkConfig().getSavePoint())) {
            String savePointPath = config.getFlinkConfig().getSavePoint();
            jobSpecBuilder.initialSavepointPath(savePointPath);
            jobSpecBuilder.upgradeMode(UpgradeMode.SAVEPOINT);

            logger.info("find save point config, the path is : {}", savePointPath);
        } else {
            jobSpecBuilder.upgradeMode(UpgradeMode.STATELESS);
            logger.info("no save point config");
        }

        flinkDeployment.getSpec().setJob(jobSpecBuilder.build());
    }

    private void initResource(KubernetesClient kubernetesClient) {
        Pod defaultPod;
        JobManagerSpec jobManagerSpec = new JobManagerSpec();
        TaskManagerSpec taskManagerSpec = new TaskManagerSpec();
        if (submitConfiguration.containsKey("kubernetes.pod-template")) {
            InputStream inputStream = new ByteArrayInputStream(submitConfiguration
                    .get("kubernetes.pod-template")
                    .getBytes(StandardCharsets.UTF_8));
            defaultPod = kubernetesClient.pods().load(inputStream).get();
            flinkDeploymentSpec.setPodTemplate(defaultPod);
        }
        if (submitConfiguration.containsKey("kubernetes.pod-template.jobmanager")) {
            InputStream inputStream = new ByteArrayInputStream(submitConfiguration
                    .get("kubernetes.pod-template.jobmanager")
                    .getBytes(StandardCharsets.UTF_8));
            Pod pod = kubernetesClient.pods().load(inputStream).get();
            jobManagerSpec.setPodTemplate(pod);
        }
        if (submitConfiguration.containsKey("kubernetes.pod-template.taskmanager")) {
            InputStream inputStream = new ByteArrayInputStream(submitConfiguration
                    .get("kubernetes.pod-template.taskmanager")
                    .getBytes(StandardCharsets.UTF_8));
            Pod pod = kubernetesClient.pods().load(inputStream).get();
            taskManagerSpec.setPodTemplate(pod);
        }
        flinkDeploymentSpec.setJobManager(jobManagerSpec);
        flinkDeploymentSpec.setTaskManager(taskManagerSpec);
    }

    private void initSpec() {
        FlinkVersion flinkVersion = getFlinkVersion(config.getClusterConfig().getFlinkVersion());
        String image = submitConfiguration.get("kubernetes.container.image");
        String serviceAccount = submitConfiguration.get("kubernetes.service.account");

        logger.info("\nflinkVersion is : {} \n image is : {}", flinkVersion, image);

        if (Asserts.isNotNull(flinkVersion)) {
            flinkDeploymentSpec.setFlinkVersion(flinkVersion);
        } else {
            throw new IllegalArgumentException("Flink version are not Set！！use Operator must be set!");
        }

        flinkDeploymentSpec.setImage(image);

        flinkDeploymentSpec.setFlinkConfiguration(config.getFlinkConfig().getConfiguration());
        flinkDeployment.setSpec(flinkDeploymentSpec);

        if (Asserts.isNotNull(serviceAccount)) {
            flinkDeploymentSpec.setServiceAccount(serviceAccount);
            logger.info("serviceAccount is : {}", serviceAccount);
        } else {
            logger.info("serviceAccount not config, use default");
            flinkDeploymentSpec.setServiceAccount("default");
        }
    }

    private void initBase() {
        // set k8s version info and deployment type
        flinkDeployment.setApiVersion("flink.apache.org/v1beta1");
        flinkDeployment.setKind("FlinkDeployment");

        logger.info("ApiVersion is : {}", "flink.apache.org/v1beta1");
        logger.info("Kind is : {}", "FlinkDeployment");
    }

    private void initMetadata() {
        String jobName = config.getFlinkConfig().getJobName();
        String nameSpace = submitConfiguration.get("kubernetes.namespace");

        logger.info("\njobName is ：{} \n namespce is : {}", jobName, nameSpace);

        // set Meta info , include pod name, namespace conf
        ObjectMeta objectMeta = new ObjectMeta();
        objectMeta.setName(jobName);
        objectMeta.setNamespace(nameSpace);
        flinkDeployment.setMetadata(objectMeta);
    }

    private FlinkVersion getFlinkVersion(Object version) {
        if ("v1_13".equals(version)) {
            return FlinkVersion.v1_13;
        } else if ("v1_14".equals(version)) {
            return FlinkVersion.v1_14;
        } else if ("v1_15".equals(version)) {
            return FlinkVersion.v1_15;
        } else if ("v1_16".equals(version)) {
            return FlinkVersion.v1_16;
        }
        return null;
    }
}
