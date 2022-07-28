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

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.context.RequestContext;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.TenantMapper;
import com.dlink.model.Namespace;
import com.dlink.model.Role;
import com.dlink.model.Tenant;
import com.dlink.service.NamespaceService;
import com.dlink.service.RoleService;
import com.dlink.service.TaskService;
import com.dlink.service.TenantService;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class TenantServiceImpl extends SuperServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private NamespaceService namespaceService;

    @Autowired
    private TaskService taskService;


    @Override
    public Result saveOrUpdateTenant(Tenant tenant) {
        Integer tenantId = tenant.getId();
        if (Asserts.isNull(tenantId)) {
            Tenant tenantByTenantCode = getTenantByTenantCode(tenant.getTenantCode());
            if (Asserts.isNotNull(tenantByTenantCode)) {
                return Result.failed("该租户已存在");
            }
            tenant.setIsDelete(false);
            if (save(tenant)) {
                RequestContext.set(tenant.getId());
                return Result.succeed("新增成功");
            }
            return Result.failed("新增失败");
        } else {
            if (modifyTenant(tenant)) {
                return Result.failed("修改成功");
            }
            return Result.failed("新增失败");
        }
    }

    @Override
    public Tenant getTenantByTenantCode(String tenantCode) {
        return getOne(new QueryWrapper<Tenant>().eq("tenant_code", tenantCode).eq("is_delete", 0));
    }

    @Override
    public boolean modifyTenant(Tenant tenant) {
        if (Asserts.isNull(tenant.getId())) {
            return false;
        }
        return updateById(tenant);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> deleteTenantById(JsonNode para) {
        for (JsonNode item : para) {
            Integer id = item.asInt();
            Tenant tenant = getById(id);
            if (Asserts.isNull(tenant)) {
                return Result.failed("租户不存在");
            }

            ProTableResult<Role> roleProTableResult = roleService.selectForProTable(para);
            if (roleProTableResult.getData().size() > 0) {
                return Result.failed("删除租户失败，该租户已绑定角色");
            }

            ProTableResult<Namespace> namespaceProTableResult = namespaceService.selectForProTable(para);
            if (namespaceProTableResult.getData().size() > 0) {
                return Result.failed("删除租户失败，该租户已绑定名称空间");
            }

            boolean result = removeById(id);
            if (result) {
                return Result.succeed("删除成功");
            } else {
                return Result.failed("删除失败");
            }
        }
        return Result.failed("删除租户不存在");
    }

    @Override
    public List<Tenant> getTenantByIds(Set<Integer> tenantIds) {
        return baseMapper.getTenantByIds(tenantIds);
    }
}