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

package org.dinky.explainer.ca;

import java.util.List;

/**
 * TableCAResult
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class TableCAResult {
    private String sinkName;
    private List<ICA> sourceTableCAS;
    private ICA sinkTableCA;

    public TableCAResult(TableCAGenerator generator) {
        this.sourceTableCAS = generator.getSourceTableCAS();
        this.sinkTableCA = generator.getSinkTableCA();
        this.sinkName = generator.getSinkTableName();
    }

    public String getSinkName() {
        return sinkName;
    }

    public void setSinkName(String sinkName) {
        this.sinkName = sinkName;
    }

    public List<ICA> getSourceTableCAS() {
        return sourceTableCAS;
    }

    public void setSourceTableCAS(List<ICA> sourceTableCAS) {
        this.sourceTableCAS = sourceTableCAS;
    }

    public ICA getSinkTableCA() {
        return sinkTableCA;
    }

    public void setSinkTableCA(ICA sinkTableCA) {
        this.sinkTableCA = sinkTableCA;
    }
}
