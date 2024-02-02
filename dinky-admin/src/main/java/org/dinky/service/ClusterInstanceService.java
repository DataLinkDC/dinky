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

import org.dinky.cluster.FlinkClusterInfo;
import org.dinky.data.dto.ClusterInstanceDTO;
import org.dinky.data.model.ClusterInstance;
import org.dinky.job.JobConfig;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

/** ClusterInstanceService */
public interface ClusterInstanceService extends ISuperService<ClusterInstance> {

    /**
     * check cluster heartbeat status
     *
     * @param hosts {@link String} eg: host1:8081,host2:8082,host3:8083,host4:8084
     * @param host {@link String} eg: host1
     * @return {@link FlinkClusterInfo}
     */
    FlinkClusterInfo checkHeartBeat(String hosts, String host);

    /**
     * get job manager address
     *
     * @param clusterInstance {@link ClusterInstance} clusterInstance instance
     * @return {@link String} eg: host1:8081
     */
    String getJobManagerAddress(ClusterInstance clusterInstance);

    /**
     * build environment address
     *
     * @param config {@link JobConfig} the config of job
     * @return {@link String} eg: host1:8081
     */
    String buildEnvironmentAddress(JobConfig config);

    /**
     * list enabled cluster instances
     *
     * @return {@link List< ClusterInstance >}
     */
    List<ClusterInstance> listEnabledAllClusterInstance();

    /**
     * list session enable cluster instances
     *
     * @return {@link List< ClusterInstance >}
     */
    List<ClusterInstance> listSessionEnable();

    /**
     * list auto enable cluster instances
     *
     * @return {@link List< ClusterInstance >}
     */
    List<ClusterInstance> listAutoEnable();

    /**
     * register clusterInstance instance
     *
     * @param clusterInstanceDTO {@link ClusterInstanceDTO} clusterInstanceDTO instance
     * @return {@link ClusterInstance}
     */
    ClusterInstance registersCluster(ClusterInstanceDTO clusterInstanceDTO);

    /**
     * register clusterInstance instance
     *
     * @param clusterInstance {@link ClusterInstance} clusterInstanceDTO instance
     * @return {@link ClusterInstance}
     */
    ClusterInstance registersCluster(ClusterInstance clusterInstance);

    /**
     * delete cluster instance by id
     *
     * @param id {@link Integer} cluster id
     * @return {@link Boolean}
     */
    Boolean deleteClusterInstanceById(Integer id);

    /**
     * enable cluster or disable cluster by id
     *
     * @param id {@link Integer} cluster id
     * @return {@link Boolean}
     */
    Boolean modifyClusterInstanceStatus(Integer id);

    /**
     * recycle cluster instance
     *
     * @return {@link Integer} eg: 1
     */
    Integer recycleCluster();

    /**
     * kill cluster instance by id
     *
     * @param id {@link Integer} cluster id
     */
    void killCluster(Integer id);

    /**
     * deploy session cluster
     *
     * @param id {@link Integer} cluster id
     * @return {@link ClusterInstance}
     */
    ClusterInstance deploySessionCluster(Integer id);

    List<ClusterInstance> selectListByKeyWord(String searchKeyWord, boolean isAutoCreate);

    /**
     * check cluster instance has relationship
     * @param id {@link Integer} alert template id
     * @return {@link Boolean} true: has relationship, false: no relationship
     */
    boolean hasRelationShip(Integer id);

    /**
     * heartbeat
     * @return {@link Long}
     */
    Long heartbeat();
}
