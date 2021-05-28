package com.dlink.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dlink.common.result.ProTableResult;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * ISuperService
 *
 * @author wenmo
 * @since 2021/5/25
 **/
public interface ISuperService<T> extends IService<T> {

    ProTableResult<T> selectForProTable(JsonNode para);

}
