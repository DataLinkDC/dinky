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

package com.dlink.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DefaultDockerCmdExecFactory;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

/**
 * @author ZackYoung
 * @since
 */
public class DockerClientBuilder {
    private final DockerClientConfig dockerClientConfig;

    private DockerCmdExecFactory dockerCmdExecFactory = null;

    private DockerHttpClient dockerHttpClient = null;

    private DockerClientBuilder(DockerClientConfig dockerClientConfig) {
        this.dockerClientConfig = dockerClientConfig;
    }

    public static DockerClientBuilder getInstance() {
        return new DockerClientBuilder(
            DefaultDockerClientConfig.createDefaultConfigBuilder().build()
        );
    }

    /**
     * @deprecated use {@link #getInstance(DockerClientConfig)}
     */
    @Deprecated
    public static DockerClientBuilder getInstance(DefaultDockerClientConfig.Builder dockerClientConfigBuilder) {
        return getInstance(dockerClientConfigBuilder.build());
    }

    public static DockerClientBuilder getInstance(DockerClientConfig dockerClientConfig) {
        return new DockerClientBuilder(dockerClientConfig);
    }

    /**
     * @deprecated use {@link DefaultDockerClientConfig.Builder#withDockerHost(String)}
     */
    @Deprecated
    public static DockerClientBuilder getInstance(String serverUrl) {
        return new DockerClientBuilder(
            DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(serverUrl)
                .build()
        );
    }

    /**
     * @deprecated no replacement, use one of {@link DockerHttpClient}
     */
    @Deprecated
    public static DockerCmdExecFactory getDefaultDockerCmdExecFactory() {
        return new DefaultDockerCmdExecFactory(getInstance().dockerHttpClient, new ObjectMapper());
    }

    /**
     * Note that this method overrides {@link DockerHttpClient} if it was previously set
     *
     * @deprecated use {@link #withDockerHttpClient(DockerHttpClient)}
     */
    @Deprecated
    public DockerClientBuilder withDockerCmdExecFactory(DockerCmdExecFactory dockerCmdExecFactory) {
        this.dockerCmdExecFactory = dockerCmdExecFactory;
        this.dockerHttpClient = null;
        return this;
    }

    /**
     * Note that this method overrides {@link DockerCmdExecFactory} if it was previously set
     */
    public DockerClientBuilder withDockerHttpClient(DockerHttpClient dockerHttpClient) {
        this.dockerCmdExecFactory = null;
        this.dockerHttpClient = dockerHttpClient;
        return this;
    }

    public DockerClient build() {
        if (dockerHttpClient != null) {
            return DockerClientImpl.getInstance(
                dockerClientConfig,
                dockerHttpClient
            );
        } else if (dockerCmdExecFactory != null) {
            return DockerClientImpl.getInstance(dockerClientConfig)
                .withDockerCmdExecFactory(dockerCmdExecFactory);
        } else {
            Logger log = LoggerFactory.getLogger(DockerClientBuilder.class);
            log.warn(
                "'dockerHttpClient' should be set. Falling back to Jersey, will be an error in future releases."
            );

            return DockerClientImpl.getInstance(
                dockerClientConfig,
                new ApacheDockerHttpClient.Builder()
                    .dockerHost(dockerClientConfig.getDockerHost())
                    .sslConfig(dockerClientConfig.getSSLConfig())
                    .build()
            );
        }
    }
}
