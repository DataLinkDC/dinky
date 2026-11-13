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

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Loads JobManager and cluster host addresses already recorded in dinky_cluster.
 */
@Component
public class KnownClusterAddressLoader {

    private static final String SELECT_CLUSTER_ADDRESSES = "SELECT hosts, job_manager_host FROM dinky_cluster";

    private final JdbcTemplate jdbcTemplate;

    public KnownClusterAddressLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> load() {
        List<String> addresses = new ArrayList<>();
        jdbcTemplate.query(SELECT_CLUSTER_ADDRESSES, rs -> {
            FlinkProxySupport.collectClusterAddress(addresses, rs.getString("hosts"));
            FlinkProxySupport.collectClusterAddress(addresses, rs.getString("job_manager_host"));
        });
        return addresses;
    }
}
