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

package com.dlink.service.impl;

import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.NamespaceMapper;
import com.dlink.model.Namespace;
import com.dlink.model.RoleNamespace;
import com.dlink.model.Tenant;
import com.dlink.service.NamespaceService;
import com.dlink.service.RoleNamespaceService;
import com.dlink.service.TenantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class NamespaceServiceImpl extends SuperServiceImpl<NamespaceMapper, Namespace> implements NamespaceService {

    @Autowired
    private RoleNamespaceService roleNamespaceService;

    @Autowired
    private TenantService tenantService;

    @Override
    public ProTableResult<Namespace> selectForProTable(JsonNode para) {
        ProTableResult<Namespace> namespaceProTableResult = super.selectForProTable(para);
        namespaceProTableResult.getData().forEach(namespace -> {
            Tenant tenant = tenantService.getBaseMapper().selectById(namespace.getTenantId());
            namespace.setTenant(tenant);
        });
        return namespaceProTableResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteNamespaceById(JsonNode para) {
        for (JsonNode item : para) {
            Integer id = item.asInt();
            Namespace namespace = getById(id);
            if (Asserts.isNull(namespace)) {
                return Result.failed("名称空间不存在");
            }
            Long roleNamespaceCount = roleNamespaceService.getBaseMapper().selectCount(new QueryWrapper<RoleNamespace>().eq("namespace_id", id));
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