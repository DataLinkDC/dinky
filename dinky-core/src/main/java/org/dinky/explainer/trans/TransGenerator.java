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

package org.dinky.explainer.trans;

import org.dinky.assertion.Asserts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TransGenerator
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class TransGenerator {

    private ObjectNode plan;

    public TransGenerator(ObjectNode plan) {
        this.plan = plan;
    }

    public static Trans getTrans(String pact) {
        switch (pact) {
            case OperatorTrans.TRANS_TYPE:
                return new OperatorTrans();
            case SourceTrans.TRANS_TYPE:
                return new SourceTrans();
            case SinkTrans.TRANS_TYPE:
                return new SinkTrans();
            default:
                return null;
        }
    }

    public List<Trans> translateTrans() {
        JsonNode nodes = plan.get("nodes");
        List<Trans> nodeList = new ArrayList<>();
        Map<Integer, Trans> nodemap = new HashMap<>();
        for (JsonNode node : nodes) {
            String pact = node.get("pact").asText();
            Trans trans = getTrans(pact);
            Asserts.checkNotNull(trans, "该转换无法被解析，原文如下：" + pact);
            trans.build(node);
            nodemap.put(trans.getId(), trans);
        }
        setParentId(nodemap);
        for (Map.Entry<Integer, Trans> entry : nodemap.entrySet()) {
            nodeList.add(entry.getValue());
        }
        return nodeList;
    }

    private void setParentId(Map<Integer, Trans> nodemap) {
        for (Map.Entry<Integer, Trans> entry : nodemap.entrySet()) {
            Trans trans = entry.getValue();
            List<Predecessor> predecessors = trans.getPredecessors();
            if (Asserts.isNullCollection(predecessors)) {
                continue;
            }
            for (int i = 0; i < predecessors.size(); i++) {
                nodemap.get(predecessors.get(i).getId()).setParentId(trans.getId());
            }
        }
    }
}
