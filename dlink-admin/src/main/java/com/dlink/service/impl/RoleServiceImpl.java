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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.RoleMapper;
import com.dlink.model.*;
import com.dlink.service.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private TenantService  tenantService;
    @Autowired
    private NamespaceService namespaceService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveOrUpdateRole(JsonNode para) {
        String roleCode = para.get("roleCode").asText();
        String roleName =  para.get("roleName").asText();
        String note =  para.get("note").asText();
        String namespaceIds = para.get("namespaceIds").asText();
        Integer tenantId =  para.get("tenantId").asInt();
        Integer roleResult ;
        Boolean insertResult = false;
        Role  roleCodeBean = getOne(new QueryWrapper<Role>().eq("role_code", roleCode).eq("tenant_id", tenantId));
        if (Asserts.isNotNull(roleCodeBean)) {
            LOG.info("roleCode is exist, roleCode:{} , exec update", roleCode);
            roleCodeBean.setTenantId(tenantId);
            roleCodeBean.setRoleName(roleName);
            roleCodeBean.setNote(note);
            baseMapper.update(roleCodeBean, new QueryWrapper<Role>().eq("id", roleCodeBean.getId()));
            roleResult = roleCodeBean.getId();
            insertResult = true;
        }else {
            LOG.info("roleCode is not exist, roleCode:{} , exec insert", roleCode);
            Role role = new Role();
            role.setRoleCode(roleCode);
            role.setTenantId(tenantId);
            role.setRoleName(roleName);
            role.setNote(note);
            baseMapper.insert(role);
            roleResult = role.getId();
            insertResult = true;
        }

        List<RoleNamespace> roleNamespaceList = new ArrayList<>();
        String[] idsList = namespaceIds.split(",");
        for (String namespaceId : idsList) {
            RoleNamespace roleNamespace = new RoleNamespace();
            roleNamespace.setRoleId(roleResult);
            roleNamespace.setNamespaceId(Integer.valueOf(namespaceId));
            roleNamespaceList.add(roleNamespace);
        }
        // save or update role namespace relation // TODO: 新增正常，更新报错 原因是:role_namespace中有唯一索引，(role_id,namespace_id)
        boolean roleNamespaceResult = roleNamespaceService.saveOrUpdateBatch(roleNamespaceList, 1000);

        if (insertResult && roleNamespaceResult) {
            return Result.succeed(Asserts.isNotNull(roleResult) ? "新增成功" : "修改成功");
        } else {
            return Result.failed(Asserts.isNotNull(roleResult) ? "新增失败" : "修改失败");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteRoles(JsonNode para) {
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                boolean roleNameSpaceRemove =  roleNamespaceService.remove(new QueryWrapper<RoleNamespace>().eq("role_id", id));
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
    public ProTableResult<Role> selectForProTable(JsonNode para,boolean isDelete) {
        ProTableResult<Role> roleProTableResult = super.selectForProTable(para,isDelete);
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