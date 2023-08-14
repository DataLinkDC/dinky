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

package org.dinky.utils;

import org.dinky.config.Docker;
import org.dinky.docker.DockerClientBuilder;

import java.io.File;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** @since 0.7.0 */
@Getter
@Slf4j
public class DockerClientUtils {

    private final DockerClient dockerClient;
    private final Docker docker;
    private final File dockerfile;

    public DockerClientUtils(Docker docker) {
        this.docker = docker;
        this.dockerfile = FileUtil.writeUtf8String(
                docker.getDockerfile(), System.getProperty("user.dir") + "/tmp/dockerfile/" + UUID.randomUUID());
        dockerClient = DockerClientBuilder.getInstance(DefaultDockerClientConfig.createDefaultConfigBuilder()
                        .withDockerHost(docker.getInstance())
                        .withRegistryUrl(docker.getRegistryUrl())
                        .withRegistryUsername(docker.getRegistryUsername())
                        .withRegistryPassword(docker.getRegistryPassword())
                        .build())
                .build();
        try {
            log.info("===============================  Initializing docker " + " ===============================");
            Info info = dockerClient.infoCmd().exec();
            log.info("===============================  The docker connection is successful, the"
                    + " relevant information is as follows  ===============================");
            log.info(info.toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(
                    "The docker initialization failed. If k8s application mode must be used,"
                            + " please check the configuration and try again! reason:{}",
                    e.getMessage());
        }
    }

    public void initImage() throws InterruptedException {
        try {
            BuildImageResultCallback resultCallback = new BuildImageResultCallback();

            if (FileUtil.readUtf8String(dockerfile).length() > 0) {
                dockerClient
                        .buildImageCmd()
                        .withRemove(true)
                        .withDockerfile(dockerfile)
                        .withTag(docker.getTag())
                        .exec(resultCallback);
                resultCallback.awaitCompletion().onError(new RuntimeException());
            }
            pushImage(docker.getTag());
            cleanNoneImage();
        } finally {
            FileUtil.del(dockerfile);
        }
    }

    public void pushImage(String tag) throws InterruptedException {
        ResultCallback.Adapter<PushResponseItem> resultCallback1 = new ResultCallback.Adapter<>();
        dockerClient.pushImageCmd(tag).exec(resultCallback1);
        try {
            log.info("start push-image: {}", tag);
            resultCallback1.awaitCompletion().onError(new RuntimeException());
            log.info("push-image finish: {}", tag);
        } catch (Exception e) {
            log.error("push-image failed: {} , reason: {}", tag, e.getMessage());
            throw e;
        }
    }

    /** 清除空容器 */
    public void cleanNoneImage() {
        dockerClient.listImagesCmd().exec().stream()
                .filter(x -> x.getRepoTags() == null || "<none>:<none>".equals(x.getRepoTags()[0]))
                .map(Image::getId)
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
     * @param client client
     * @return 创建容器
     */
    public CreateContainerResponse createContainers(DockerClient client, String containerName, String imageName) {
        return client.createContainerCmd(imageName).withName(containerName).exec();
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
