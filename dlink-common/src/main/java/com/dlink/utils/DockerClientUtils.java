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

package com.dlink.utils;

import com.dlink.config.Docker;
import com.dlink.docker.DockerClientBuilder;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ZackYoung
 * @since 0.7.0
 */
@Getter
@Slf4j
public class DockerClientUtils {

    private final DockerClient dockerClient;
    private final Docker docker;
    private final File dockerfile;
    private final String image;

    public DockerClientUtils(Docker docker) {
        this(docker, null);
    }

    public DockerClientUtils(Docker docker, File dockerfile) {
        this.docker = docker;
        this.dockerfile = dockerfile;
        this.image = String.join("/",
                Arrays.asList(docker.getRegistryUrl(), docker.getImageNamespace(), docker.getImageStorehouse())) + ":"
                + docker.getImageDinkyVersion();
        dockerClient = DockerClientBuilder.getInstance(DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(docker.getInstance())
                .withRegistryUrl(docker.getRegistryUrl()).withRegistryUsername(docker.getRegistryUsername())
                .withRegistryPassword(docker.getRegistryPassword())
                .build())
                .build();
        try {
            log.info("===============================  Initializing docker  ===============================");
            Info info = dockerClient.infoCmd().exec();
            log.info(
                    "===============================  The docker connection is successful, the relevant information is as follows  ===============================");
            log.info(info.toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(
                    "The docker initialization failed. If k8s application mode must be used, please check the configuration and try again! reason:{}",
                    e.getMessage());
        }
    }

    public void initImage() throws URISyntaxException, InterruptedException {
        BuildImageResultCallback resultCallback = new BuildImageResultCallback();
        dockerClient.buildImageCmd()
                .withRemove(true)
                .withDockerfile(dockerfile)
                .withTag(image)
                .exec(resultCallback);
        resultCallback.awaitImageId();
        pushImage(image);
        cleanNoneImage();
    }

    public void pushImage(String tag) throws InterruptedException {
        ResultCallback.Adapter<PushResponseItem> resultCallback1 = new ResultCallback.Adapter<>();
        dockerClient.pushImageCmd(tag).exec(resultCallback1);
        try {
            log.info("start push-image: {}", tag);
        } catch (Exception e) {
            log.error("push-image failed: {} , reason: {}", tag, e.getMessage());
        }
        resultCallback1.awaitCompletion();
        log.info("push-image finish: {}", tag);
    }

    /**
     * 清除空容器
     */
    public void cleanNoneImage() {
        dockerClient.listImagesCmd().exec().stream()
                .filter(x -> x.getRepoTags() == null || "<none>:<none>".equals(x.getRepoTags()[0])).map(Image::getId)
                .forEach(id -> {
                    try {
                        dockerClient.removeImageCmd(id).exec();
                    } catch (Exception ignored) {
                        log.warn("容器删除失败，id:{}", id);
                    }
                });
    }

    /**
     * 创建容器
     *
     * @param client
     * @return
     */
    public CreateContainerResponse createContainers(DockerClient client, String containerName, String imageName) {
        CreateContainerResponse container = client.createContainerCmd(imageName)
                .withName(containerName)
                .exec();
        return container;
    }

    /**
     * 启动容器
     *
     * @param client
     * @param containerId
     */
    public void startContainer(DockerClient client, String containerId) {
        client.startContainerCmd(containerId).exec();
    }

    /**
     * 停止容器
     *
     * @param client
     * @param containerId
     */
    public void stopContainer(DockerClient client, String containerId) {
        client.stopContainerCmd(containerId).exec();
    }

    /**
     * 删除容器
     *
     * @param client
     * @param containerId
     */
    public void removeContainer(DockerClient client, String containerId) {
        client.removeContainerCmd(containerId).exec();
    }
}
