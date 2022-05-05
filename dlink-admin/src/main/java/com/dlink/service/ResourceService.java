package com.dlink.service;

import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.model.Resource;
import com.fasterxml.jackson.databind.JsonNode;


public interface ResourceService extends ISuperService<Resource> {
    /**
     * delete resource
     *
     * @param para role id
     * @return delete result code
     */
    Result deleteResourceById(JsonNode para);
}
