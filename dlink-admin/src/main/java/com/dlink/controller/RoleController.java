package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.Role;
import com.dlink.service.RoleService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * create or update role
     *
     * @return delete result code
     */
    @PutMapping
    public Result saveOrUpdateRole(@RequestBody JsonNode para) {
        return roleService.saveOrUpdateRole(para);
    }

    /**
     * delete tenant by id
     *
     * @return delete result code
     */
    @DeleteMapping
    public Result deleteRoleById(@RequestBody JsonNode para) {
        return roleService.deleteRoleById(para);
    }

    /**
     * query role list
     */
    @PostMapping
    public ProTableResult<Role> listRoles(@RequestBody JsonNode para) {
        return roleService.selectForProTable(para);
    }
}