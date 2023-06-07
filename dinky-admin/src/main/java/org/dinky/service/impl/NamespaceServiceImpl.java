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

package org.dinky.service.impl;

import org.dinky.assertion.Asserts;
import org.dinky.data.model.Namespace;
import org.dinky.data.model.RoleNamespace;
import org.dinky.data.model.Tenant;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.mapper.NamespaceMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.NamespaceService;
import org.dinky.service.RoleNamespaceService;
import org.dinky.service.TenantService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Deprecated
public class NamespaceServiceImpl extends SuperServiceImpl<NamespaceMapper, Namespace>
        implements NamespaceService {

    private final RoleNamespaceService roleNamespaceService;

    private final TenantService tenantService;

    @Override
    public ProTableResult<Namespace> selectForProTable(JsonNode para) {
        ProTableResult<Namespace> namespaceProTableResult = super.selectForProTable(para);
        namespaceProTableResult
                .getData()
                .forEach(
                        namespace -> {
                            Tenant tenant =
                                    tenantService
                                            .getBaseMapper()
                                            .selectById(namespace.getTenantId());
                            namespace.setTenant(tenant);
                        });
        return namespaceProTableResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<Void> deleteNamespaceById(JsonNode para) {
        for (JsonNode item : para) {
            Integer id = item.asInt();
            Namespace namespace = getById(id);
            if (Asserts.isNull(namespace)) {
                return Result.failed("名称空间不存在");
            }
            Long roleNamespaceCount =
                    roleNamespaceService
                            .getBaseMapper()
                            .selectCount(new QueryWrapper<RoleNamespace>().eq("namespace_id", id));
            if (roleNamespaceCount > 0) {
                return Result.failed("删除名称空间失败，该名称空间被角色绑定");
            }
            boolean result = removeById(id);
            if (result) {
                return Result.succeed("删除名称空间成功");
            } else {
                return Result.failed("删除名称空间失败");
            }
        }
        return Result.failed("名称空间不存在");
    }
}
