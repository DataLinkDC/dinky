package com.dlink.service;


import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.model.Tenant;
import com.fasterxml.jackson.databind.JsonNode;

public interface TenantService extends ISuperService<Tenant> {
    /**
     * delete tenant by id
     *
     * @param para tenant id
     * @return delete result code
     */
    Result deleteTenantById(JsonNode para);

}
