package org.apache.flink.table.test.utils;

import java.util.regex.Pattern;

/**
 * Author: lwjhn
 * Date: 2024/6/15 10:21
 * Description:
 */
public enum SqlType {
    SELECT("SELECT", "^SELECT.*"),

    CREATE("CREATE", "^CREATE(?!\\s+TABLE.*AS SELECT).*$"),

    DROP("DROP", "^DROP.*"),

    ALTER("ALTER", "^ALTER.*"),

    INSERT("INSERT", "^INSERT.*"),

    DESC("DESC", "^DESC.*"),

    DESCRIBE("DESCRIBE", "^DESCRIBE.*"),

    EXPLAIN("EXPLAIN", "^EXPLAIN.*"),

    USE("USE", "^USE.*"),

    SHOW("SHOW", "^SHOW.*"),

    LOAD("LOAD", "^LOAD.*"),

    UNLOAD("UNLOAD", "^UNLOAD.*"),

    SET("SET", "^SET.*"),

    RESET("RESET", "^RESET.*"),

    EXECUTE("EXECUTE", "^EXECUTE.*"),

    ADD_JAR("ADD_JAR", "^ADD\\s+JAR\\s+\\S+"),

    ADD("ADD", "^ADD\\s+CUSTOMJAR\\s+\\S+"),

    ADD_FILE("ADD_FILE", "^ADD\\s+FILE\\s+\\S+"),

    PRINT("PRINT", "^PRINT.*"),

    CTAS("CTAS", "^CREATE\\s.*AS\\sSELECT.*$"),

    WITH("WITH", "^WITH.*"),

    UNKNOWN("UNKNOWN", "^UNKNOWN.*");

    private String type;
    private Pattern pattern;

    SqlType(String type, String regexp) {
        this.type = type;
        this.pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean match(String statement) {
        return pattern.matcher(statement).matches();
    }
}
