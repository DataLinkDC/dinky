package com.dlink.dto;

import com.dlink.gateway.GatewayType;
import com.dlink.job.JobConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * StudioMetaStoreDTO
 *
 * @author wenmo
 * @since 2022/7/16 23:18
 */
@Getter
@Setter
public class StudioMetaStoreDTO extends AbstractStatementDTO {
    private String catalog;
    private String database;
    private String table;
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
