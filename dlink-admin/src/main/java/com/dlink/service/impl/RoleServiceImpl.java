package com.dlink.service.impl;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.RoleMapper;
import com.dlink.model.Role;
import com.dlink.model.RoleNamespace;
import com.dlink.model.UserRole;
import com.dlink.service.RoleNamespaceService;
import com.dlink.service.RoleService;
import com.dlink.service.UserRoleService;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * role service impl
 */
@Service
public class RoleServiceImpl extends SuperServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleNamespaceService roleNamespaceService;

    @Autowired
    private UserRoleService userRoleService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result saveOrUpdateRole(JsonNode para) {
        Role role = new Role();
        String id = para.get("id").asText(null);

        role.setTenantId(para.get("tenantId").asInt());
        role.setRoleCode(para.get("roleCode").asText());
        role.setRoleName(para.get("roleName").asText());
        role.setNote(para.get("note").asText());
        if (StringUtils.isNotEmpty(id)) {
            role.setId(Integer.valueOf(id));
        }

        // save or update role
        boolean roleResult = saveOrUpdate(role);

        List<RoleNamespace> roleNamespaceList = new ArrayList<>();
        JsonNode namespaceJsonNode = para.get("namespaceIds");
        for (JsonNode ids : namespaceJsonNode) {
            RoleNamespace roleNamespace = new RoleNamespace();
            roleNamespace.setRoleId(role.getId());
            roleNamespace.setNamespaceId(ids.asInt());
            roleNamespaceList.add(roleNamespace);
        }
        // save or update role namespace relation
        boolean roleNamespaceResult = roleNamespaceService.saveOrUpdateBatch(roleNamespaceList, 1000);

        if (roleResult && roleNamespaceResult) {
            return Result.failed(Asserts.isNotNull(id) ? "修改成功" : "新增成功");
        } else {
            return Result.failed(Asserts.isNotNull(id) ? "修改失败" : "新增失败");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteRoleById(JsonNode para) {
        for (JsonNode item : para) {
            Integer id = item.asInt();
            Role role = getById(id);
            if (Asserts.isNull(role)) {
                return Result.failed("角色不存在");
            }
            ProTableResult<RoleNamespace> roleNamespaceProTableResult = roleNamespaceService.selectForProTable(para);
            if (roleNamespaceProTableResult.getData().size() > 0) {
                return Result.failed("删除角色失败，该角色已绑定名称空间");
            }
            ProTableResult<UserRole> userRoleProTableResult = userRoleService.selectForProTable(para);
            if (userRoleProTableResult.getData().size() > 0) {
                return Result.failed("删除角色失败，该角色已绑定用户");
            }
            boolean result = removeById(id);
            if (result) {
                return Result.succeed("删除角色成功");
            } else {
                return Result.failed("删除角色失败");
            }
        }
        return Result.failed("删除角色不存在");
    }

    @Override
    public List<Role> getRoleByIds(List<Integer> roleIds) {
        return baseMapper.getRoleByIds(roleIds);
    }

}