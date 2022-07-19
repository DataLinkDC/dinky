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


package com.dlink.explainer.lineage;

/**
 * LineageRelation
 *
 * @author wenmo
 * @since 2022/3/15 23:00
 */
public class LineageRelation {
    private String id;
    private String srcTableId;
    private String tgtTableId;
    private String srcTableColName;
    private String tgtTableColName;

    public LineageRelation() {
    }

    public LineageRelation(String id, String srcTableId, String tgtTableId, String srcTableColName, String tgtTableColName) {
        this.id = id;
        this.srcTableId = srcTableId;
        this.tgtTableId = tgtTableId;
        this.srcTableColName = srcTableColName;
        this.tgtTableColName = tgtTableColName;
    }

    public static LineageRelation build(String id, String srcTableId, String tgtTableId, String srcTableColName, String tgtTableColName) {
        return new LineageRelation(id, srcTableId, tgtTableId, srcTableColName, tgtTableColName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrcTableId() {
        return srcTableId;
    }

    public void setSrcTableId(String srcTableId) {
        this.srcTableId = srcTableId;
    }

    public String getTgtTableId() {
        return tgtTableId;
    }

    public void setTgtTableId(String tgtTableId) {
        this.tgtTableId = tgtTableId;
    }

    public String getSrcTableColName() {
        return srcTableColName;
    }

    public void setSrcTableColName(String srcTableColName) {
        this.srcTableColName = srcTableColName;
    }

    public String getTgtTableColName() {
        return tgtTableColName;
    }

    public void setTgtTableColName(String tgtTableColName) {
        this.tgtTableColName = tgtTableColName;
    }
}
