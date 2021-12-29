package com.dlink.dto;

/**
 * AbstractStatementDTO
 *
 * @author wenmo
 * @since 2021/12/29
 **/
public class AbstractStatementDTO {

    private String statement;
    private Integer envId;

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getEnvId() {
        return envId;
    }

    public void setEnvId(Integer envId) {
        this.envId = envId;
    }
}
