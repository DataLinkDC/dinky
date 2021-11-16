package com.dlink.job;

import com.dlink.parser.SqlType;

/**
 * StatementParam
 *
 * @author wenmo
 * @since 2021/11/16
 */
public class StatementParam {
    private String value;
    private SqlType type;

    public StatementParam(String value, SqlType type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SqlType getType() {
        return type;
    }

    public void setType(SqlType type) {
        this.type = type;
    }
}
