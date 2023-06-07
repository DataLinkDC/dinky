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
import org.dinky.data.model.Namespace;
import org.dinky.data.model.Role;
import org.dinky.data.model.Tenant;
import org.dinky.data.model.UserTenant;
import org.dinky.data.params.AssignUserToTenantParams;
import org.dinky.data.result.Result;
import org.dinky.mapper.TenantMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.NamespaceService;
import org.dinky.service.RoleService;
import org.dinky.service.TenantService;
import org.dinky.service.UserTenantService;
import org.dinky.utils.I18nMsgUtils;

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
public class TenantServiceImpl extends SuperServiceImpl<TenantMapper, Tenant>
        implements TenantService {

    @Resource @Lazy private RoleService roleService;

    @Resource @Lazy private NamespaceService namespaceService;

    private final UserTenantService userTenantService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> saveOrUpdateTenant(Tenant tenant) {
        Integer tenantId = tenant.getId();
        if (Asserts.isNull(tenantId)) {
            Tenant tenantByTenantCode = getTenantByTenantCode(tenant.getTenantCode());
            if (Asserts.isNotNull(tenantByTenantCode)) {
                return Result.failed(I18nMsgUtils.getMsg("tenant.exists"));
            }
            tenant.setIsDelete(false);
            if (save(tenant)) {
                TenantContextHolder.set(tenant.getId());
                return Result.succeed(I18nMsgUtils.getMsg("create.success"));
            }
            return Result.failed(I18nMsgUtils.getMsg("create.failed"));
        } else {
            if (modifyTenant(tenant)) {
                return Result.succeed(I18nMsgUtils.getMsg("modify.success"));
            }
            return Result.failed(I18nMsgUtils.getMsg("modify.failed"));
        }
    }

    @Override
    public Tenant getTenantByTenantCode(String tenantCode) {
        return getOne(
                new LambdaQueryWrapper<Tenant>()
                        .eq(Tenant::getTenantCode, tenantCode)
                        .eq(Tenant::getIsDelete, 0));
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
                    roleService
                            .getBaseMapper()
                            .selectCount(new QueryWrapper<Role>().eq("tenant_id", id));
            if (tenantRoleCount > 0) {
                return Result.failed("删除租户失败，该租户已绑定角色");
            }

            Long tenantNamespaceCount =
                    namespaceService
                            .getBaseMapper()
                            .selectCount(new QueryWrapper<Namespace>().eq("tenant_id", id));
            if (tenantNamespaceCount > 0) {
                return Result.failed("删除租户失败，该租户已绑定名称空间");
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
            return Result.failed(I18nMsgUtils.getMsg("tenant.not.exists"));
        }

        List<UserTenant> userTenants =
                userTenantService
                        .getBaseMapper()
                        .selectList(
                                new LambdaQueryWrapper<UserTenant>()
                                        .eq(UserTenant::getTenantId, tenantId));
        if (CollectionUtil.isNotEmpty(userTenants)) {
            return Result.failed(I18nMsgUtils.getMsg("tenant.has.user"));
        }
        Integer deleteByIdResult = baseMapper.deleteById(tenantId);
        if (deleteByIdResult > 0) {
            return Result.succeed(I18nMsgUtils.getMsg("delete.success"));
        } else {
            return Result.failed(I18nMsgUtils.getMsg("delete.failed"));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> distributeUsers(JsonNode para) {
        if (para.size() > 0) {
            List<UserTenant> tenantUserList = new ArrayList<>();
            Integer tenantId = para.get("tenantId").asInt();
            userTenantService.remove(new QueryWrapper<UserTenant>().eq("tenant_id", tenantId));
            JsonNode tenantUserJsonNode = para.get("users");
            for (JsonNode ids : tenantUserJsonNode) {
                UserTenant userTenant = new UserTenant();
                userTenant.setTenantId(tenantId);
                userTenant.setUserId(ids.asInt());
                tenantUserList.add(userTenant);
            }
            // save or update user role

            boolean result = userTenantService.saveOrUpdateBatch(tenantUserList, 1000);
            if (result) {
                return Result.succeed("分配用户成功");
            } else {
                if (tenantUserList.size() == 0) {
                    return Result.succeed("该租户下的用户已被全部删除");
                }
                return Result.failed("分配用户失败");
            }
        } else {
            return Result.failed("请选择要分配的用户");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> assignUserToTenant(AssignUserToTenantParams assignUserToTenantParams) {
        List<UserTenant> tenantUserList = new ArrayList<>();
        Integer tenantId = assignUserToTenantParams.getTenantId();
        userTenantService.remove(
                new LambdaQueryWrapper<UserTenant>().eq(UserTenant::getTenantId, tenantId));
        List<Integer> userIds = assignUserToTenantParams.getUserIds();
        for (Integer userId : userIds) {
            UserTenant userTenant = new UserTenant();
            userTenant.setTenantId(tenantId);
            userTenant.setUserId(userId);
            tenantUserList.add(userTenant);
        }
        // save or update user role
        boolean result =
                userTenantService.saveOrUpdateBatch(
                        tenantUserList, BaseConstant.DEFAULT_BATCH_INSERT_SIZE);
        if (result) {
            return Result.succeed(I18nMsgUtils.getMsg("tenant.assign.user.success"));
        } else {
            if (tenantUserList.size() == 0) {
                return Result.succeed(I18nMsgUtils.getMsg("tenant.binding.user.deleteAll"));
            }
            return Result.failed(I18nMsgUtils.getMsg("tenant.assign.user.failed"));
        }
    }
}
