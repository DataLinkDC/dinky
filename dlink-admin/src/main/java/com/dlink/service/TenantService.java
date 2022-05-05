package com.dlink.service;


import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.model.Tenant;
import com.dlink.model.User;
import com.fasterxml.jackson.databind.JsonNode;

public interface TenantService extends ISuperService<Tenant> {
    /**
     * delete tenant by id
     *
     * @param para tenant id
     * @return delete result code
     */
    Result deleteTenantById(JsonNode para);

    /**
     * add  or update tenant
     *
     * @param tenant tenant info
     * @return add or update code
     */
    Result saveOrUpdateTenant(Tenant tenant);

    /**
     * @param tenantCode tenant code
     * @return Tenant
     */
    Tenant getTenantByTenantCode(String tenantCode);

    /**
     * @param tenant tenant info
     * @return modify code
     */
    boolean modifyTenant(Tenant tenant);

}
