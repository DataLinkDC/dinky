package com.dlink.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.Tenant;
import com.dlink.service.TenantService;
import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@RestController
@RequestMapping("/api/tenant")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    /**
     * create or update tenant
     *
     * @return delete result code
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody Tenant tenant) {
        Integer tenantId = tenant.getId();
        if (tenantService.saveOrUpdate(tenant)) {
            return Result.succeed(Asserts.isNotNull(tenantId) ? "修改成功" : "新增成功");
        } else {
            return Result.failed(Asserts.isNotNull(tenantId) ? "修改失败" : "新增失败");
        }
    }

    /**
     * delete tenant by id
     *
     * @return delete result code
     */
    @DeleteMapping()
    public Result deleteTenantById(@RequestBody JsonNode para) {
        return tenantService.deleteTenantById(para);
    }

    /**
     * query tenant list
     */
    @PostMapping
    public ProTableResult<Tenant> listTenants(@RequestBody JsonNode para) {
        return tenantService.selectForProTable(para);
    }
}