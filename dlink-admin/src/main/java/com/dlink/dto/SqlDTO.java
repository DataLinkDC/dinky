package com.dlink.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * SqlDTO
 *
 * @author wenmo
 * @since 2021/12/29 19:42
 */
@Getter
@Setter
public class SqlDTO {
    private String statement;
    private Integer databaseId;
    private Integer maxRowNum;

    public SqlDTO(String statement, Integer databaseId, Integer maxRowNum) {
        this.statement = statement;
        this.databaseId = databaseId;
        this.maxRowNum = maxRowNum;
    }

    public static SqlDTO build(String statement, Integer databaseId, Integer maxRowNum){
        return new SqlDTO(statement,databaseId,maxRowNum);
    }
}
