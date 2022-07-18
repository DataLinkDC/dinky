package com.dlink.service;

import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.model.Namespace;
import com.fasterxml.jackson.databind.JsonNode;

public interface NamespaceService extends ISuperService<Namespace> {
    /**
     * delete namespace by id
     *
     * @param para namespace id
     * @return delete result code
     */
    Result deleteNamespaceById(JsonNode para);

}