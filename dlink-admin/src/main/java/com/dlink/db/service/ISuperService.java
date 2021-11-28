package com.dlink.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dlink.common.result.ProTableResult;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * ISuperService
 *
 * @author wenmo
 * @since 2021/5/25
 **/
public interface ISuperService<T> extends IService<T> {

    ProTableResult<T> selectForProTable(JsonNode para);

    ProTableResult<T> selectForProTable(JsonNode para, boolean isDelete);

    ProTableResult<T> selectForProTable(JsonNode para,Map<String, Object> paraMap);

}
