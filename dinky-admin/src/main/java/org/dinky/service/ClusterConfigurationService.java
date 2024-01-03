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

package org.dinky.service;

import org.dinky.data.dto.ClusterConfigurationDTO;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.gateway.model.FlinkClusterConfig;
import org.dinky.gateway.result.TestResult;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

/**
 * ClusterConfigService
 *
 * @since 2021/11/6 20:52
 */
public interface ClusterConfigurationService extends ISuperService<ClusterConfiguration> {

    /**
     * Get the cluster configuration with the given ID.
     *
     * @param id The ID of the cluster configuration to get.
     * @return A {@link ClusterConfiguration} object representing the found cluster configuration.
     */
    ClusterConfiguration getClusterConfigById(Integer id);

    /**
     * List all enabled cluster configurations.
     *
     * @return A list of {@link ClusterConfiguration} objects representing all enabled cluster configurations.
     */
    List<ClusterConfiguration> listEnabledAllClusterConfig();

    /**
     * Get the Flink cluster configuration with the given ID.
     *
     * @param id The ID of the Flink cluster configuration to get.
     * @return A {@link FlinkClusterConfig} object representing the found Flink cluster configuration.
     */
    FlinkClusterConfig getFlinkClusterCfg(Integer id);

    /**
     * Test the given cluster configuration.
     *
     * @param clusterConfigurationDTO A {@link ClusterConfigurationDTO} object representing the cluster configuration to test.
     * @return A {@link TestResult} object representing the test results.
     */
    TestResult testGateway(ClusterConfigurationDTO clusterConfigurationDTO);

    /**
     * Modify the status of a cluster configuration based on its ID.
     *
     * @param id The ID of the cluster configuration to modify.
     * @return A boolean value indicating whether the modification was successful.
     */
    Boolean modifyClusterConfigStatus(Integer id);

    List<ClusterConfigurationDTO> selectListByKeyWord(String keyword);

    Boolean deleteClusterConfigurationById(Integer id);

    Boolean hasRelationShip(Integer id);
}
