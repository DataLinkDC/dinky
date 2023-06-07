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

import java.util.List;

/**
 * ExplainResult
 *
 * @since 2021/12/12 13:11
 */
public class ExplainResult {

    private boolean correct;
    private int total;
    private List<SqlExplainResult> sqlExplainResults;

    public ExplainResult(boolean correct, int total, List<SqlExplainResult> sqlExplainResults) {
        this.correct = correct;
        this.total = total;
        this.sqlExplainResults = sqlExplainResults;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SqlExplainResult> getSqlExplainResults() {
        return sqlExplainResults;
    }

    public void setSqlExplainResults(List<SqlExplainResult> sqlExplainResults) {
        this.sqlExplainResults = sqlExplainResults;
    }
}
