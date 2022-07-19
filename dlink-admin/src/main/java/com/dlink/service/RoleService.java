package com.dlink.service;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.model.Role;
import com.dlink.model.UserRole;
import com.fasterxml.jackson.databind.JsonNode;

public interface RoleService extends ISuperService<Role> {
    /**
     * delete role
     *
     * @param para role id
     * @return delete result code
     */
    Result deleteRoleById(JsonNode para);

    Result saveOrUpdateRole(JsonNode para);

    List<Role> getRoleByIds(Set<Integer> roleIds);

}