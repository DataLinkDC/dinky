package com.dlink.parser;

/**
 * SqlType
 *
 * @author wenmo
 * @since 2021/7/3 11:11
 */
public enum  SqlType {
    SELECT("SELECT"),
    CREATE("CREATE"),
    DROP("DROP"),
    ALTER("ALTER"),
    INSERT("INSERT"),
    DESCRIBE("DESCRIBE"),
    EXPLAIN("EXPLAIN"),
    USE("USE"),
    SHOW("SHOW"),
    LOAD("LOAD"),
    UNLOAD("UNLOAD"),
    SET("SET"),
    RESET("RESET"),
    UNKNOWN("UNKNOWN"),
    ;

    private String type;

    SqlType(String type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
