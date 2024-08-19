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

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

/**
 * SqlType
 *
 * @since 2021/7/3 11:11
 */
public enum SqlType {
    SELECT("SELECT", "^SELECT.*", SqlCategory.DQL),

    CREATE("CREATE", "^CREATE(?!\\s+TABLE.*AS SELECT).*$", SqlCategory.DDL),

    DROP("DROP", "^DROP.*", SqlCategory.DDL),

    ALTER("ALTER", "^ALTER.*", SqlCategory.DDL),

    INSERT("INSERT", "^INSERT.*", SqlCategory.DML),

    DESC("DESC", "^DESC.*", SqlCategory.DDL),

    DESCRIBE("DESCRIBE", "^DESCRIBE.*", SqlCategory.DDL),

    EXPLAIN("EXPLAIN", "^EXPLAIN.*", SqlCategory.DDL),

    USE("USE", "^USE.*", SqlCategory.DDL),

    SHOW("SHOW", "^SHOW.*", SqlCategory.DDL),

    LOAD("LOAD", "^LOAD.*", SqlCategory.DDL),

    UNLOAD("UNLOAD", "^UNLOAD.*", SqlCategory.DDL),

    SET("SET", "^SET.*", SqlCategory.DDL),

    RESET("RESET", "^RESET.*", SqlCategory.DDL),

    EXECUTE("EXECUTE", "^EXECUTE.*", SqlCategory.DQL),

    ADD_JAR("ADD_JAR", "^ADD\\s+JAR\\s+\\S+", SqlCategory.DDL),
    ADD("ADD", "^ADD\\s+CUSTOMJAR\\s+\\S+", SqlCategory.DDL),
    ADD_FILE("ADD_FILE", "^ADD\\s+FILE\\s+\\S+", SqlCategory.DDL),

    PRINT("PRINT", "^PRINT.*", SqlCategory.DQL),

    CTAS("CTAS", "^CREATE\\s.*AS\\sSELECT.*$", SqlCategory.DDL),

    WITH("WITH", "^WITH.*", SqlCategory.DQL),

    UNKNOWN("UNKNOWN", "^UNKNOWN.*", SqlCategory.UNKNOWN);

    private String type;
    private Pattern pattern;
    private SqlCategory category;

    private static final List<SqlType> TRANS_SQL_TYPES =
            Lists.newArrayList(INSERT, SELECT, WITH, SHOW, DESCRIBE, DESC, CTAS);

    SqlType(String type, String regrex, SqlCategory category) {
        this.type = type;
        this.pattern = Pattern.compile(regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        this.category = category;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public SqlCategory getCategory() {
        return category;
    }

    public boolean match(String statement) {
        return pattern.matcher(statement).matches();
    }

    public static List<SqlType> getTransSqlTypes() {
        return TRANS_SQL_TYPES;
    }
}
