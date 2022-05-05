package com.dlink.service;

import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.model.Role;
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

}
