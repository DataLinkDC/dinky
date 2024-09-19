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
import org.dinky.gateway.enums.UpgradeMode;
import org.dinky.gateway.kubernetes.KubernetesGateway;
import org.dinky.gateway.kubernetes.operator.api.AbstractPodSpec;
import org.dinky.gateway.kubernetes.operator.api.AbstractPodSpec.Resource;
import org.dinky.gateway.kubernetes.operator.api.FlinkDeployment;
import org.dinky.gateway.kubernetes.operator.api.FlinkDeploymentSpec;
import org.dinky.gateway.kubernetes.operator.api.JobSpec;
import org.dinky.gateway.result.SavePointResult;
import org.dinky.gateway.result.TestResult;

import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class KubernetesOperatorGateway extends KubernetesGateway {

    private Map<String, String> kubernetesConfiguration;
    private FlinkDeployment flinkDeployment = new FlinkDeployment();
    private FlinkDeploymentSpec flinkDeploymentSpec = new FlinkDeploymentSpec();

    private static final Logger logger = LoggerFactory.getLogger(KubernetesOperatorGateway.class);

    @Override
    public void init() {
        kubernetesConfiguration = config.getKubernetesConfig().getConfiguration();
        initConfig();
        initBase();
        initMetadata();
        initSpec();
        initResource();
        initJob();
    }

    @Override
    public TestResult test() {
        kubernetesConfiguration = config.getKubernetesConfig().getConfiguration();
        addConfigParas(KubernetesConfigOptions.CLUSTER_ID, UUID.randomUUID().toString());

        initConfig();
        initBase();
        initMetadata();
        initSpec();
        getK8sClientHelper().getKubernetesClient().nodes().list();
        logger.info("配置连接测试成功");
        return TestResult.success();
    }

    @Override
    public boolean onJobFinishCallback(String status) {
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
        kubernetesConfiguration = config.getKubernetesConfig().getConfiguration();
        initConfig();
        initMetadata();
        getK8sClientHelper().getKubernetesClient().resource(flinkDeployment).delete();
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

        // config.getFlinkConfig().getSavePoint() always is null
        // flinkDeployment spec.flinkConfiguration => subJob flinkConfig > kubernetes Config
        // note: flink operator can't read job some config. ex: savepoint & kubernetes.operator config
        String savePointPath = configuration.get(SavepointConfigOptions.SAVEPOINT_PATH);
        logger.info("savePointPath: {}", savePointPath);

        // flink operator upgradeMode specifies savepointPath recovery and needs to be matched
        // with savepointRedeployNonce this parameter.
        if (Asserts.isNotNull(savePointPath)) {
            /*
             * It is possible to redeploy a FlinkDeployment or FlinkSessionJob resource from a target savepoint
             * by using the combination of savepointRedeployNonce and initialSavepointPath in the job spec
             * {@see https://nightlies.apache.org/flink/flink-kubernetes-operator-docs-release-1.8/docs/custom-resource/job-management/#redeploy-using-the-savepointredeploynonce}
             * jobSpecBuilder.initialSavepointPath(savePointPath);
             * jobSpecBuilder.savepointRedeployNonce(1);
             */
            jobSpecBuilder.upgradeMode(UpgradeMode.SAVEPOINT);

            logger.info("find save point config, the path is : {}", savePointPath);
        } else {
            // Officially not recommended for production configuration
            jobSpecBuilder.upgradeMode(UpgradeMode.STATELESS);
            logger.info("no save point config");
        }

        flinkDeploymentSpec.setJob(jobSpecBuilder.build());
    }

    private void initResource() {
        AbstractPodSpec jobManagerSpec = new AbstractPodSpec();
        AbstractPodSpec taskManagerSpec = new AbstractPodSpec();
        String jbcpu = configuration.getString("kubernetes.jobmanager.cpu", "1");
        // default 1G
        String jbmem = configuration.getString(JobManagerOptions.TOTAL_PROCESS_MEMORY.key(), "1G");
        logger.info("jobmanager resource is : cpu-->{}, mem-->{}", jbcpu, jbmem);
        // if enable job high-availability
        Integer replicas = configuration.get(KubernetesConfigOptions.KUBERNETES_JOBMANAGER_REPLICAS);
        if (replicas > KubernetesConfigOptions.KUBERNETES_JOBMANAGER_REPLICAS.defaultValue()
                && !Objects.equals(
                        HighAvailabilityOptions.HA_MODE.defaultValue(),
                        configuration.get(HighAvailabilityOptions.HA_MODE))) {
            // jm ha kubernetes.jobmanager.replicas or job flinkConfig
            replicas = configuration.get(KubernetesConfigOptions.KUBERNETES_JOBMANAGER_REPLICAS);
        } else {
            logger.info(
                    "If you need to enable high availability mode, please set the high-availability.* and kubernetes.jobmanager.replicas > 1 parameters first.");
        }

        jobManagerSpec.setReplicas(replicas);
        jobManagerSpec.setResource(new Resource(Double.parseDouble(jbcpu), jbmem));

        String tmcpu = configuration.getString("kubernetes.taskmanager.cpu", "1");
        String tmmem = configuration.getString(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key(), "1G");
        logger.info("taskmanager resource is : cpu-->{}, mem-->{}", tmcpu, tmmem);
        taskManagerSpec.setResource(new Resource(Double.parseDouble(tmcpu), tmmem));

        flinkDeploymentSpec.setPodTemplate(getDefaultPodTemplate());
        jobManagerSpec.setPodTemplate(getJmPodTemplate());
        taskManagerSpec.setPodTemplate(getTmPodTemplate());
        flinkDeploymentSpec.setJobManager(jobManagerSpec);
        flinkDeploymentSpec.setTaskManager(taskManagerSpec);
    }

    // flink config defined key
    private final List<String> flinkConfigDefinedByFlink =
            Lists.newArrayList("kubernetes.namespace", "kubernetes.cluster-id");

    private void initSpec() {
        String flinkVersion = flinkConfig.getFlinkVersion();
        String image = configuration.get(KubernetesConfigOptions.CONTAINER_IMAGE);
        String serviceAccount = configuration.get(KubernetesConfigOptions.KUBERNETES_SERVICE_ACCOUNT);

        logger.info("\nflinkVersion is : {} \n image is : {}", flinkVersion, image);

        if (Asserts.isNotNull(flinkVersion)) {
            flinkDeploymentSpec.setFlinkVersion(flinkVersion);
        } else {
            throw new IllegalArgumentException("Flink version are not Set！！use Operator must be set!");
        }
        Map<String, String> combinedConfiguration = configuration.toMap();
        // note: rm flinkConfiguration by flinkDefined
        flinkConfigDefinedByFlink.forEach(combinedConfiguration::remove);
        logger.info("combinedConfiguration: {}", combinedConfiguration);
        flinkDeploymentSpec.setFlinkConfiguration(combinedConfiguration);

        flinkDeploymentSpec.setImage(image);
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
        String nameSpace = kubernetesConfiguration.get("kubernetes.namespace");

        logger.info("\njobName is ：{} \n namespace is : {}", jobName, nameSpace);

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
