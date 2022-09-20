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
import com.dlink.mapper.RoleMapper;
import com.dlink.model.Namespace;
import com.dlink.model.Role;
import com.dlink.model.RoleNamespace;
import com.dlink.model.Tenant;
import com.dlink.model.UserRole;
import com.dlink.service.NamespaceService;
import com.dlink.service.RoleNamespaceService;
import com.dlink.service.RoleService;
import com.dlink.service.TenantService;
import com.dlink.service.UserRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * role service impl
 */
@Service
public class RoleServiceImpl extends SuperServiceImpl<RoleMapper, Role> implements RoleService {

    private static final Logger LOG = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Autowired
    private RoleNamespaceService roleNamespaceService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TenantService tenantService;
    @Autowired
    private NamespaceService namespaceService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveOrUpdateRole(Role role) {
        if (Asserts.isNull(role.getId())) {
            Role roleCode = roleService.getOne(new QueryWrapper<Role>().eq("role_code", role.getRoleCode()));
            if (Asserts.isNotNull(roleCode)) {
                return Result.failed("角色编号:【" + role.getRoleCode() + "】已存在");
            }
        }
        boolean roleSaveOrUpdate = saveOrUpdate(role);
        boolean roleNamespaceSaveOrUpdate = false;
        if (roleSaveOrUpdate) {
            List<RoleNamespace> roleNamespaceList = roleNamespaceService.getBaseMapper().selectList(new QueryWrapper<RoleNamespace>().eq("role_id", role.getId()));
            roleNamespaceService.removeByIds(roleNamespaceList.stream().map(RoleNamespace::getId).collect(Collectors.toList()));
            List<RoleNamespace> arrayListRoleNamespace = new ArrayList<>();
            String[] idsList = role.getNamespaceIds().split(",");
            for (String namespaceId : idsList) {
                RoleNamespace roleNamespace = new RoleNamespace();
                roleNamespace.setRoleId(role.getId());
                roleNamespace.setNamespaceId(Integer.valueOf(namespaceId));
                arrayListRoleNamespace.add(roleNamespace);
            }
            roleNamespaceSaveOrUpdate = roleNamespaceService.saveBatch(arrayListRoleNamespace);
        }
        if (roleSaveOrUpdate && roleNamespaceSaveOrUpdate) {
            return Result.succeed("保存成功");
        } else {
            return Result.failed("保存失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteRoles(JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                boolean roleNameSpaceRemove = roleNamespaceService.remove(new QueryWrapper<RoleNamespace>().eq("role_id", id));
                boolean userRoleRemove = userRoleService.remove(new QueryWrapper<UserRole>().eq("role_id", id));
                Role role = getById(id);
                role.setIsDelete(true);
                boolean removeById = roleService.updateById(role);
                if (!removeById && !roleNameSpaceRemove && !userRoleRemove) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    @Override
    public List<Role> getRoleByIds(Set<Integer> roleIds) {
        return baseMapper.getRoleByIds(roleIds);
    }

    @Override
    public List<Role> getRoleByTenantIdAndIds(String tenantId, Set<Integer> roleIds) {
        return baseMapper.getRoleByTenantIdAndIds(tenantId, roleIds);
    }

    @Override
    public boolean deleteByIds(List<Integer> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public ProTableResult<Role> selectForProTable(JsonNode para, boolean isDelete) {
        ProTableResult<Role> roleProTableResult = super.selectForProTable(para, isDelete);
        roleProTableResult.getData().forEach(role -> {
            List<Namespace> namespaceArrayList = new ArrayList<>();
            List<Integer> idsList = new ArrayList<>();
            Tenant tenant = tenantService.getBaseMapper().selectById(role.getTenantId());
            roleNamespaceService.list(new QueryWrapper<RoleNamespace>().eq("role_id", role.getId())).forEach(roleNamespace -> {
                Namespace namespaceServiceById = namespaceService.getById(roleNamespace.getNamespaceId());
                namespaceArrayList.add(namespaceServiceById);
                idsList.add(roleNamespace.getNamespaceId());
            });
            role.setTenant(tenant);
            role.setNamespaces(namespaceArrayList);
            String result = idsList.stream().map(Object::toString).collect(Collectors.joining(","));
            role.setNamespaceIds(result);
        });
        return roleProTableResult;
    }
}