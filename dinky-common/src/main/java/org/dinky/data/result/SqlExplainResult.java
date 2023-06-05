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

package org.dinky.data.result;

import java.time.LocalDateTime;

/**
 * 解释结果
 *
 * @since 2021/6/7 22:06
 */
public class SqlExplainResult {

    private Integer index;
    private String type;
    private String sql;
    private String parse;
    private String explain;
    private String error;
    private boolean parseTrue;
    private boolean explainTrue;
    private LocalDateTime explainTime;

    public SqlExplainResult() {}

    public SqlExplainResult(
            Integer index,
            String type,
            String sql,
            String parse,
            String explain,
            String error,
            boolean parseTrue,
            boolean explainTrue,
            LocalDateTime explainTime) {
        this.index = index;
        this.type = type;
        this.sql = sql;
        this.parse = parse;
        this.explain = explain;
        this.error = error;
        this.parseTrue = parseTrue;
        this.explainTrue = explainTrue;
        this.explainTime = explainTime;
    }

    public static SqlExplainResult success(String type, String sql, String explain) {
        return new SqlExplainResult(
                1, type, sql, null, explain, null, true, true, LocalDateTime.now());
    }

    public static SqlExplainResult fail(String sql, String error) {
        return new SqlExplainResult(
                1, null, sql, null, null, error, false, false, LocalDateTime.now());
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getParse() {
        return parse;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isParseTrue() {
        return parseTrue;
    }

    public void setParseTrue(boolean parseTrue) {
        this.parseTrue = parseTrue;
    }

    public boolean isExplainTrue() {
        return explainTrue;
    }

    public void setExplainTrue(boolean explainTrue) {
        this.explainTrue = explainTrue;
    }

    public LocalDateTime getExplainTime() {
        return explainTime;
    }

    public void setExplainTime(LocalDateTime explainTime) {
        this.explainTime = explainTime;
    }

    @Override
    public String toString() {
        return String.format(
                "SqlExplainRecord{index=%d, type='%s', sql='%s', parse='%s', explain='%s', error='%s', "
                        + "parseTrue=%s, explainTrue=%s, explainTime=%s}",
                index, type, sql, parse, explain, error, parseTrue, explainTrue, explainTime);
    }
}
