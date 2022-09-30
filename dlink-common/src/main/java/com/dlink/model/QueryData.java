package com.dlink.model;

import lombok.Data;

@Data
public class QueryData {
    private String id;

    private String schemaName;

    private String tableName;

    private Option option;

    @Data
    public class Option {
        private String where;
        private String order;
        private String limitStart;
        private String limitEnd;
    }
}
