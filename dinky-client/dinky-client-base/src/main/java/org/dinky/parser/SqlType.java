/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.parser;

import java.util.regex.Pattern;

/**
 * SqlType
 *
 * @since 2021/7/3 11:11
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

    SqlType(String type, String regrex) {
        this.type = type;
        this.pattern = Pattern.compile(regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
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
