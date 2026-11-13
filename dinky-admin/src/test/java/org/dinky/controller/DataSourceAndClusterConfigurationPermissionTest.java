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

package org.dinky.controller;

import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.DataBaseDTO;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;

/**
 * Tests that test connection and cluster configuration listing require the same permissions as
 * other data source and cluster configuration APIs.
 */
public class DataSourceAndClusterConfigurationPermissionTest {

    @Test
    public void testConnectRequiresDataSourcePermissions() throws Exception {
        SaCheckPermission annotation = DataSourceController.class
                .getMethod("testConnect", DataBaseDTO.class)
                .getAnnotation(SaCheckPermission.class);
        Assert.assertNotNull(annotation);
        Set<String> values = new HashSet<>(Arrays.asList(annotation.value()));
        Assert.assertTrue(values.contains(PermissionConstants.REGISTRATION_DATA_SOURCE_ADD));
        Assert.assertTrue(values.contains(PermissionConstants.REGISTRATION_DATA_SOURCE_EDIT));
        Assert.assertEquals(SaMode.OR, annotation.mode());
    }

    @Test
    public void clusterConfigurationListRequiresPermission() throws Exception {
        SaCheckPermission annotation = ClusterConfigurationController.class
                .getMethod("listClusterConfigList", String.class)
                .getAnnotation(SaCheckPermission.class);
        Assert.assertNotNull(annotation);
        Assert.assertTrue(
                Arrays.asList(annotation.value()).contains(PermissionConstants.REGISTRATION_CLUSTER_CONFIG_LIST));
    }
}
