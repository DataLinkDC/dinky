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
import org.dinky.context.TenantContextHolder;
import org.dinky.data.constant.BaseConstant;
import org.dinky.data.dto.AssignUserToTenantDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.model.rbac.Role;
import org.dinky.data.model.rbac.Tenant;
import org.dinky.data.model.rbac.UserTenant;
import org.dinky.data.result.Result;
import org.dinky.mapper.TenantMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.RoleService;
import org.dinky.service.TenantService;
import org.dinky.service.UserTenantService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl extends SuperServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Resource
    @Lazy
    private RoleService roleService;

    private final UserTenantService userTenantService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> saveOrUpdateTenant(Tenant tenant) {
        Integer tenantId = tenant.getId();
        if (Asserts.isNull(tenantId)) {
            Tenant tenantByTenantCode = getTenantByTenantCode(tenant.getTenantCode());
            if (Asserts.isNotNull(tenantByTenantCode)) {
                return Result.failed(Status.TENANT_ALREADY_EXISTS);
            }
            tenant.setIsDelete(false);
            if (save(tenant)) {
                TenantContextHolder.set(tenant.getId());
                return Result.succeed(Status.ADDED_SUCCESS);
            }
            return Result.failed(Status.ADDED_FAILED);
        } else {
            if (modifyTenant(tenant)) {
                return Result.succeed(Status.MODIFY_SUCCESS);
            }
            return Result.failed(Status.MODIFY_FAILED);
        }
    }

    @Override
    public Tenant getTenantByTenantCode(String tenantCode) {
        return getOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getTenantCode, tenantCode)
                .eq(Tenant::getIsDelete, 0));
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public List<Tenant> getTenantListByUserId(Integer userId) {
        List<UserTenant> userTenants = userTenantService
                .getBaseMapper()
                .selectList(new LambdaQueryWrapper<UserTenant>().eq(UserTenant::getUserId, userId));
        if (CollectionUtil.isNotEmpty(userTenants)) {
            List<Integer> tenantIds = new ArrayList<>();
            userTenants.forEach(userTenant -> tenantIds.add(userTenant.getTenantId()));
            return getBaseMapper().selectList(new LambdaQueryWrapper<Tenant>().in(Tenant::getId, tenantIds));
        }
        return null;
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
    public Result<Void> deleteTenantById(JsonNode para) {
        for (JsonNode item : para) {
            Integer id = item.asInt();
            Tenant tenant = getById(id);
            if (Asserts.isNull(tenant)) {
                return Result.failed("租户不存在");
            }

            Long tenantRoleCount =
                    roleService.getBaseMapper().selectCount(new QueryWrapper<Role>().eq("tenant_id", id));
            if (tenantRoleCount > 0) {
                return Result.failed("删除租户失败，该租户已绑定角色");
            }

            tenant.setIsDelete(true);
            boolean result = updateById(tenant);
            if (result) {
                return Result.succeed("删除成功");
            } else {
                return Result.failed("删除失败");
            }
        }
        return Result.failed("删除租户不存在");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> removeTenantById(Integer tenantId) {
        Tenant tenant = getById(tenantId);
        if (Asserts.isNull(tenant)) {
            return Result.failed(Status.TENANT_NOT_EXIST);
        }

        List<UserTenant> userTenants = userTenantService
                .getBaseMapper()
                .selectList(new LambdaQueryWrapper<UserTenant>().eq(UserTenant::getTenantId, tenantId));
        if (CollectionUtil.isNotEmpty(userTenants)) {
            return Result.failed(Status.TENANT_BINDING_USER);
        }
        Integer deleteByIdResult = baseMapper.deleteById(tenantId);
        if (deleteByIdResult > 0) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> assignUserToTenant(AssignUserToTenantDTO assignUserToTenantDTO) {
        List<UserTenant> tenantUserList = new ArrayList<>();
        Integer tenantId = assignUserToTenantDTO.getTenantId();
        userTenantService.remove(new LambdaQueryWrapper<UserTenant>().eq(UserTenant::getTenantId, tenantId));
        List<Integer> userIds = assignUserToTenantDTO.getUserIds();
        for (Integer userId : userIds) {
            UserTenant userTenant = new UserTenant();
            userTenant.setTenantId(tenantId);
            userTenant.setUserId(userId);
            tenantUserList.add(userTenant);
        }
        // save or update user role
        boolean result = userTenantService.saveOrUpdateBatch(tenantUserList, BaseConstant.DEFAULT_BATCH_INSERT_SIZE);
        if (result) {
            return Result.succeed(Status.TENANT_ASSIGN_USER_SUCCESS);
        } else {
            if (tenantUserList.size() == 0) {
                return Result.succeed(Status.TENANT_BINDING_USER_DELETE_ALL);
            }
            return Result.failed(Status.TENANT_ASSIGN_USER_FAILED);
        }
    }
}
