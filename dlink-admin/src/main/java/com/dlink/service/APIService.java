package com.dlink.service;

import com.dlink.dto.APIExecuteSqlDTO;
import com.dlink.result.APIJobResult;

/**
 * APIService
 *
 * @author wenmo
 * @since 2021/12/11 21:45
 */
public interface APIService {

    APIJobResult executeSql(APIExecuteSqlDTO apiExecuteSqlDTO);
}
