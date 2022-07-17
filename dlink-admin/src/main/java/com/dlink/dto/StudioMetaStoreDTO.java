package com.dlink.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.GatewayType;
import com.dlink.job.JobConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * StudioMetaStoreDTO
 *
 * @author wenmo
 * @since 2022/7/16 23:18
 */
@Getter
public class StudioMetaStoreDTO extends AbstractStatementDTO {
    private String catalog;
    private String database;
    private String dialect;
    private Integer databaseId;

    public JobConfig getJobConfig() {
        return new JobConfig(
            GatewayType.LOCAL.getLongValue(), true, false, false, false,
            null, null, null, null, null,
            null, isFragment(), false, false, 0,
            null, null, null, null, null);
    }
}
