package com.dlink.service;

import com.dlink.dto.APIExecuteSqlDTO;
import com.dlink.dto.APIExplainSqlDTO;
import com.dlink.result.APIJobResult;
import com.dlink.result.ExplainResult;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * APIService
 *
 * @author wenmo
 * @since 2021/12/11 21:45
 */
public interface APIService {

    APIJobResult executeSql(APIExecuteSqlDTO apiExecuteSqlDTO);

    ExplainResult explainSql(APIExplainSqlDTO apiExplainSqlDTO);

    ObjectNode getJobPlan(APIExplainSqlDTO apiExplainSqlDTO);

    ObjectNode getStreamGraph(APIExplainSqlDTO apiExplainSqlDTO);
}
