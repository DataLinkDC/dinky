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
import org.dinky.context.FlinkUdfPathContextHolder;
import org.dinky.data.constant.NetConstant;
import org.dinky.data.enums.GatewayType;
import org.dinky.gateway.kubernetes.operator.api.FlinkDeployment;
import org.dinky.gateway.result.GatewayResult;
import org.dinky.gateway.result.KubernetesResult;
import org.dinky.utils.LogUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.http.HttpUtil;
import io.fabric8.kubernetes.api.model.ListOptions;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;

public class KubetnetsApplicationOperatorGateway extends KubernetsOperatorGateway {

    private static final Logger logger = LoggerFactory.getLogger(KubetnetsApplicationOperatorGateway.class);

    @Override
    public GatewayType getType() {
        return GatewayType.KUBERNETES_APPLICATION_OPERATOR;
    }

    private static final String CLUSTER_IP = "ClusterIP";
    private static final String NODE_PORT = "NodePort";
    private static final String LOAD_BALANCER = "LoadBalancer";

    @Override
    public GatewayResult submitJar(FlinkUdfPathContextHolder udfPathContextHolder) {
        // TODO 改为ProcessStep注释
        logger.info("start submit flink jar use {}", getType());

        KubernetesResult result = KubernetesResult.build(getType());

        try {
            init();

            KubernetesClient kubernetesClient = getK8sClientHelper().getKubernetesClient();
            FlinkDeployment flinkDeployment = getFlinkDeployment();

            kubernetesClient.resource(flinkDeployment).delete();
            kubernetesClient.resource(flinkDeployment).waitUntilCondition(Objects::isNull, 1, TimeUnit.MINUTES);
            kubernetesClient.resource(flinkDeployment).createOrReplace();

            FlinkDeployment flinkDeploymentResult = kubernetesClient
                    .resource(flinkDeployment)
                    .waitUntilCondition(
                            flinkDeployment1 -> {
                                if (Asserts.isNull(flinkDeployment1.getStatus())) {

                                    return false;
                                }
                                String status = String.valueOf(
                                        flinkDeployment1.getStatus().getJobManagerDeploymentStatus());
                                logger.info("deploy kubernetes , status is : {}", status);

                                String error = flinkDeployment1.getStatus().getError();
                                if (Asserts.isNotNullString(error)) {
                                    logger.info("deploy kubernetes error :{}", error);
                                    throw new RuntimeException(error);
                                }
                                if (status.equals("READY")) {
                                    String jobId = flinkDeployment1
                                            .getStatus()
                                            .getJobStatus()
                                            .getJobId();
                                    String jobName = flinkDeployment1
                                            .getStatus()
                                            .getJobStatus()
                                            .getJobName();
                                    if (Asserts.isNull(jobName)) {
                                        logger.warn("waiting for job init");
                                        return false;
                                    }
                                    result.setJids(Collections.singletonList(jobId));
                                    logger.info("deploy kubernetes success ");
                                    return true;
                                }
                                if (status.equals("DEPLOYING")) {
                                    getK8sClientHelper().createDinkyResource();
                                }
                                return false;
                            },
                            2,
                            TimeUnit.MINUTES);

            // sleep a time ,because some time the service will not be found
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // get jobmanager addr by service
            ListOptions options = new ListOptions();
            String serviceName = config.getFlinkConfig().getJobName() + "-rest";
            options.setFieldSelector("metadata.name=" + serviceName);
            ServiceList list = kubernetesClient.services().list(options);
            String ipPort = getWebUrl(list, kubernetesClient);
            result.setWebURL("http://" + ipPort);
            result.setId(result.getJids().get(0) + System.currentTimeMillis());
            result.success();
        } catch (KubernetesClientException e) {
            // some error while connecting to kube cluster
            result.fail(LogUtil.getError(e));
            e.printStackTrace();
        }
        logger.info(
                "submit {} job finish, web url is {} , jobid is {}", getType(), result.getWebURL(), result.getJids());
        return result;
    }

    /**
     * 根据实际环境 获取web url
     *
     * @param list
     * @return
     */
    public String getWebUrl(ServiceList list, KubernetesClient kubernetesClient) {
        StringBuilder ipPort = new StringBuilder();
        StringBuilder svcRestPort = new StringBuilder();
        StringBuilder svcType = new StringBuilder();
        for (Service item : list.getItems()) {
            svcRestPort
                    .append(item.getMetadata().getName())
                    .append(".")
                    .append(item.getMetadata().getNamespace());
            svcType.append(item.getSpec().getType());
            for (ServicePort servicePort : item.getSpec().getPorts()) {
                if (servicePort.getName().equals("rest")) {
                    switch (svcType.toString()) {
                        case CLUSTER_IP:
                            ipPort.append(item.getSpec().getClusterIP());
                            ipPort.append(":")
                                    .append(item.getSpec().getPorts().get(0).getPort());
                            break;
                        case NODE_PORT:
                            List<NodeAddress> addresses = kubernetesClient
                                    .nodes()
                                    .list()
                                    .getItems()
                                    .get(0)
                                    .getStatus()
                                    .getAddresses();
                            for (NodeAddress address : addresses) {
                                if (address.getType().equals("InternalIP")) {
                                    ipPort.append(address.getAddress());
                                    break;
                                }
                            }
                            ipPort.append(":")
                                    .append(item.getSpec().getPorts().get(0).getNodePort());
                            break;
                            // TODO 没有环境测试不了
                        case LOAD_BALANCER:
                            ipPort.append(":")
                                    .append(item.getSpec().getPorts().get(0).getPort());
                            svcRestPort
                                    .append(":")
                                    .append(item.getSpec().getPorts().get(0).getPort());
                            break;
                        default:
                            break;
                    }
                    svcRestPort
                            .append(":")
                            .append(item.getSpec().getPorts().get(0).getPort());
                }
            }
        }
        logger.info("get ipPort {} , svcRestPort {}", ipPort.toString(), svcRestPort.toString());
        if (pingIpPort(ipPort.toString())) {
            return ipPort.toString();
        } else if (pingIpPort(svcRestPort.toString())) {
            return svcRestPort.toString();
        } else {
            throw new RuntimeException("all ip port is not available ");
        }
    }

    private boolean pingIpPort(String ipPort) {
        logger.info("ping ip port {}", ipPort);
        try {
            String url = NetConstant.HTTP + ipPort + NetConstant.SLASH + "config";
            HttpUtil.get(url, NetConstant.SERVER_TIME_OUT_ACTIVE);
        } catch (Exception e) {
            logger.warn("ping ip port error", e);
            return false;
        }
        return true;
    }
}
