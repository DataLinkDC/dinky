package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.SysConfig;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

/**
 * SysConfig
 *
 * @author wenmo
 * @since 2021/11/18
 **/
public interface SysConfigService extends ISuperService<SysConfig> {

    Map<String,String> getAll();

    void initSysConfig();

    void updateSysConfigByJson(JsonNode node);
}
