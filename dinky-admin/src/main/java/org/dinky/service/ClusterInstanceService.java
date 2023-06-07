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
import org.dinky.data.model.Cluster;
import org.dinky.mybatis.service.ISuperService;

import java.util.List;

/** ClusterInstanceService */
public interface ClusterInstanceService extends ISuperService<Cluster> {

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
     * @param cluster {@link Cluster} cluster instance
     * @return {@link String} eg: host1:8081
     */
    String getJobManagerAddress(Cluster cluster);

    /**
     * build environment address
     *
     * @param useRemote {@link Boolean} use remote or local
     * @param id {@link Integer} cluster id
     * @return {@link String} eg: host1:8081
     */
    String buildEnvironmentAddress(boolean useRemote, Integer id);

    /**
     * build remote environment address by cluster id
     *
     * @param id {@link Integer} cluster id
     * @return {@link String} eg: host1:8081
     */
    String buildRemoteEnvironmentAddress(Integer id);

    /**
     * build local environment address
     *
     * @return {@link String} eg: host1:8081
     */
    String buildLocalEnvironmentAddress();

    /**
     * list enabled cluster instances
     *
     * @return {@link List<Cluster>}
     */
    List<Cluster> listEnabledAll();

    /**
     * list session enable cluster instances , this method is {@link @Deprecated}
     *
     * @return {@link List<Cluster>}
     */
    @Deprecated
    List<Cluster> listSessionEnable();

    /**
     * list auto enable cluster instances
     *
     * @return {@link List<Cluster>}
     */
    List<Cluster> listAutoEnable();

    /**
     * register cluster instance
     *
     * @param cluster {@link Cluster} cluster instance
     * @return {@link Cluster}
     */
    Cluster registersCluster(Cluster cluster);

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
    Boolean enableClusterInstance(Integer id);

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
     * @return {@link Cluster}
     */
    Cluster deploySessionCluster(Integer id);
}
