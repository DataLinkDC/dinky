package com.dlink.model;

import org.apache.http.util.TextUtils;

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

        @Override
        public String toString() {
            StringBuilder optionBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(where)) {
                optionBuilder.append(" where " + where);
            }
            if (!TextUtils.isEmpty(order)) {
                optionBuilder.append(" order by " + order);
            }
            if (!TextUtils.isEmpty(limitStart) && !TextUtils.isEmpty(limitEnd)) {
                optionBuilder.append(" limit " + limitStart + "," + limitEnd);
            }
            return optionBuilder.toString();
        }
    }
}
