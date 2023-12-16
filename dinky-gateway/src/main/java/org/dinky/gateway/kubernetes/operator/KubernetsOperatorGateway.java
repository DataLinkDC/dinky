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

package org.dinky.gateway.kubernetes.operator;

import org.dinky.assertion.Asserts;
import org.dinky.data.enums.JobStatus;
import org.dinky.gateway.AbstractGateway;
import org.dinky.gateway.config.FlinkConfig;
import org.dinky.gateway.config.K8sConfig;
import org.dinky.gateway.enums.UpgradeMode;
import org.dinky.gateway.kubernetes.operator.api.AbstractPodSpec;
import org.dinky.gateway.kubernetes.operator.api.AbstractPodSpec.Resource;
import org.dinky.gateway.kubernetes.operator.api.FlinkDeployment;
import org.dinky.gateway.kubernetes.operator.api.FlinkDeploymentSpec;
import org.dinky.gateway.kubernetes.operator.api.JobSpec;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.gateway.result.TestResult;
import org.dinky.utils.TextUtil;

import org.apache.flink.configuration.CoreOptions;
import org.apache.http.util.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class KubernetsOperatorGateway extends AbstractGateway {

    private Map<String, String> kubernetsConfiguration;
    private K8sConfig k8sConfig;
    private FlinkConfig flinkConfig;
    private FlinkDeployment flinkDeployment = new FlinkDeployment();
    private FlinkDeploymentSpec flinkDeploymentSpec = new FlinkDeploymentSpec();
    private KubernetesClient kubernetesClient;

    private static final Logger logger = LoggerFactory.getLogger(KubernetsOperatorGateway.class);

    @Override
    protected void init() {
        initKubeClient();
        initBase();
        initMetadata();
        initSpec();
        initResource(kubernetesClient);
        initJob();
    }

    private void initKubeClient() {
        kubernetsConfiguration = config.getKubernetesConfig().getConfiguration();
        flinkConfig = config.getFlinkConfig();
        k8sConfig = config.getKubernetesConfig();
        if (TextUtils.isEmpty(k8sConfig.getKubeConfig())) {
            kubernetesClient = new DefaultKubernetesClient();
        } else {
            kubernetesClient = DefaultKubernetesClient.fromConfig(k8sConfig.getKubeConfig());
        }
    }

    @Override
    public TestResult test() {

        kubernetsConfiguration = config.getKubernetesConfig().getConfiguration();
        flinkConfig = config.getFlinkConfig();
        config.getFlinkConfig().setJobName("test");
        initKubeClient();
        initBase();
        initMetadata();
        initSpec();
        kubernetesClient.resource(flinkDeployment).fromServer();
        logger.info("配置连接测试成功");
        return TestResult.success();
    }

    @Override
    public boolean onJobFinishCallback(String status) {

        kubernetsConfiguration = config.getKubernetesConfig().getConfiguration();
        initKubeClient();
        String jobName = config.getFlinkConfig().getJobName();
        if (status.equals(JobStatus.FINISHED.getValue())) {
            logger.info(
                    "start handle job {} done on application mode, job status is {}, the cluster"
                            + " will be delete later..",
                    jobName,
                    status);
            return deleteCluster();
        }
        return false;
    }

    public boolean deleteCluster() {
        initMetadata();
        kubernetesClient.resource(flinkDeployment).delete();
        return true;
    }

    private void initJob() {

        String jarMainClass = config.getAppConfig().getUserJarMainAppClass();
        String userJarPath = config.getAppConfig().getUserJarPath();
        String[] userJarParas = config.getAppConfig().getUserJarParas();
        String parallelism = flinkConfig.getConfiguration().get(CoreOptions.DEFAULT_PARALLELISM.key());

        logger.info(
                "\nThe app config is : \njarMainClass:{}\n userJarPath:{}\n userJarParas:{}\n ",
                jarMainClass,
                userJarPath,
                userJarParas);

        if (Asserts.isNullString(jarMainClass) || Asserts.isNullString(userJarPath)) {
            throw new IllegalArgumentException("jar MainClass or userJarPath must be config!!!");
        }

        JobSpec.JobSpecBuilder jobSpecBuilder = JobSpec.builder()
                .entryClass(jarMainClass)
                .jarURI(userJarPath)
                .args(userJarParas)
                .parallelism(Integer.parseInt(parallelism));

        if (Asserts.isNotNull(config.getFlinkConfig().getSavePoint())) {
            String savePointPath = config.getFlinkConfig().getSavePoint();
            jobSpecBuilder.initialSavepointPath(savePointPath);
            jobSpecBuilder.upgradeMode(UpgradeMode.SAVEPOINT);

            logger.info("find save point config, the path is : {}", savePointPath);
        } else {
            jobSpecBuilder.upgradeMode(UpgradeMode.STATELESS);
            logger.info("no save point config");
        }

        flinkDeploymentSpec.setJob(jobSpecBuilder.build());
    }

    private void initResource(KubernetesClient kubernetesClient) {
        Pod defaultPod;
        AbstractPodSpec jobManagerSpec = new AbstractPodSpec();
        AbstractPodSpec taskManagerSpec = new AbstractPodSpec();
        String jbcpu = kubernetsConfiguration.getOrDefault("kubernetes.jobmanager.cpu", "1");
        String jbmem = flinkConfig.getConfiguration().getOrDefault("jobmanager.memory.process.size", "1G");
        logger.info("jobmanager resource is : cpu-->{}, mem-->{}", jbcpu, jbmem);
        jobManagerSpec.setResource(new Resource(Double.parseDouble(jbcpu), jbmem));

        String tmcpu = kubernetsConfiguration.getOrDefault("kubernetes.taskmanager.cpu", "1");
        String tmmem = flinkConfig.getConfiguration().getOrDefault("taskmanager.memory.process.size", "1G");
        logger.info("taskmanager resource is : cpu-->{}, mem-->{}", tmcpu, tmmem);
        taskManagerSpec.setResource(new Resource(Double.parseDouble(tmcpu), tmmem));

        if (!TextUtil.isEmpty(k8sConfig.getPodTemplate())) {
            InputStream inputStream =
                    new ByteArrayInputStream(k8sConfig.getPodTemplate().getBytes(StandardCharsets.UTF_8));
            defaultPod = kubernetesClient.pods().load(inputStream).get();
            flinkDeploymentSpec.setPodTemplate(defaultPod);
        }
        if (!TextUtil.isEmpty(k8sConfig.getJmPodTemplate())) {
            InputStream inputStream =
                    new ByteArrayInputStream(k8sConfig.getJmPodTemplate().getBytes(StandardCharsets.UTF_8));
            Pod pod = kubernetesClient.pods().load(inputStream).get();
            jobManagerSpec.setPodTemplate(pod);
        }
        if (!TextUtil.isEmpty(k8sConfig.getTmPodTemplate())) {
            InputStream inputStream =
                    new ByteArrayInputStream(k8sConfig.getTmPodTemplate().getBytes(StandardCharsets.UTF_8));
            Pod pod = kubernetesClient.pods().load(inputStream).get();
            taskManagerSpec.setPodTemplate(pod);
        }
        flinkDeploymentSpec.setJobManager(jobManagerSpec);
        flinkDeploymentSpec.setTaskManager(taskManagerSpec);
    }

    private void initSpec() {
        String flinkVersion = flinkConfig.getFlinkVersion();
        String image = kubernetsConfiguration.get("kubernetes.container.image");
        String serviceAccount = kubernetsConfiguration.get("kubernetes.service.account");

        logger.info("\nflinkVersion is : {} \n image is : {}", flinkVersion, image);

        if (Asserts.isNotNull(flinkVersion)) {
            flinkDeploymentSpec.setFlinkVersion(flinkVersion);
        } else {
            throw new IllegalArgumentException("Flink version are not Set！！use Operator must be set!");
        }

        flinkDeploymentSpec.setImage(image);

        flinkDeploymentSpec.setFlinkConfiguration(flinkConfig.getConfiguration());
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
        flinkDeployment.setApiVersion("flink.apache.org/v1beta1");
        flinkDeployment.setKind("FlinkDeployment");

        logger.info("ApiVersion is : {}", "flink.apache.org/v1beta1");
        logger.info("Kind is : {}", "FlinkDeployment");
    }

    private void initMetadata() {
        String jobName = config.getFlinkConfig().getJobName();
        String nameSpace = kubernetsConfiguration.get("kubernetes.namespace");

        logger.info("\njobName is ：{} \n namespce is : {}", jobName, nameSpace);

        // set Meta info , include pod name, namespace conf
        ObjectMeta objectMeta = new ObjectMeta();
        objectMeta.setName(jobName);
        objectMeta.setNamespace(nameSpace);
        flinkDeployment.setMetadata(objectMeta);
    }

    @Override
    public SavePointResult savepointCluster(String savePoint) {
        return null;
    }

    @Override
    public SavePointResult savepointJob(String savePoint) {
        return null;
    }
}
