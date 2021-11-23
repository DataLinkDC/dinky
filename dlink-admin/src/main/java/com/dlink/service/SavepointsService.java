package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.Savepoints;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Savepoints
 *
 * @author wenmo
 * @since 2021/11/21
 **/
public interface SavepointsService extends ISuperService<Savepoints> {
    List<Savepoints> listSavepointsByTaskId(Integer taskId);

    Savepoints getLatestSavepointByTaskId(Integer taskId);

    Savepoints getEarliestSavepointByTaskId(Integer taskId);

}
