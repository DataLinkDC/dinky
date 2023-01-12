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

import java.util.Objects;

/**
 * NodeRel
 *
 * @author wenmo
 * @since 2021/6/22
 */
public class NodeRel {

    private Integer preId;
    private Integer sufId;

    public NodeRel(Integer preId, Integer sufId) {
        this.preId = preId;
        this.sufId = sufId;
    }

    public Integer getPreId() {
        return preId;
    }

    public void setPreId(Integer preId) {
        this.preId = preId;
    }

    public Integer getSufId() {
        return sufId;
    }

    public void setSufId(Integer sufId) {
        this.sufId = sufId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeRel nodeRel = (NodeRel) o;
        return Objects.equals(preId, nodeRel.preId) && Objects.equals(sufId, nodeRel.sufId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(preId, sufId);
    }
}
