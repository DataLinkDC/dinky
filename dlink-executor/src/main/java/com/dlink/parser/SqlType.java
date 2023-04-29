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

package com.dlink.parser;

/**
 * SqlType
 *
 * @author wenmo
 * @since 2021/7/3 11:11
 */
public enum SqlType {

    SELECT("SELECT"), CREATE("CREATE"), DROP("DROP"), ALTER("ALTER"), INSERT("INSERT"), DESC("DESC"), DESCRIBE(
            "DESCRIBE"), EXPLAIN("EXPLAIN"), USE("USE"), SHOW("SHOW"), LOAD("LOAD"), UNLOAD("UNLOAD"), SET(
                    "SET"), RESET("RESET"), EXECUTE("EXECUTE"), ADD("ADD"), WITH("WITH"), UNKNOWN("UNKNOWN"),;

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

    public boolean equalsValue(String value) {
        return type.equalsIgnoreCase(value);
    }

    public boolean isInsert() {
        return type.equals("INSERT");
    }

}
